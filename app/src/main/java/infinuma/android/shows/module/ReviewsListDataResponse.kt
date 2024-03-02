package infinuma.android.shows.module

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReviewsListDataResponse(
    @SerialName("reviews") val reviews: List<Review>,
    @SerialName("meta") val meta: Meta
)

@Serializable
data class Review(
    @SerialName("id") val id: String,
    @SerialName("comment") val comment: String?,
    @SerialName("rating") val rating: Int,
    @SerialName("show_id") val show_id: Int,
    @SerialName("user") val user: User
)

@Serializable
data class MetaReviews(
    @SerialName("pagination") val pagination: Pagination,
)

@Serializable
data class PaginationReviews(
    @SerialName("count") val count: Int,
    @SerialName("page") val page: Int,
    @SerialName("items") val items: Int,
    @SerialName("pages") val pages: Int,
)