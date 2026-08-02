package com.golapp.attendances.core.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.work.WorkManager
import com.golapp.attendances.data.local.datastore.SessionManager
import com.golapp.attendances.data.remote.RefreshApi
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context = context

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder().create()

    @Provides
    @Singleton
    fun provideAppDataStore(
        preferenceDataStore: DataStore<Preferences>,
        refreshApi: Lazy<RefreshApi>,
        gson: Gson
    ): SessionManager = SessionManager(
        preferenceDatasource = preferenceDataStore,
        refreshApi = refreshApi,
        gson = gson
    )

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }
}