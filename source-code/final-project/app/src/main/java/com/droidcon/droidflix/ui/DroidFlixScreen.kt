package com.droidcon.droidflix.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.droidcon.droidflix.R
import com.droidcon.droidflix.data.auth.AuthEventsViewModel
import com.droidcon.droidflix.data.model.FlixNav
import com.mikepenz.aboutlibraries.LibsBuilder
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DroidFlixAppBar(
    currentScreen: FlixNav,
    canNavigateBack: Boolean,
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    var showMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current

    TopAppBar(
        title = { Text(stringResource(currentScreen.title)) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back)
                    )
                }
            }
        },
        actions = {
            if (currentScreen == FlixNav.FlixList) {
                IconButton(onClick = { showMenu = !showMenu }) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = stringResource(R.string.options)
                    )
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(text = {
                        Text(text = stringResource(R.string.login))
                    }, onClick = {
                        showMenu = false
                        navController.navigate(FlixNav.Login.route)
                    })
                    DropdownMenuItem(text = {
                        Text(text = stringResource(R.string.add))
                    }, onClick = {
                        showMenu = false
                        navController.navigate(FlixNav.FlixEdit.route)
                    })
                    DropdownMenuItem(text = {
                        Text(text = stringResource(R.string.libraries))
                    }, onClick = {
                        showMenu = false
                        LibsBuilder().start(context)
                    })
                }
            }
        }
    )
}

@Composable
fun DroidFlixApp(
    navController: NavHostController = rememberNavController(),
) {
    val viewModel: FlixViewModel = hiltViewModel()
    val authEvents = hiltViewModel<AuthEventsViewModel>()
    val koinViewModel: FlixKoinViewModel = koinViewModel()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = FlixNav.getScreenByRoute(
        backStackEntry?.destination?.route ?: FlixNav.FlixList.route
    )
    viewModel.clearState()

    LaunchedEffect(Unit) {
        authEvents.onUnauthorized.collect {
            navController.navigate(FlixNav.Login.route)
        }
    }

    Scaffold(
        topBar = {
            DroidFlixAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navController = navController
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = FlixNav.FlixList.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = FlixNav.FlixList.route) {
                FlixListScreen(
                    koinViewModel,
                    onFlixClicked = {
                        navController.navigate(FlixNav.FlixDetail.route.replace("{id}", it.id))
                    },
                )
            }
            composable(
                route = FlixNav.FlixDetail.route,
                arguments = listOf(navArgument("id") {type = NavType.StringType})
            ) {
                FlixDetailsScreen(
                    viewModel,
                    it.arguments!!.getString("id")!!,
                    onFlixWatchClicked = {
                        navController.navigate(FlixNav.FlixWatch.route)
                    },
                    onFlixEditClicked = {
                        navController.navigate(FlixNav.FlixEdit.route)
                    }
                )
            }
            composable(FlixNav.Login.route) {
                FlixLoginScreen(
                    viewModel,
                    onLogin = {
                        navController.popBackStack()
                    },
                )
            }
            composable(FlixNav.FlixEdit.route) {
                FlixEditScreen(
                    viewModel,
                    onFinish = {
                        navController.popBackStack()
                    },
                )
            }
            composable(FlixNav.FlixWatch.route) {
                FlixWatchScreen(viewModel)
            }
        }
    }
}