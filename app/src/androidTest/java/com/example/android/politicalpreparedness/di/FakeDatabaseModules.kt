/*
 * Copyright (c) 2021. Ryan Wong (hello@ryanwong.co.uk)
 *
 */

package com.example.android.politicalpreparedness.di

import android.content.Context
import androidx.room.Room
import com.example.android.politicalpreparedness.data.database.ElectionDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DatabaseModules::class],
)
object FakeDatabaseModules {
    // Using an in-memory database for testing, because it doesn't survive killing the process.
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext appContext: Context,
    ): ElectionDatabase {
        return Room.inMemoryDatabaseBuilder(
            appContext.applicationContext,
            ElectionDatabase::class.java,
        ).allowMainThreadQueries().build()
    }
}
