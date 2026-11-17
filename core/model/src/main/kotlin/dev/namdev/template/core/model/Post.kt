package dev.namdev.template.core.model

/** Domain model shared by every layer. No framework annotations here on purpose. */
data class Post(
    val id: Int,
    val userId: Int,
    val title: String,
    val body: String,
)
