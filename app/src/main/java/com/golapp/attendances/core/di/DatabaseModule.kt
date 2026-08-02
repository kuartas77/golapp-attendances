package com.golapp.attendances.core.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.golapp.attendances.BuildConfig
import com.golapp.attendances.core.common.Constants.PREFERENCES_NAME
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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import timber.log.Timber
import java.util.concurrent.Executors
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDb(@ApplicationContext context: Context): AttendancesDB {
        val builder = Room.databaseBuilder(
            context,
            AttendancesDB::class.java,
            "attendances.db"
        ).fallbackToDestructiveMigration(false)
            .addMigrations()
        if (BuildConfig.DEBUG) {
            builder.setQueryCallback({ sqlQuery, _ ->
                Timber.tag("Room").d("SQL Query: $sqlQuery")
            }, Executors.newSingleThreadExecutor())
        }
        return builder.build()
    }

    @Provides
    @Singleton
    fun providePreferencesDataStore(
        @ApplicationContext context: Context,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
        @ApplicationExceptionHandler exceptionHandler: CoroutineExceptionHandler,
    ): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { emptyPreferences() }
            ),
            migrations = listOf(SharedPreferencesMigration((context), PREFERENCES_NAME)),
            scope = CoroutineScope(
                SupervisorJob() + ioDispatcher + exceptionHandler + CoroutineName("preferences-datastore")
            ),
            produceFile = { context.preferencesDataStoreFile(PREFERENCES_NAME) }
        )
    }

    @Provides
    fun provideGroupDao(db: AttendancesDB): GroupDao = db.groupDao()

    @Provides
    fun provideClassDayDao(db: AttendancesDB): ClassDayDao = db.classDayDao()

    @Provides
    fun providePlayerDao(db: AttendancesDB): PlayerDao = db.playerDao()

    @Provides
    fun provideAttendanceDao(db: AttendancesDB): AttendanceDao = db.attendanceDao()
}