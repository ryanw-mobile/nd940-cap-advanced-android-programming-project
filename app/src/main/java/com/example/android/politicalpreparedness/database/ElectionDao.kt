package com.example.android.politicalpreparedness.database

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

}