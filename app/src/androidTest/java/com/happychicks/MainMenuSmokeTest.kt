package com.happychicks

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** Smoke test: main menu displays without crashing. */
@RunWith(AndroidJUnit4::class)
class MainMenuSmokeTest {

    @get:Rule val rule = ActivityScenarioRule(MainActivity::class.java)

    @Test fun mainMenuShowsCategoryTitles() {
        onView(withText(com.happychicks.R.string.menu_chicken)).check(matches(isDisplayed()))
        onView(withText(com.happychicks.R.string.menu_funland)).check(matches(isDisplayed()))
        onView(withText(com.happychicks.R.string.menu_farm)).check(matches(isDisplayed()))
        onView(withText(com.happychicks.R.string.menu_settings)).check(matches(isDisplayed()))
    }
}
