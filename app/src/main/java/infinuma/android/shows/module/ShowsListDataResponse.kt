package infinuma.android.shows.module

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShowsListDataResponse(
    @SerialName("shows") val shows: List<Show>,
    @SerialName("meta") val meta: Meta
)

@Serializable
@Parcelize
data class Show(
    @SerialName("id") val id: String,
    @SerialName("average_rating") val averageRating: Float?,
    @SerialName("description") val description: String?,
    @SerialName("image_url") val showImageUrl: String?,
    @SerialName("no_of_reviews") val noOfReviews: Int?,
    @SerialName("title") val title: String?
) : Parcelable

@Serializable
data class Meta(
    @SerialName("pagination") val pagination: Pagination,
)

@Serializable
data class Pagination(
    @SerialName("count") val count: Int,
    @SerialName("page") val page: Int,
    @SerialName("items") val items: Int,
    @SerialName("pages") val pages: Int,
)
