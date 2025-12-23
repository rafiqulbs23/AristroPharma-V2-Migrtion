package com.aristopharma.dev.v2.di

import android.content.Context
import com.aristopharma.dev.v2.core.utils.sharedPref.Prefs
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PreferenceModule {

    @Provides
    @Singleton
    fun providePrefs(@ApplicationContext context: Context): Prefs {
        return Prefs(context)
    }
}
