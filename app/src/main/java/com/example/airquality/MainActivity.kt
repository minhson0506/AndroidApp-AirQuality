package com.example.airquality

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.airquality.components.*
import com.example.airquality.services.DataViewModel
import com.example.airquality.services.GetLocation
import com.example.airquality.ui.theme.*
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class MainActivity : ComponentActivity() {

    companion object {
        private lateinit var model: DataViewModel
        var tag = "airQuality"
    }

    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        // check permission
        checkPermission(this)

        // init view model
        model = DataViewModel(application = application)

        setContent {
            val mainNavController = rememberNavController()

            AirQualityTheme {
                // change status bar color
                val systemUiController = rememberSystemUiController()
                SideEffect {
                    systemUiController.setStatusBarColor(
                        color = White,
                        darkIcons = true
                    )
                }
                // start get phone location
                GetLocation(this, model)
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background) {
                    NavHost(mainNavController, startDestination = "landingPage") {
                        composable("landingPage") {
                            LandingPage(model, mainNavController)
                        }
                        composable("main") {
                            MainScreen(mainNavController, model = model)
                        }

                    }
                }
            }
        }
    }

    override fun onPause() {
        Log.d(tag, "onDestroy pause")
        super.onPause()
    }

    override fun onDestroy() {
        Log.d(tag, "onDestroy")
        super.onDestroy()
    }
}

sealed class BottomNavItem(var title: String, var icon: Int, var screen_route: String) {
    object Home : BottomNavItem("Home", R.drawable.home, "home")
    object Chart : BottomNavItem("Chart", R.drawable.chart, "chart")
    object Setting : BottomNavItem("Settings", R.drawable.settings, "setting")

}

@ExperimentalFoundationApi
@Composable
fun NavigationGraph(
    mainNavController: NavController,
    navController: NavHostController, model: DataViewModel,
) {
    NavHost(navController, startDestination = BottomNavItem.Home.screen_route) {
        composable(BottomNavItem.Home.screen_route) {
            Dashboard(model = model)
        }
        composable(BottomNavItem.Chart.screen_route) {
            ChartPage(model = model)
        }
        composable(BottomNavItem.Setting.screen_route) {
            Settings(navController = mainNavController, model = model)
        }
        composable("landingPage") {
            LandingPage(model, mainNavController)
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Chart,
        BottomNavItem.Setting,

        )

    BottomNavigation(
        backgroundColor = Color.White,
    ) {
        val backStackEntry = navController.currentBackStackEntryAsState()
        items.forEach { item ->
            val currentRoute = backStackEntry.value?.destination?.route;
            val selected = currentRoute == item.screen_route
            BottomNavigationItem(
                icon = {
                    Image(
                        painterResource(id = item.icon),
                        contentDescription = item.title,
                        modifier = Modifier.size(25.dp),
                        colorFilter = if (selected) ColorFilter.tint(color = Blue) else ColorFilter.tint(
                            color = DarkGray)
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        color = if (selected) Blue else Color.Black,
                        fontFamily = regular
                    )
                },
                alwaysShowLabel = true,
                selected = false,
                onClick = {
                    navController.navigate(item.screen_route) {
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) {
                                saveState = true
                            }
                        }

                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@ExperimentalFoundationApi
@Composable
fun MainScreen(mainNavController: NavController, model: DataViewModel) {
    val navController = rememberNavController()
    var showBottomBar by remember { mutableStateOf(true) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    showBottomBar = when (navBackStackEntry?.destination?.route) {
        "exercise" -> false
        "exercise_result" -> false
        "daily" -> false
        "graph-heartRate" -> false
        "history" -> false
        "update" -> false
        else -> true
    }

    Scaffold(
        bottomBar = { if (showBottomBar) BottomNavigationBar(navController) },
        content = { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                NavigationGraph(
                    mainNavController = mainNavController,
                    navController = navController,
                    model = model
                )
            }
        }
    )
}

fun checkPermission(activity: Activity) {
    if (Build.VERSION.SDK_INT >= 33) {
        if ((activity.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) || (activity.checkSelfPermission(
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) ||
            (activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        ) {
            Log.d(MainActivity.tag, "No permission for notification")
            activity.requestPermissions(
                arrayOf(
                    Manifest.permission.POST_NOTIFICATIONS,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                ), 1
            )
            while ((activity.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) || (activity.checkSelfPermission(
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) ||
                (activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ) {
                Thread.sleep(100)
            }
        }
    } else {
        if (
            (activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) ||
            (activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        ) {
            Log.d(MainActivity.tag, "No permission")
            activity.requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                ), 1
            )
            while ((activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) ||
                (activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ) {
                Thread.sleep(100)
            }

        }
    }
    Log.i(MainActivity.tag, "permissions ok")
}