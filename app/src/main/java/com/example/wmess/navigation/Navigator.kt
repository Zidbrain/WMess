package com.example.wmess.navigation

import androidx.compose.runtime.*
import androidx.navigation.*
import androidx.navigation.compose.*

abstract class NavTarget(val route: String)

abstract class Navigator<in T : NavTarget>(
    private val navController: NavHostController
) {
    @Composable
    fun NavigateComposable(navTarget: T, builder: NavOptionsBuilder.() -> Unit = {}) {
        LaunchedEffect("Navigation") {
            navigate(navTarget, builder)
        }
    }

    fun navigate(navTarget: T, builder: NavOptionsBuilder.() -> Unit = {}) =
        navController.navigate(navTarget.route, builder)

    abstract fun NavGraphBuilder.navGraph()

    @Suppress("UNCHECKED_CAST")
    fun buildTree(navGraphBuilder: NavGraphBuilder) = navGraphBuilder.navGraph()

    protected fun NavGraphBuilder.navigation(
        startDestination: T,
        route: String,
        builder: NavGraphBuilder.() -> Unit
    ): Unit =
        navigation(startDestination = startDestination.route, route = route, builder)

    protected fun NavGraphBuilder.composable(
        target: T,
        arguments: List<NamedNavArgument> = emptyList(),
        deepLinks: List<NavDeepLink> = emptyList(),
        content: @Composable
            (NavBackStackEntry) -> Unit
    ): Unit =
        composable(target.route, arguments, deepLinks, content)
}