package com.example.android.politicalpreparedness.di

import android.content.Context
import androidx.room.Room
import com.example.android.politicalpreparedness.data.database.ElectionDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModules {
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext appContext: Context,
    ): ElectionDatabase {
        return Room.databaseBuilder(
            appContext.applicationContext,
            ElectionDatabase::class.java,
            "election_database",
        )
            .fallbackToDestructiveMigration()
            .build()
    }
}
