package dev.namdev.template.feature.posts

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val POSTS_ROUTE = "posts"

fun NavGraphBuilder.postsScreen() {
    composable(route = POSTS_ROUTE) {
        PostsRoute()
    }
}
