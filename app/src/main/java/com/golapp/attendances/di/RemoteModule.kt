package com.golapp.attendances.di

import android.content.Context
import androidx.tracing.trace
import coil3.ImageLoader
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.memory.MemoryCache
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.request.CachePolicy
import coil3.request.crossfade
import coil3.util.DebugLogger
import com.golapp.attendances.BuildConfig
import com.golapp.attendances.common.NetworkMonitor
import com.golapp.attendances.data.local.datastore.SessionManager
import com.golapp.attendances.data.remote.GolappAPI
import com.golapp.attendances.data.remote.RefreshApi
import com.golapp.attendances.data.remote.auth.TokenAuthenticator
import com.golapp.attendances.data.remote.interceptors.AuthCoilInterceptor
import com.golapp.attendances.data.remote.interceptors.AuthHeaderInterceptor
import com.golapp.attendances.data.remote.interceptors.NetworkMonitorInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RemoteModule {

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder().create()

    @Provides
    @Singleton
    fun provideNetworkMonitor(@ApplicationContext context: Context): NetworkMonitor =
        NetworkMonitor(context)

    @Provides
    @Singleton
    fun provideAuthHeaderInterceptor(
        sessionManager: SessionManager
    ): AuthHeaderInterceptor = AuthHeaderInterceptor(sessionManager)

    @Provides
    @Singleton
    fun provideTokenAuthenticator(
        sessionManager: SessionManager
    ): TokenAuthenticator = TokenAuthenticator(sessionManager)

    @Provides
    @Singleton
    fun provideHeaderCoilInterceptor(
        sessionManager: SessionManager
    ): AuthCoilInterceptor = AuthCoilInterceptor(sessionManager)

    @Provides
    @Singleton
    fun provideNetworkMonitorInterceptor(
        networkMonitor: NetworkMonitor
    ): NetworkMonitorInterceptor = NetworkMonitorInterceptor(networkMonitor)

    @Provides
    @Singleton
    @ApiOkHttp
    fun provideOkHttpClient(
        authHeaderInterceptor: AuthHeaderInterceptor,
        networkMonitorInterceptor: NetworkMonitorInterceptor,
        tokenAuthenticator: TokenAuthenticator
    ): OkHttpClient = trace("GolappOkHttpClient") {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
            })
            .addInterceptor(authHeaderInterceptor)
            .addInterceptor(networkMonitorInterceptor)
            .authenticator(tokenAuthenticator)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @RefreshOkHttp
    fun provideRefreshOkHttpClient(
        networkMonitorInterceptor: NetworkMonitorInterceptor
    ): OkHttpClient = trace("GolappRefreshOkHttpClient") {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
            })
            .addInterceptor(networkMonitorInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideGolappAPI(
        @ApiOkHttp okHttpClient: OkHttpClient,
        gson: Gson
    ): GolappAPI {
        return trace("GolappNetwork") {
            Retrofit.Builder()
                .baseUrl(BuildConfig.API_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build()
                .create(GolappAPI::class.java)
        }
    }

    @Provides
    @Singleton
    fun provideRefreshApi(
        @RefreshOkHttp okHttpClient: OkHttpClient,
        gson: Gson
    ): RefreshApi {
        return trace("GolappRefreshNetwork") {
            Retrofit.Builder()
                .baseUrl(BuildConfig.API_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build()
                .create(RefreshApi::class.java)
        }
    }

    @Provides
    @Singleton
    @CoilOkHttp
    fun provideCoilOkHttpClient(
        headerCoilInterceptor: AuthCoilInterceptor,
        networkMonitorInterceptor: NetworkMonitorInterceptor
    ): OkHttpClient = trace("GolappCoilOkHttpClient") {
        OkHttpClient.Builder()
            .addInterceptor(headerCoilInterceptor)
            .addInterceptor(networkMonitorInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideGolappImageLoader(
        @ApplicationContext context: Context,
        @CoilOkHttp coilOkHttpClient: OkHttpClient
    ): ImageLoader = trace("GolappImageLoader") {
        ImageLoader.Builder(context)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizePercent(context, 0.1)
                    .strongReferencesEnabled(true)
                    .build()
            }
            .diskCachePolicy(CachePolicy.ENABLED)
            .diskCache {
                DiskCache.Builder()
                    .maxSizePercent(0.02)
                    .directory(context.cacheDir.resolve("image_cache"))
                    .build()
            }
            .components {
                add(
                    OkHttpNetworkFetcherFactory(
                        callFactory = { coilOkHttpClient }
                    )
                )
            }
            .apply {
                if (BuildConfig.DEBUG) logger(DebugLogger())
            }
            .crossfade(true)
            .build()
    }
}