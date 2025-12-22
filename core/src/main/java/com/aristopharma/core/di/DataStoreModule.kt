package com.aristopharma.core.di

import android.content.Context
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import com.aristopharma.core.utils.DataStoreManager

@InstallIn(SingletonComponent::class)
@Module
object DataStoreModule {

    // Provide DataStoreManager as a singleton
     @Provides
     fun provideDataStoreManager(@ApplicationContext context: Context): DataStoreManager {
         return DataStoreManager(context)
     }
}