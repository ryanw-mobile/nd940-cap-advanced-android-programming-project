package com.example.android.politicalpreparedness.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.android.politicalpreparedness.data.network.models.Election
import com.example.android.politicalpreparedness.data.network.models.FollowedElection

@Database(entities = [Election::class, FollowedElection::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class ElectionDatabase: RoomDatabase() {

    // DAOs
    abstract val electionDao: ElectionDao
}