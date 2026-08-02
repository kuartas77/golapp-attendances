package com.golapp.attendances.data.repositories

import androidx.room.withTransaction
import com.golapp.attendances.core.coroutines.rethrowIfCancellation
import com.golapp.attendances.data.datasources.GroupRemoteDataSource
import com.golapp.attendances.data.local.database.AttendancesDB
import com.golapp.attendances.data.local.database.daos.ClassDayDao
import com.golapp.attendances.data.local.database.daos.GroupDao
import com.golapp.attendances.data.local.database.daos.PlayerDao
import com.golapp.attendances.data.local.database.entities.GroupWithPlayersEntity
import com.golapp.attendances.data.mappers.toDomain
import com.golapp.attendances.data.mappers.toGraphEntities
import com.golapp.attendances.core.di.IoDispatcher
import com.golapp.attendances.domain.models.ClassDay
import com.golapp.attendances.domain.models.Group
import com.golapp.attendances.domain.models.GroupWithClassDays
import com.golapp.attendances.domain.models.GroupWithClassPlayers
import com.golapp.attendances.domain.models.GroupWithPlayers
import com.golapp.attendances.domain.repositories.GroupRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class GroupRepositoryImpl @Inject constructor(
    private val db: AttendancesDB,
    private val groupDao: GroupDao,
    private val playerDao: PlayerDao,
    private val classDayDao: ClassDayDao,
    private val remote: GroupRemoteDataSource,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : GroupRepository {
    // ---------------------------
    // Sync al ingresar (asignados)
    // ---------------------------
    override suspend fun syncAssignedGroups() = withContext(ioDispatcher) {
        val remoteSnapshot = try {
            remote.fetchAllGroupsSnapshot()
        } catch (e: Exception) {
            e.rethrowIfCancellation()
            Timber.e(e, "syncAssignedGroups failed: ${e.message}")
            return@withContext
        }

        // Si el backend retorna vacío porque no tiene grupos
        // borrado completo (el usuario no tiene asignaciones)
        if (remoteSnapshot.isEmpty()) {
            db.withTransaction { groupDao.deleteAllGroups() }
            return@withContext
        }

        val graphs = remoteSnapshot.map { it.toGraphEntities() }
        val assignedGroupIds = graphs.map { it.group.id }

        db.withTransaction {
            groupDao.upsertGroups(graphs.map { it.group })
            classDayDao.upsertClassDays(graphs.flatMap { it.classDays })
            playerDao.upsertPlayers(graphs.flatMap { it.players }.distinctBy { it.playerId })

            // elimina grupos que ya no están asignados (CASCADE limpia class_days/players por FK)
            groupDao.deleteGroupsNotIn(assignedGroupIds)
        }
    }

    // ---------------------------
    // UI: grupos (solo DB)
    // ---------------------------
    override fun observeGroups(): Flow<List<Group>> =
        groupDao.observeGroups()
            .map { list -> list.map { it.toDomain() } }

    // ---------------------------
    // UI: class days por grupo
    // ---------------------------
    override fun observeClassDaysByGroup(groupId: Int): Flow<List<ClassDay>> =
        classDayDao.observeClassDaysByGroup(groupId)
            .map { list -> list.map { it.toDomain() } }

    // ---------------------------
    // Lectura: grupo con players
    // ---------------------------
    override suspend fun getGroupWhitPlayersById(groupId: Int): GroupWithPlayers =
        withContext(ioDispatcher) {
            val entity = db.withTransaction {
                val group = groupDao.getGroupById(groupId)
                    ?: throw IllegalStateException("Group not found: $groupId")
                val players = playerDao.getPlayersByGroupIdOrderByCategoryNumber(groupId)

                GroupWithPlayersEntity(
                    group = group,
                    players = players
                )
            }
            entity.toDomain()
        }

    // ---------------------------
    // UI: grupos con classdays por mes (solo DB)
    // ---------------------------
    override fun observeGroupsWithClassDaysOnMonth(month: Int): Flow<List<GroupWithClassDays>> =
        groupDao.observeGroupsWithClassDaysOnMonth(month)
            .map { list -> list.map { it.toDomain(filterMonth = month) } }

    // ---------------------------
    // Sync helpers (tu interfaz)
    // ---------------------------
    override suspend fun fetchAllGroupsSnapshot(): List<GroupWithClassPlayers> =
        withContext(ioDispatcher) { remote.fetchAllGroupsSnapshot() }

    override suspend fun upsertGroupsWithClassPlayers(items: List<GroupWithClassPlayers>) =
        withContext(ioDispatcher) {
            if (items.isEmpty()) return@withContext
            val graphs = items.map { it.toGraphEntities() }

            db.withTransaction {
                groupDao.upsertGroups(graphs.map { it.group })
                classDayDao.upsertClassDays(graphs.flatMap { it.classDays })
                playerDao.upsertPlayers(graphs.flatMap { it.players }.distinctBy { it.playerId })
            }
        }

    override suspend fun syncGroupsIfEmpty(month: Int) =
        withContext(ioDispatcher) {
            val local = groupDao.getGroupsWithClassDaysOnMonth(month)
            if (local.isNotEmpty()) return@withContext

            Timber.d("syncGroupsIfEmpty: local empty for month=$month, fetching remote...")

            val remoteSnapshot = try {
                remote.fetchAllGroupsSnapshot()
            } catch (e: Exception) {
                e.rethrowIfCancellation()
                Timber.e(e, "syncGroupsIfEmpty failed (remote)")
                return@withContext
            }

            if (remoteSnapshot.isEmpty()) return@withContext
            upsertGroupsWithClassPlayers(remoteSnapshot)
        }
}
