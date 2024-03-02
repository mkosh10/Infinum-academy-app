package infinuma.android.shows.ui.shows

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import infinuma.android.shows.ShowsDatabase.ShowEntity
import infinuma.android.shows.db.ShowsAppDatabase
import infinuma.android.shows.module.Show
import infinuma.android.shows.networking.ApiModule
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

class ShowsViewModel(private val database: ShowsAppDatabase) : ViewModel() {

    private val _checkConnectivity = MutableLiveData<Boolean>()
    val checkConnectivity: LiveData<Boolean> = _checkConnectivity
    private val _usersMELiveData = MutableLiveData<String?>()
    val usersMELiveData: LiveData<String?> = _usersMELiveData

    val showsListLiveData: MutableLiveData<MutableList<Show>> by lazy {
        database.showDao().getAllShows().map { showsEntityList ->
            showsEntityList.map { showEntity ->
                Show(
                    id = showEntity.id,
                    averageRating = showEntity.averageRating,
                    description = showEntity.description,
                    showImageUrl = showEntity.showImageUrl,
                    noOfReviews = showEntity.noOfReviews,
                    title = showEntity.title
                )
            }
        } as MutableLiveData<MutableList<Show>>
    }

    fun putUserImage(image: File) = viewModelScope.launch {
        try {
            val requestBody = image.asRequestBody("image/*".toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData("image", image.name, requestBody)
            Log.e("API", "API IMAGE")
            ApiModule.retrofit.putImage(part)
        } catch (e: Exception) {
            Log.e("ShowsViewModel", "error - putUserImage() : ${e.message}")
        }
    }

    fun getProfilePictureViewModel() = viewModelScope.launch {
        try {
            Log.e("SHOWSVIEWMODEL", "ZIMAM SLIKA")
            val userMeResponse = ApiModule.retrofit.getProfilePicture()
            _usersMELiveData.value = userMeResponse.user.imageUrl
            Log.e("SHOWSVIEWMODEL", "SLIKA STAVENA")

        } catch (e: Exception) {
            Log.e("SHOWS", "PROFILE PICTURE ${e.message}")
        }
    }

    fun checkConnectivity(isOnline: Boolean) {
        _checkConnectivity.value = isOnline

        Log.e("ShowsViewModel", "STAVENO INTERNET NA ${_checkConnectivity.value}")
    }

    fun listShowList() = viewModelScope.launch {
        //        m
        try {
            val showsResponse = ApiModule.retrofit.showsListSusFun()
            showsListLiveData.value = showsResponse.shows.toMutableList()
            Log.e("ShowsViewModel", "zapisuvamVoDatabaza")

            withContext(Dispatchers.IO) {
                showsListLiveData.value?.let { showsList ->
                    val showEntityList = showsList.map { show ->
                        ShowEntity(
                            id = show.id,
                            averageRating = show.averageRating,
                            description = show.description,
                            showImageUrl = show.showImageUrl,
                            noOfReviews = show.noOfReviews,
                            title = show.title
                        )
                    }
                    Log.e("EOEOEOOEOEEOEO", "ZAPISANO ${showEntityList[0].title}")
                    database.showDao().insertAllShows(showEntityList)
                }
            }
        } catch (e: Exception) {
            Log.e("SHOWS", "${e.message}  SHOWS")
        }
    }
}


