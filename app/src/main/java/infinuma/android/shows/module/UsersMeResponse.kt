package infinuma.android.shows.module

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
@Serializable
data class UsersMeResponse (
    @SerialName("user") val user : User
)
