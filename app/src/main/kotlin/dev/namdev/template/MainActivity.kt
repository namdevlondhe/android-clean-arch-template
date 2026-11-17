package dev.namdev.template

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import dev.namdev.template.core.designsystem.TemplateTheme
import dev.namdev.template.feature.posts.POSTS_ROUTE
import dev.namdev.template.feature.posts.postsScreen

@HiltAndroidApp
class TemplateApplication : Application()

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TemplateTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = POSTS_ROUTE) {
                    postsScreen()
                    // Add more feature graphs here, e.g. settingsScreen()
                }
            }
        }
    }
}
