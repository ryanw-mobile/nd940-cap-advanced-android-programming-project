package com.example.android.politicalpreparedness.data.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert
import org.hamcrest.core.Is
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
@SmallTest
class ElectionDaoTest {

    // HiltAndroidRule executes first - https://developer.android.com/training/dependency-injection/hilt-testing#multiple-testrules
    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    // Executes each task synchronously using Architecture Components.
    @get:Rule(order = 1)
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun init() {
        hiltRule.inject()
    }

    // Use in-memory database for testing
    @Inject
    lateinit var database: ElectionDatabase

    // Followed-election CRUD tests
    @Test
    fun emptyDatabase_followedElection_followSuccess() = runBlockingTest {
        // GIVEN: Empty Database -- nothing to do
        // WHEN: follow an election
        val electionId = 1
        database.electionDao.followElection(1)

        // THEN: Database should return the election is followed
        val isFollowedElection = database.electionDao.isFollowedElection(electionId)
        MatcherAssert.assertThat(isFollowedElection, Is.`is`(true))
    }

    @Test
    fun emptyDatabase_nonExistId_NotFollowed() = runBlockingTest {
        // GIVEN: Empty Database -- nothing to do
        // WHEN: Query an election Id which does not in the DB
        val electionId = 1

        // THEN: Database should return isFollowedElection is false
        val isFollowedElection = database.electionDao.isFollowedElection(electionId)
        MatcherAssert.assertThat(isFollowedElection, Is.`is`(false))
    }

    @Test
    fun followedElection_unfollow_NotFollowed() = runBlockingTest {
        // GIVEN: election is followed
        val electionId = 1
        database.electionDao.followElection(1)

        // WHEN: Unfollow the election
        database.electionDao.unfollowElection(electionId)

        // THEN: Database should return isFollowedElection is false
        val isFollowedElection = database.electionDao.isFollowedElection(electionId)
        MatcherAssert.assertThat(isFollowedElection, Is.`is`(false))
    }

    @Test
    fun nonEmptyDatabase_clearFollowedElections_NotFollowed() = runBlockingTest {
        // GIVEN: election is followed
        val electionId = 1
        database.electionDao.followElection(1)

        // WHEN: Unfollow the election
        database.electionDao.clearFollowedElections()

        // THEN: Database should return isFollowedElection is false
        val followedElectionIds = database.electionDao.getFollowedElectionIds()
        MatcherAssert.assertThat(followedElectionIds.size, Is.`is`(0))
    }

    @After
    fun closeDb() = database.close()
}