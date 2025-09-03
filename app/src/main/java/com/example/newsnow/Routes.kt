package com.example.newsnow

import kotlinx.serialization.Serializable

@Serializable
object HomePageScreen

@Serializable
data class NewsarticleScreen(
    val url: String
)
