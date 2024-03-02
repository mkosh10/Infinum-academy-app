package infinuma.android.shows.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import infinuma.android.shows.ShowsDatabase.ReviewEntity

@Dao
interface ReviewDao {

    @Query("SELECT * FROM review WHERE show_id IS :showId")
    fun getAllReviews(showId: String): LiveData<List<ReviewEntity>>

    @Query("SELECT * FROM review WHERE id_review IS :reviewId")
    suspend fun getReview(reviewId: String): ReviewEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllReviews(reviews: List<ReviewEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReview(reviews: ReviewEntity)
}

