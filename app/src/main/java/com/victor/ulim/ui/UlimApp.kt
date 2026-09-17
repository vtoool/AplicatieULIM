package com.victor.ulim.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.victor.ulim.R
import com.victor.ulim.ui.screens.DisplayScreen
import com.victor.ulim.ui.screens.FormScreen
import com.victor.ulim.ui.screens.LoginScreen

object Routes {
    const val LOGIN = "login"
    const val FORM = "form"
    const val DISPLAY = "display"
}

private val routeOrder = mapOf(
    Routes.LOGIN to 0,
    Routes.FORM to 1,
    Routes.DISPLAY to 2
)

@Composable
fun UlimApp(vm: AppViewModel) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            UlimBottomBar(
                currentRoute = currentRoute,
                loggedIn = vm.loggedIn,
                onSelect = { route -> navController.navigateToRoute(route) }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.LOGIN,
            modifier = Modifier.padding(padding),
            enterTransition = { forwardEnter() },
            exitTransition = { forwardExit() },
            popEnterTransition = { forwardEnter() },
            popExitTransition = { forwardExit() }
        ) {
            composable(Routes.LOGIN) {
                LoginScreen(
                    vm = vm,
                    snackbarHostState = snackbarHostState,
                    onLoginSuccess = { navController.navigateToRoute(Routes.FORM) }
                )
            }
            composable(Routes.FORM) {
                FormScreen(
                    vm = vm,
                    snackbarHostState = snackbarHostState,
                    onContinue = { navController.navigateToRoute(Routes.DISPLAY) }
                )
            }
            composable(Routes.DISPLAY) {
                DisplayScreen(
                    vm = vm,
                    snackbarHostState = snackbarHostState,
                    onBackHome = { navController.navigateToRoute(Routes.LOGIN) }
                )
            }
        }
    }
}

/**
 * Tabs match the sketch (logare / formular / afisare); the form and display
 * tabs stay locked until the user has signed in.
 */
@Composable
private fun UlimBottomBar(
    currentRoute: String?,
    loggedIn: Boolean,
    onSelect: (String) -> Unit
) {
    val items = listOf(
        Triple(Routes.LOGIN, R.string.tab_logare, Icons.Filled.Lock),
        Triple(Routes.FORM, R.string.tab_formular, Icons.Filled.EditNote),
        Triple(Routes.DISPLAY, R.string.tab_afisare, Icons.Filled.Visibility)
    )
    NavigationBar {
        items.forEach { (route, labelRes, icon) ->
            NavigationBarItem(
                selected = currentRoute == route,
                enabled = loggedIn || route == Routes.LOGIN,
                onClick = { onSelect(route) },
                icon = { Icon(icon, contentDescription = null) },
                label = {
                    Text(
                        text = stringResource(labelRes),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            )
        }
    }
}

/** Bottom-nav navigation pattern: single top, state saved and restored. */
private fun NavHostController.navigateToRoute(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

// Screen transitions: slide towards the direction of the route order (login → form → display)
private fun AnimatedContentTransitionScope<NavBackStackEntry>.forwardEnter(): EnterTransition {
    val forward =
        (routeOrder[targetState.destination.route] ?: 0) >= (routeOrder[initialState.destination.route] ?: 0)
    val x = if (forward) 1 else -1
    return slideInHorizontally(tween(380)) { x * it / 3 } + fadeIn(tween(380))
}

private fun AnimatedContentTransitionScope<NavBackStackEntry>.forwardExit(): ExitTransition {
    val forward =
        (routeOrder[targetState.destination.route] ?: 0) >= (routeOrder[initialState.destination.route] ?: 0)
    val x = if (forward) -1 else 1
    return slideOutHorizontally(tween(380)) { x * it / 4 } + fadeOut(tween(380))
}
