package com.sun.daily_photo_share_android.navigation

import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

/** True when this destination, or one of its parents, is the route [T]. */
internal inline fun <reified T : Any> NavDestination?.isOn(): Boolean =
    this?.hierarchy?.any { it.hasRoute<T>() } == true

/** Switching between top-level tabs keeps one back stack per tab and restores its state. */
internal fun NavHostController.navigateToTopLevel(route: Any) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}