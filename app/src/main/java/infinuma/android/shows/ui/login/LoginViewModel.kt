package infinuma.android.shows.ui.login

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import infinuma.android.shows.module.LoginRequest
import infinuma.android.shows.module.LoginResponse
import infinuma.android.shows.networking.ApiModule
import infinuma.android.shows.networking.SessionManager
import kotlinx.coroutines.launch
import retrofit2.Response

class LoginViewModel : ViewModel() {
    private lateinit var sessionManager: SessionManager

    private val _loginResultLiveData: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    val loginResultLiveData: LiveData<Boolean> = _loginResultLiveData

        companion object {
            const val accesstoken = "access-token"
            const val tokentype = "token-type"
            const val authorization = "authorization"
            const val client = "client"
            const val uid = "uid"

        }

    fun initSessionManager(context: Context) {
        sessionManager = SessionManager(context)
    }

    fun onLoginButtonClicked(username: String, password: String) = viewModelScope.launch {
        try {
            val loginResponse: Response<LoginResponse> = loginUser(username, password)

            sessionManager.saveAuthToken(
                accessToken = loginResponse.headers()[accesstoken].toString(),
                token_type = loginResponse.headers()[tokentype].toString(),
                authentication = loginResponse.headers()[authorization].toString(),
                client = loginResponse.headers()[client].toString(),
                uid = loginResponse.headers()[uid].toString()
            )

            _loginResultLiveData.value = loginResponse.isSuccessful

        } catch (e: Exception) {
            Log.e("LoginFragment", "ERROR API ${e.message} onLoginButtonClicked()")
            _loginResultLiveData.value = false
        }

    }

    private suspend fun loginUser(username: String, password: String) =
        ApiModule.retrofit.login(
            request = LoginRequest(
                email = username,
                password = password
            )
        )
}