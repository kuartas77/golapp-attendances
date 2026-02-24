package com.golapp.attendances.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.golapp.attendances.common.Constants.PREFERENCES_NAME
import com.golapp.attendances.data.local.database.AttendancesDB
import com.golapp.attendances.data.local.database.daos.AttendanceDao
import com.golapp.attendances.data.local.database.daos.ClassDayDao
import com.golapp.attendances.data.local.database.daos.GroupDao
import com.golapp.attendances.data.local.database.daos.PlayerDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import java.util.prefs.Preferences
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDb(@ApplicationContext context: Context): AttendancesDB =
        Room.databaseBuilder(context, AttendancesDB::class.java, "attendances.db")
            // .addMigrations(MIGRATION_1_2, ...)
            .fallbackToDestructiveMigration()
            .build()

    @Provides fun provideGroupDao(db: AttendancesDB): GroupDao = db.groupDao()
    @Provides fun provideClassDayDao(db: AttendancesDB): ClassDayDao = db.classDayDao()
    @Provides fun providePlayerDao(db: AttendancesDB): PlayerDao = db.playerDao()
    @Provides fun provideAttendanceDao(db: AttendancesDB): AttendanceDao = db.attendanceDao()

    @Provides
    @Singleton
    fun providePreferencesDataStore(@ApplicationContext context: Context): DataStore<androidx.datastore.preferences.core.Preferences> {
        return PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { emptyPreferences() }
            ),
            migrations = listOf(SharedPreferencesMigration((context), PREFERENCES_NAME)),
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            produceFile = { context.preferencesDataStoreFile(PREFERENCES_NAME) }
        )
    }
}