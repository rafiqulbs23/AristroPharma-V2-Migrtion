package com.aristopharma.dev.v2.di

import com.aristopharma.dev.v2.features.login.data.repository.AuthRepositoryImpl
import com.aristopharma.dev.v2.features.login.domain.repository.AuthRepository
import com.aristopharma.dev.v2.features.dashboard.data.repository.DashboardRepositoryImpl
import com.aristopharma.dev.v2.features.dashboard.domain.repository.DashboardRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class AuthRepositoryModule {

    @Binds
    @Singleton
    abstract fun provideAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun provideDashboardRepository(
        dashboardRepositoryImpl: DashboardRepositoryImpl
    ): DashboardRepository
}