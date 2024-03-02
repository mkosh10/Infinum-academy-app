package infinuma.android.shows.module

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

    @Serializable
    data class CreateReviewResponse(
        @SerialName("review") val review: Review
    )
