package com.helloyatri.di.module

import android.util.Log
import com.helloyatri.core.Session
import com.helloyatri.exception.AuthenticationException
import com.helloyatri.exception.ServerError
import com.helloyatri.network.APIFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module(includes = [(ApplicationModule::class)])
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Singleton
    @Provides
    internal fun getOkHttpClient(
        @Named("header") headerInterceptor: Interceptor,
        @Named("pre_validation") networkInterceptor: Interceptor,
    ): OkHttpClient {
           val httpLoggingInterceptor = HttpLoggingInterceptor()
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            return OkHttpClient.Builder()
            .addInterceptor(headerInterceptor)
             .addInterceptor(httpLoggingInterceptor)
            .addNetworkInterceptor(networkInterceptor)
            .connectTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(1, TimeUnit.MINUTES)
            .readTimeout(1, TimeUnit.MINUTES)
            .build()
    }

    @Provides
    @Singleton
    @Named("header")
    internal fun getHeaderInterceptor(session: Session): Interceptor {
        return Interceptor { chain ->
            Log.e("TAG",session.userSession)
            val build = chain.request().newBuilder().addHeader(Session.API_KEY, session.apiKey)
                .addHeader(Session.USER_SESSION, "Bearer " + session.userSession)
                .header(Session.LANGUAGE, session.language).build()
            chain.proceed(build)
        }
    }

    @Provides
    @Singleton
    @Named("pre_validation")
    internal fun provideNetworkInterceptor(): Interceptor {
        return Interceptor { chain ->
            val response = chain.proceed(chain.request())
            val code = response.code
//            Log.e("RES",response.body.toString())
           /* if (code >= 500) throw ServerError("Unknown server error", response.body!!.string())
            else if (code == 401 || code == 403) throw AuthenticationException()*/
            response
        }
    }

    @Provides
    @Singleton
    internal fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder().baseUrl(APIFactory.getHttpUrl()).client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create()).build()
    }
}