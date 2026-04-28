package com.golapp.attendances.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.golapp.attendances.data.BuildConfig
import com.golapp.attendances.common.Constants.BD_NAME
import com.golapp.attendances.common.Constants.PREFERENCES_NAME
import com.golapp.attendances.data.datasources.local.AttendancesLocalDataSourceImpl
import com.golapp.attendances.data.datasources.local.ClassDayLocalDataSourceImpl
import com.golapp.attendances.data.datasources.local.GroupsLocalDataSourceImpl
import com.golapp.attendances.data.datasources.local.PlayersLocalDataSourceImpl
import com.golapp.attendances.data.datasources.local.StoreLocalDataSourceImpl
import com.golapp.attendances.data.local.AttendancesDB
import com.golapp.attendances.data.local.dao.AttendanceDao
import com.golapp.attendances.data.local.dao.ClassDayDao
import com.golapp.attendances.data.local.dao.GroupDao
import com.golapp.attendances.data.local.dao.PlayerDao
import com.golapp.attendances.data.local.datasources.AttendancesLocalDataSource
import com.golapp.attendances.data.local.datasources.ClassDayLocalDataSource
import com.golapp.attendances.data.local.datasources.GroupsLocalDataSource
import com.golapp.attendances.data.local.datasources.PlayersLocalDataSource
import com.golapp.attendances.data.local.datasources.StoreLocalDataSource
import com.golapp.attendances.data.remote.GolappAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import java.util.concurrent.Executors
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalModule {
    @Provides
    @Singleton
    fun providePreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { emptyPreferences() }
            ),
            migrations = listOf(SharedPreferencesMigration((context), PREFERENCES_NAME)),
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            produceFile = { context.preferencesDataStoreFile(PREFERENCES_NAME) }
        )
    }

    @Provides
    @Singleton
    fun provideGolAppDatabase(@ApplicationContext context: Context): AttendancesDB {
        val builder = Room.databaseBuilder(
            context = context,
            klass = AttendancesDB::class.java,
            name = BD_NAME
        )
            .fallbackToDestructiveMigration(false)

        if (BuildConfig.DEBUG) {
            builder.setQueryCallback({ sqlQuery, bindArgs ->
                println("SQL Query: $sqlQuery SQL Args: $bindArgs")
            }, Executors.newSingleThreadExecutor())
        }

        return builder.build()
    }

    @Provides
    fun provideAttendanceDao(db: AttendancesDB) = db.attendanceDao()

    @Provides
    fun provideClassDayDao(db: AttendancesDB) = db.classDayDao()

    @Provides
    fun provideGroupDao(db: AttendancesDB) = db.groupDao()

    @Provides
    fun providePlayerDao(db: AttendancesDB) = db.playerDao()

    @Provides
    fun provideAttendancesLocalDatasource(attendanceDao: AttendanceDao): AttendancesLocalDataSource =
        AttendancesLocalDataSourceImpl(attendanceDao)

    @Provides
    fun provideGroupsLocalDatasource(groupDao: GroupDao, playerDao: PlayerDao): GroupsLocalDataSource =
        GroupsLocalDataSourceImpl(groupDao, playerDao)

    @Provides
    fun providePlayerLocalDatasource(playerDao: PlayerDao): PlayersLocalDataSource =
        PlayersLocalDataSourceImpl(playerDao)

    @Provides
    fun provideClassDayLocalDatasource(classDayDao: ClassDayDao): ClassDayLocalDataSource =
        ClassDayLocalDataSourceImpl(classDayDao)

    @Provides
    fun provideStoreLocalDatasource(
        preferenceDataStore: DataStore<Preferences>,
        api: GolappAPI
    ): StoreLocalDataSource =
        StoreLocalDataSourceImpl(preferenceDataStore, api)
}
