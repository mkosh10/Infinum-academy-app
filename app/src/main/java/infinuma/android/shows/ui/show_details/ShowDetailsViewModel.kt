package infinuma.android.shows.ui.show_details

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import infinuma.android.shows.ShowsDatabase.ReviewEntity
import infinuma.android.shows.module.CreateReviewRequest
import infinuma.android.shows.module.CreateReviewResponse
import infinuma.android.shows.module.Review
import infinuma.android.shows.networking.ApiModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import kotlinx.coroutines.withContext
import infinuma.android.shows.db.ShowsAppDatabase
import infinuma.android.shows.module.User
import kotlinx.coroutines.Dispatchers.Main

class ShowDetailsViewModel(private val database: ShowsAppDatabase) : ViewModel() {
    var showsId = MutableLiveData<String>()

    val reviewsListLiveData: MutableLiveData<MutableList<Review>> by lazy {
        showsId.value?.let {
            database.reviewDao().getAllReviews(it).map { ReviewEntityList ->
                _listSizeLiveData.value = ReviewEntityList.size
                ReviewEntityList.map { review ->
                    Review(
                        id = review.idRev,
                        comment = review.comment,
                        rating = review.rating,
                        show_id = review.show_id,
                        user = User(
                            review.idUser,
                            review.email,
                            review.imageUrl
                        )
                    )
                }
            }
        } as MutableLiveData<MutableList<Review>>

    }
    private val _listSizeLiveData = MutableLiveData<Int>()
    val listSizeLiveData: LiveData<Int> = _listSizeLiveData

    private val _averageReviewLiveData = MutableLiveData<Float>()
    val averageReviewLiveData: LiveData<Float> = _averageReviewLiveData

    private val _ratingLiveData = MutableLiveData<Boolean>()
    val ratingLiveData: LiveData<Boolean> = _ratingLiveData

    private val _checkConnectivity = MutableLiveData<Boolean>()
    val checkConnectivity: LiveData<Boolean> = _checkConnectivity

    private val _createReviewLiveData = MutableLiveData<Review>()

    private fun averageReviewFun() {
        var sum = 0f
        for (review in reviewsListLiveData.value!!) {
            sum += review.rating
        }
        _averageReviewLiveData.value = sum / _listSizeLiveData.value!!
    }

    fun checkConnectivity(isOnline: Boolean) {
        _checkConnectivity.value = isOnline
    }

    fun checkRating(rate: Float) {
        _ratingLiveData.value = rate > 0
    }

    fun listOfReviewsForShowWithId(showId: String) = viewModelScope.launch {
        try {
            val reviewsResponse = ApiModule.retrofit.getReviews(showId)
            val displayShowAPI = ApiModule.retrofit.showsListSusFun()

            reviewsListLiveData.value = reviewsResponse.reviews.toMutableList()
            _listSizeLiveData.value = reviewsResponse.meta.pagination.count
            averageReviewFun()

            reviewsListLiveData.value?.let { reviewsList ->
                val reviewEntityList = reviewsList.map { review ->
                    ReviewEntity(
                        idRev = review.id,
                        comment = review.comment,
                        rating = review.rating,
                        show_id = review.show_id,
                        idUser = review.user.id,
                        email = review.user.email,
                        imageUrl = review.user.imageUrl
                    )
                }

                averageReviewFun()
                withContext(Dispatchers.IO) {
                    database.reviewDao().insertAllReviews(reviewEntityList)
                }
            }

        } catch (e: Exception) {
            Log.e("ShowDetailsViewModel", "ERROR API - ${e.message} - listOfReviewsForShowWithId()")
            Log.e("ShowDetailsViewModel", "ERROR API - ${e.message} - listOfReviewsForShowWithId()")
        }
    }

    fun onSubmitReviewButtonClicked(rating: Int, comment: String, showId: Int) = viewModelScope.launch {
        try {

            val createAReviewResponse: CreateReviewResponse = submitReview(rating, comment, showId)
            _createReviewLiveData.value = createAReviewResponse.review

            _createReviewLiveData.value?.let { review ->
                val reviewEntity = ReviewEntity(
                    idRev = review.id,
                    comment = review.comment,
                    rating = review.rating,
                    show_id = review.show_id,
                    idUser = review.user.id,
                    email = review.user.email,
                    imageUrl = review.user.imageUrl
                )

                withContext(Dispatchers.IO) {
                    database.reviewDao().insertReview(reviewEntity)
                }
            }
            listOfReviewsForShowWithId(showId = showId.toString())


        } catch (e: Exception) {
            Log.e("ShowDetailsViewModel", "ERROR API - ${e.message} - onSubmitButtonClicked()")
        }

    }

    private suspend fun submitReview(rating: Int, comment: String, showId: Int) =
        ApiModule.retrofit.createReview(CreateReviewRequest(rating = rating, comment = comment, showId = showId))

}

