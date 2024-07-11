package com.helloyatri.di.module

import com.helloyatri.network.ApiDataSource
import com.helloyatri.network.AuthRepo
import com.helloyatri.network.AuthService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiServiceModule {

    @Provides
    @Singleton
    fun getAuthRepo(apiDataSource: ApiDataSource) : AuthRepo {
        return apiDataSource
    }

    @Provides
    @Singleton
    fun getAuthService(retrofit: Retrofit) : AuthService {
        return retrofit.create(AuthService::class.java)
    }
}