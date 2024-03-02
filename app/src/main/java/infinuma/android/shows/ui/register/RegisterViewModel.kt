package infinuma.android.shows.ui.register

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import infinuma.android.shows.module.RegisterRequest
import infinuma.android.shows.networking.ApiModule
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    private val _registrationResultLiveData: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    val registrationResultLiveData: LiveData<Boolean> = _registrationResultLiveData

    fun onRegisterButtonClicked(username: String, password: String, passwordConfirmation: String) = viewModelScope.launch {
        try {
            val registerResponse = register(username, password, passwordConfirmation)
            registerResponse.headers()
            _registrationResultLiveData.value = registerResponse.isSuccessful
        } catch (e: Exception) {
            _registrationResultLiveData.value = false
            Log.e("RegisterViewModel", "ERROR API - ${e.message} - onRegisterButtonClicked()")
        }

    }

    private suspend fun register(username: String, password: String, passwordConfirmation: String) =
        ApiModule.retrofit.register(
            request = RegisterRequest(
                email = username,
                password = password,
                passwordConfirmation = passwordConfirmation
            )
        )
}