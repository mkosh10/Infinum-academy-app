package infinuma.android.shows.ShowsDatabase

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import infinuma.android.shows.module.User
import kotlinx.serialization.SerialName

@Entity(tableName = "show")
data class ShowEntity(
    @ColumnInfo(name = "id") @PrimaryKey val id: String,
    @ColumnInfo(name = "average_rating") val averageRating: Float?,
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(name = "image_url") val showImageUrl: String?,
    @ColumnInfo(name = "no_of_reviews") val noOfReviews: Int?,
    @ColumnInfo(name = "title") val title: String?
)

@Entity(tableName = "review")
data class ReviewEntity(
    @ColumnInfo(name = "id_review") @PrimaryKey val idRev: String,
    @ColumnInfo(name = "comment") val comment: String?,
    @ColumnInfo(name = "rating") val rating: Int,
    @ColumnInfo(name = "show_id") val show_id: Int,
    @ColumnInfo(name = "id_user") val idUser: String,
    @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "image_url") val imageUrl: String?
)