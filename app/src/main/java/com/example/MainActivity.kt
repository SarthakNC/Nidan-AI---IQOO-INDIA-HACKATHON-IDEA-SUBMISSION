package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.navigation.BottomNavItem
import com.example.ui.navigation.NidanNavHost
import com.example.ui.theme.ClinicalOutline
import com.example.ui.theme.ClinicalSurface
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SlateSecondary
import com.example.ui.theme.TealOnPrimaryContainer
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.TealPrimaryContainer

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                NidanAppRoot()
            }
        }
    }
}

@Composable
fun NidanAppRoot() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomNavItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Patients,
        BottomNavItem.History,
        BottomNavItem.Handover,
        BottomNavItem.Settings
    )

    val isTopLevelDestination = bottomNavItems.any { it.route == currentRoute }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (isTopLevelDestination) {
                NavigationBar(
                    containerColor = ClinicalSurface,
                    tonalElevation = 0.dp,
                    modifier = Modifier
                        .testTag("nidan_bottom_nav")
                        .drawBehind {
                            drawLine(
                                color = ClinicalOutline,
                                start = Offset(0f, 0f),
                                end = Offset(size.width, 0f),
                                strokeWidth = 1.dp.toPx()
                            )
                        }
                ) {
                    bottomNavItems.forEach { item ->
                        val selected = currentRoute == item.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title
                                )
                            },
                            label = {
                                Text(
                                    text = item.title,
                                    fontSize = 10.5.sp,
                                    color = if (selected) TealOnPrimaryContainer else SlateSecondary
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = TealOnPrimaryContainer,
                                selectedTextColor = TealOnPrimaryContainer,
                                unselectedIconColor = SlateSecondary,
                                unselectedTextColor = SlateSecondary,
                                indicatorColor = TealPrimaryContainer
                            ),
                            modifier = Modifier.testTag("nav_item_${item.title.lowercase()}")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NidanNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

