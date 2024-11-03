/*
 * Copyright (c) 2024. Ryan Wong
 * https://github.com/ryanw-mobile
 */

package com.example.android.politicalpreparedness.ui

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.example.android.politicalpreparedness.R

internal class MainActivityTestRobot {
    fun checkNavigationLayoutIsCorrect() {
        try {
            assertTopAppBarIsDisplayed()
            assertLaunchLogoIsDisplayed()
            assertFindMyRepresentativesButtonIsDisplayed()
            assertUpcomingElectionsButtonIsDisplayed()
        } catch (e: AssertionError) {
            throw AssertionError("Expected navigation layout is not observed. ${e.message}", e)
        }
    }

    // assert
    private fun assertFindMyRepresentativesButtonIsDisplayed() {
        onView(withId(R.id.representative_button)).check(matches(isDisplayed()))
    }

    private fun assertUpcomingElectionsButtonIsDisplayed() {
        onView(withId(R.id.upcoming_button)).check(matches(isDisplayed()))
    }

    private fun assertLaunchLogoIsDisplayed() {
        onView(withId(R.id.launch_logo)).check(matches(isDisplayed()))
    }

    private fun assertTopAppBarIsDisplayed() {
        onView(withId(R.id.top_appbar)).check(matches(isDisplayed()))
    }
}
