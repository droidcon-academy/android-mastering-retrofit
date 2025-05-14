package com.droidcon.droidflix.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Flix (
    val id: String = "",
    val title: String = "n/a",
    val year: String = "n/a",
    val plot: String = "n/a",
    val poster: String?,
    val video: String?,
    val ratings: List<Rating> = emptyList(),
) : Parcelable

@Parcelize
data class Rating(
    val source: String = "n/a",
    val value: String = "n/a"
) : Parcelable {
    override fun toString(): String {
        return "$source: $value"
    }
}