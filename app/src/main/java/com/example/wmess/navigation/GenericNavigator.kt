package com.example.wmess.navigation

import androidx.compose.runtime.*
import androidx.navigation.*
import androidx.navigation.compose.*

abstract class GenericNavTarget(val route: String)

abstract class GenericNavigator<in NavTarget : GenericNavTarget, in Navigator : GenericNavigator<NavTarget, Navigator>>(
    private val navController: NavHostController
) {
    @Composable
    fun NavigateComposable(navTarget: NavTarget, builder: NavOptionsBuilder.() -> Unit = {}) {
        LaunchedEffect("Navigation") {
            navigate(navTarget, builder)
        }
    }

    fun navigate(navTarget: NavTarget, builder: NavOptionsBuilder.() -> Unit = {}) =
        navController.navigate(navTarget.route, builder)

    abstract fun NavGraphBuilder.navGraph(navigator: Navigator)

    @Suppress("UNCHECKED_CAST")
    fun buildTree(navGraphBuilder: NavGraphBuilder) = navGraphBuilder.navGraph(this as Navigator)

    protected fun NavGraphBuilder.navigation(
        startDestination: NavTarget,
        route: String,
        builder: NavGraphBuilder.() -> Unit
    ): Unit =
        navigation(startDestination = startDestination.route, route = route, builder)

    protected fun NavGraphBuilder.composable(
        target: NavTarget,
        arguments: List<NamedNavArgument> = emptyList(),
        deepLinks: List<NavDeepLink> = emptyList(),
        content: @Composable
            (NavBackStackEntry) -> Unit
    ): Unit =
        composable(target.route, arguments, deepLinks, content)
}