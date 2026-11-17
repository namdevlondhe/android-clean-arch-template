package dev.namdev.template.feature.posts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.namdev.template.core.domain.ObservePostsUseCase
import dev.namdev.template.core.domain.RefreshPostsUseCase
import dev.namdev.template.core.model.Post
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface PostsUiState {
    data object Loading : PostsUiState
    data class Success(val posts: List<Post>, val isRefreshing: Boolean) : PostsUiState
    data class Error(val message: String) : PostsUiState
}

@HiltViewModel
class PostsViewModel @Inject constructor(
    observePosts: ObservePostsUseCase,
    private val refreshPosts: RefreshPostsUseCase,
) : ViewModel() {

    private val isRefreshing = MutableStateFlow(false)
    private val error = MutableStateFlow<String?>(null)

    val uiState: StateFlow<PostsUiState> =
        combine(observePosts(), isRefreshing, error) { posts, refreshing, errorMessage ->
            when {
                posts.isNotEmpty() -> PostsUiState.Success(posts, refreshing)
                refreshing -> PostsUiState.Loading
                errorMessage != null -> PostsUiState.Error(errorMessage)
                else -> PostsUiState.Success(emptyList(), isRefreshing = false)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
            initialValue = PostsUiState.Loading,
        )

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            isRefreshing.value = true
            error.value = null
            refreshPosts().onFailure { error.value = it.message ?: "Something went wrong" }
            isRefreshing.value = false
        }
    }

    private companion object {
        const val STOP_TIMEOUT_MS = 5_000L
    }
}
