package infinuma.android.shows.networking

import infinuma.android.shows.module.CreateReviewRequest
import infinuma.android.shows.module.CreateReviewResponse
import infinuma.android.shows.module.LoginRequest
import infinuma.android.shows.module.LoginResponse
import infinuma.android.shows.module.RegisterRequest
import infinuma.android.shows.module.RegisterResponse
import infinuma.android.shows.module.ReviewsListDataResponse
import infinuma.android.shows.module.ShowsListDataResponse
import infinuma.android.shows.module.User
import infinuma.android.shows.module.UsersMeResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

// TODO define all functions required to communicate with the server
interface ShowsApiService {

    @POST("users")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("users/sign_in")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("shows")
    suspend fun showsListSusFun(): ShowsListDataResponse

    @GET("shows/{show_id}/reviews")
    suspend fun getReviews(@Path("show_id") showId: String): ReviewsListDataResponse

    @POST("reviews")
    suspend fun createReview(@Body request: CreateReviewRequest): CreateReviewResponse

    @Multipart
    @PUT("/users")
    suspend fun putImage(@Part request: MultipartBody.Part): Response<User>

    @GET("users/me")
    suspend fun getProfilePicture(): UsersMeResponse


}