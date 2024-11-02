package com.example.android.politicalpreparedness.data.database

import android.content.Context
import android.os.Build
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.android.politicalpreparedness.TestApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(
    sdk = [Build.VERSION_CODES.P],
    application = TestApplication::class,
)
class ElectionDaoTest {
    private lateinit var electionDatabase: ElectionDatabase
    private lateinit var electionDao: ElectionDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        electionDatabase = Room.inMemoryDatabaseBuilder(context, ElectionDatabase::class.java)
            .allowMainThreadQueries() // only for testing
            .build()

        electionDao = electionDatabase.electionDao
    }

    @After
    fun teardown() {
        electionDatabase.close()
    }

    // Followed-election CRUD tests
    @Test
    fun emptyDatabase_followedElection_followSuccess() = runTest {
        // GIVEN: Empty Database -- nothing to do
        // WHEN: follow an election
        val electionId = 1
        electionDao.followElection(1)

        // THEN: Database should return the election is followed
        val isFollowedElection = electionDao.isFollowedElection(electionId)
        assertTrue(isFollowedElection)
    }

    @Test
    fun emptyDatabase_nonExistId_NotFollowed() = runTest {
        // GIVEN: Empty Database -- nothing to do
        // WHEN: Query an election Id which does not in the DB
        val electionId = 1

        // THEN: Database should return isFollowedElection is false
        val isFollowedElection = electionDao.isFollowedElection(electionId)
        assertFalse(isFollowedElection)
    }

    @Test
    fun followedElection_unfollow_NotFollowed() = runTest {
        // GIVEN: election is followed
        val electionId = 1
        electionDao.followElection(electionId)

        // WHEN: Unfollow the election
        electionDao.unfollowElection(electionId)

        // THEN: Database should return isFollowedElection is false
        val isFollowedElection = electionDao.isFollowedElection(electionId)
        assertFalse(isFollowedElection)
    }

    @Test
    fun nonEmptyDatabase_clearFollowedElections_NotFollowed() = runTest {
        // GIVEN: election is followed
        val electionId = 1
        electionDao.followElection(electionId)

        // WHEN: Unfollow the election
        electionDao.clearFollowedElections()

        // THEN: Database should return isFollowedElection is false
        val followedElectionIds = electionDao.getFollowedElectionIds()
        assertEquals(0, followedElectionIds.size)
    }
}
