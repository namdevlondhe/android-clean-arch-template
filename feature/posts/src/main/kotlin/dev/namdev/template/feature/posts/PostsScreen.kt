package dev.namdev.template.feature.posts

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.namdev.template.core.designsystem.ErrorView
import dev.namdev.template.core.designsystem.LoadingView
import dev.namdev.template.core.designsystem.TemplateTheme
import dev.namdev.template.core.model.Post

/** Stateful entry point used by navigation. */
@Composable
fun PostsRoute(viewModel: PostsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    PostsScreen(state = state, onRefresh = viewModel::refresh)
}

/** Stateless screen: easy to preview and to test. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostsScreen(
    state: PostsUiState,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = { TopAppBar(title = { Text("Posts") }) },
    ) { padding ->
        when (state) {
            PostsUiState.Loading -> LoadingView(Modifier.padding(padding))
            is PostsUiState.Error -> ErrorView(
                message = state.message,
                onRetry = onRefresh,
                modifier = Modifier.padding(padding),
            )
            is PostsUiState.Success -> PullToRefreshBox(
                isRefreshing = state.isRefreshing,
                onRefresh = onRefresh,
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
            ) {
                PostList(posts = state.posts)
            }
        }
    }
}

@Composable
private fun PostList(posts: List<Post>) {
    LazyColumn(contentPadding = PaddingValues(vertical = 8.dp)) {
        items(posts, key = Post::id) { post ->
            ListItem(
                headlineContent = { Text(post.title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                supportingContent = { Text(post.body, maxLines = 2, overflow = TextOverflow.Ellipsis) },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PostsScreenPreview() {
    TemplateTheme(dynamicColor = false) {
        PostsScreen(
            state = PostsUiState.Success(
                posts = listOf(
                    Post(2, 1, "Offline-first with Room", "The database is the single source of truth."),
                    Post(1, 1, "Convention plugins", "Shared Gradle setup lives in build-logic."),
                ),
                isRefreshing = false,
            ),
            onRefresh = {},
        )
    }
}
