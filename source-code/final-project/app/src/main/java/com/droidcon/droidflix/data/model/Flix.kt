package com.droidcon.droidflix.data.model

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import com.google.gson.annotations.SerializedName

@Parcelize
@Serializable
@JsonClass(generateAdapter = true)
data class Flix (
    @SerializedName("id", alternate = ["ID"])
    val id: String = "",
    val title: String = "n/a",
    val year: String = "n/a",
    val plot: String = "n/a",
    val poster: String?,
    val video: String?,
    val ratings: List<Rating> = emptyList(),
) : Parcelable

@Parcelize
@Serializable
@JsonClass(generateAdapter = true)
data class Rating(
    val source: String = "n/a",
    val value: String = "n/a"
) : Parcelable {
    override fun toString(): String {
        return "$source: $value"
    }
}