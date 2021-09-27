package com.example.android.politicalpreparedness.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.android.politicalpreparedness.network.models.Election

@Dao
interface ElectionDao {

    //COMPLETED: Add insert query
    @Insert
    suspend fun insert(election: Election)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(elections: List<Election>)

    //COMPLETED: Add select all election query
    @Query("SELECT * FROM election_table")
    fun getAllElections(): LiveData<List<Election>>

    //COMPLETED: Add select single election query
    @Query("SELECT * FROM election_table WHERE id = :electionId")
    fun getElection(electionId: Int): LiveData<Election>

    //COMPLETED: Add delete query
    @Query("DELETE FROM election_table WHERE id = :electionId")
    suspend fun delete(electionId: Int)

    //COMPLETED: Add clear query
    @Query("DELETE FROM election_table")
    suspend fun clear()


    // Extended functions to manage followed elections
    @Query("INSERT INTO followed_election VALUES(:electionId)")
    suspend fun followElection(electionId: Int)

    @Query("SELECT * FROM election_table WHERE id IN (select ID from followed_election)")
    fun getFollowedElections(): LiveData<List<Election>>

    @Query("select exists(select * from followed_election where id = :electionId)")
    fun isFollowedElection(electionId: Int): LiveData<Boolean>

    @Query("DELETE FROM followed_election WHERE id = :electionId")
    suspend fun unfollowElection(electionId: Int)

    @Query("DELETE FROM followed_election")
    suspend fun clearFollowedElections()

}