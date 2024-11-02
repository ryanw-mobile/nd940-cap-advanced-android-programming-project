package com.example.android.politicalpreparedness.di

import com.example.android.politicalpreparedness.data.database.ElectionDao
import com.example.android.politicalpreparedness.data.database.ElectionDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class AppModules {
    @Provides
    @Singleton
    fun provideElectionDao(database: ElectionDatabase): ElectionDao {
        return database.electionDao
    }
}
