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
    fun insert(election: Election)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg elections: Election)

    //COMPLETED: Add select all election query
    @Query("SELECT * FROM election_table")
    fun getAll(): Election?

    //COMPLETED: Add select single election query
    @Query("SELECT * FROM election_table WHERE id = :key")
    fun getElection(key: Int): Election?

    //COMPLETED: Add delete query
    @Query("DELETE FROM election_table WHERE id = :key")
    fun delete(key: Int)

    //COMPLETED: Add clear query
    @Query("DELETE FROM election_table")
    fun clear()
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