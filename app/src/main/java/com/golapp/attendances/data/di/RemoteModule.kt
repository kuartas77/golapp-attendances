package com.golapp.attendances.data.di

import android.content.Context
import androidx.compose.ui.util.trace
import coil3.ImageLoader
import coil3.memory.MemoryCache
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.request.CachePolicy
import coil3.request.crossfade
import coil3.util.DebugLogger
import com.golapp.attendances.BuildConfig
import com.golapp.attendances.common.remote.ConnectivityManagerNetworkMonitor
import com.golapp.attendances.common.remote.NetworkMonitor
import com.golapp.attendances.data.datasources.remote.AttendancesRemoteDataSourceImpl
import com.golapp.attendances.data.datasources.remote.AuthRemoteDataSourceImpl
import com.golapp.attendances.data.datasources.remote.GroupsRemoteDataSourceImpl
import com.golapp.attendances.data.local.datasources.StoreLocalDataSource
import com.golapp.attendances.data.remote.GolappAPI
import com.golapp.attendances.data.remote.datasources.AttendancesRemoteDataSource
import com.golapp.attendances.data.remote.datasources.AuthRemoteDataSource
import com.golapp.attendances.data.remote.datasources.GroupsRemoteDataSource
import com.golapp.attendances.data.remote.interceptors.AuthCoilInterceptor
import com.golapp.attendances.data.remote.interceptors.AuthHeaderInterceptor
import com.golapp.attendances.data.remote.interceptors.NetworkMonitorInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RemoteModule {
    @Provides
    @Singleton
    fun provideNetworkMonitor(@ApplicationContext context: Context): NetworkMonitor =
        ConnectivityManagerNetworkMonitor(context, Dispatchers.IO)

    @Provides
    @Singleton
    fun providesHeadersInterceptor(storeLocalDatasource: StoreLocalDataSource): AuthHeaderInterceptor =
        AuthHeaderInterceptor(storeLocalDatasource)

    @Provides
    @Singleton
    fun providesHeaderCoilInterceptor(storeLocalDatasource: StoreLocalDataSource): AuthCoilInterceptor =
        AuthCoilInterceptor(storeLocalDatasource)

    @Provides
    @Singleton
    fun providesNetworkMonitorInterceptor(networkMonitor: NetworkMonitor): NetworkMonitorInterceptor =
        NetworkMonitorInterceptor(networkMonitor)

    @Provides
    @Singleton
    fun okHttpCallFactory(
        authHeaderInterceptor: AuthHeaderInterceptor,
        networkMonitorInterceptor: NetworkMonitorInterceptor
    ): Call.Factory = trace("GolappOkHttpClient") {
        OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor()
                    .apply {
                        if (BuildConfig.DEBUG) {
                            setLevel(HttpLoggingInterceptor.Level.BODY)
                        } else {
                            setLevel(HttpLoggingInterceptor.Level.NONE)
                        }
                    },
            )
            .addInterceptor(authHeaderInterceptor)
            .addInterceptor(networkMonitorInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideGolappAPI(
        okhttpCallFactory: dagger.Lazy<Call.Factory>
    ): GolappAPI {
        return trace("GolappNetwork") {
            Retrofit.Builder()
                .baseUrl(BuildConfig.API_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .callFactory { okhttpCallFactory.get().newCall(it) }
                .build()
                .create(GolappAPI::class.java)
        }
    }

    @Provides
    @Singleton
    fun provideGolappImageLoader(
        @ApplicationContext context: Context,
        headerCoilInterceptor: AuthCoilInterceptor
    ): ImageLoader = trace("GolappImageLoader") {
        ImageLoader.Builder(context)
            .crossfade(true)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizePercent(context, 0.25)
                    .strongReferencesEnabled(true)
                    .build()
            }
            .diskCachePolicy(CachePolicy.ENABLED)
            .components {
                add(
                    OkHttpNetworkFetcherFactory(
                        OkHttpClient.Builder()
                            .addNetworkInterceptor(
                                headerCoilInterceptor
                            ).build()
                    )
                )
            }
            .apply {
                if (BuildConfig.DEBUG) {
                    logger(DebugLogger())
                }
            }
            .build()
    }

    @Provides
    fun provideAttendancesRemoteDatasource(api: GolappAPI): AttendancesRemoteDataSource =
        AttendancesRemoteDataSourceImpl(api)

    @Provides
    fun provideGroupsRemoteDatasource(api: GolappAPI): GroupsRemoteDataSource =
        GroupsRemoteDataSourceImpl(api)

    @Provides
    fun provideAuthRemoteDatasource(api: GolappAPI): AuthRemoteDataSource =
        AuthRemoteDataSourceImpl(api)
}
