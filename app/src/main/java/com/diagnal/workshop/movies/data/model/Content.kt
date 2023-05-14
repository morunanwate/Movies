package com.diagnal.workshop.movies.data.model

import com.google.gson.annotations.SerializedName

data class Content(
    val name: String,
    @field:SerializedName("poster-image") val posterImage: String
)