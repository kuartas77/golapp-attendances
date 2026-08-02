package com.golapp.attendances.core.di

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
import com.golapp.attendances.core.common.NetworkMonitor
import com.golapp.attendances.data.local.datastore.SessionManager
import com.golapp.attendances.data.remote.GolappAPI
import com.golapp.attendances.data.remote.RefreshApi
import com.golapp.attendances.data.remote.auth.TokenAuthenticator
import com.golapp.attendances.data.remote.interceptors.AuthCoilInterceptor
import com.golapp.attendances.data.remote.interceptors.AuthInterceptor
import com.golapp.attendances.data.remote.interceptors.LoggingInterceptor
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
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): GolappAPI {
        return retrofit.create(GolappAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideRefreshApi(): RefreshApi {
        // Refresh cannot use the authenticated client: a 401 would recursively
        // invoke TokenAuthenticator and leave the original request waiting.
        val refreshClient = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
            })
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_URL)
            .client(refreshClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RefreshApi::class.java)
    }

    @Provides
    @Singleton
    fun provideNetworkMonitor(@ApplicationContext context: Context): NetworkMonitor =
        NetworkMonitor(context)

    @Provides
    @Singleton
    fun provideAuthInterceptor(dataStore: SessionManager): AuthInterceptor =
        AuthInterceptor(dataStore)

    @Provides
    @Singleton
    fun provideAuthCoilInterceptor(dataStore: SessionManager): AuthCoilInterceptor =
        AuthCoilInterceptor(dataStore)

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): LoggingInterceptor = LoggingInterceptor()

    @Provides
    @Singleton
    fun provideTokenAuthenticator(
        sessionManager: SessionManager
    ): TokenAuthenticator = TokenAuthenticator(sessionManager)

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        tokenAuthenticator: TokenAuthenticator
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
            })
            .addInterceptor(authInterceptor)
            .authenticator(tokenAuthenticator)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideGolappImageLoader(
        @ApplicationContext context: Context,
        headerCoilInterceptor: AuthCoilInterceptor
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
                        callFactory = {
                            OkHttpClient().newBuilder()
                                .addNetworkInterceptor(headerCoilInterceptor)
                                .build()
                        }
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