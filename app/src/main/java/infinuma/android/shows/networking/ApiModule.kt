package infinuma.android.shows.networking

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

object ApiModule {
    private const val BASE_URL = "https://tv-shows.infinum.academy/"
    private val json = Json { ignoreUnknownKeys = true }

    lateinit var retrofit: ShowsApiService

    fun initRetrofit(context: Context) {
        val okhttp = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .addInterceptor(ChuckerInterceptor.Builder(context).build())
            .addInterceptor(AuthInterceptor(context))
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .client(okhttp)
            .build()
            .create(ShowsApiService::class.java)
    }
}

class AuthInterceptor(context: Context) : Interceptor {
    private val sessionManager = SessionManager(context)

    companion object {
        const val ACCESS_TOKEN = "access-token"
        const val TOKEN_TYPE = "token-type"
        const val AUTHORISATION = "authorization"
        const val CLIENT = "client"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        sessionManager.fetchAccessToken()?.let { token ->
            requestBuilder.addHeader(ACCESS_TOKEN, token)
        }
        sessionManager.fetchTokenType()?.let { token ->
            requestBuilder.addHeader(TOKEN_TYPE, token)
        }
        sessionManager.fetchTokenClient()?.let { token ->
            requestBuilder.addHeader(CLIENT, token)
        }
        sessionManager.fetchAuthorisation()?.let { token ->
            requestBuilder.addHeader(AUTHORISATION, token)
        }

        return chain.proceed(requestBuilder.build())
    }
}

class SessionManager(context: Context) {
    private var sharedPreferences: SharedPreferences =
        context.getSharedPreferences(AUTH_API_USER, Context.MODE_PRIVATE)
    private var accessToken: String? = null
    private var tokenType: String? = null
    private var clientToken: String? = null
    private var uid: String? = null
    private var auth: String? = null

    companion object {
        const val ACCESS_TOKEN = "ACCESS_TOKEN"
        const val CLIENT = "CLIENT"
        const val UID = "UID"
        const val TOKEN_TYPE = "TOKEN_TYPE"
        const val AUTHORISATION = "AUTHORISATION"
        const val AUTH_API_USER = "AUTH_API_USER"
    }

    fun saveAuthToken(accessToken: String, token_type: String, authentication: String, client: String, uid: String) {

        sharedPreferences.edit {
            putString(ACCESS_TOKEN, accessToken)
            putString(TOKEN_TYPE, token_type)
            putString(CLIENT, client)
            putString(AUTHORISATION, authentication)
            putString(UID, uid)
        }
    }

    fun fetchAccessToken(): String? {
        if (accessToken == null)
            accessToken = sharedPreferences.getString(ACCESS_TOKEN, null)
        return accessToken
    }

    fun fetchTokenType(): String? {
        if (tokenType == null)
            tokenType = sharedPreferences.getString(TOKEN_TYPE, null)
        return tokenType
    }

    fun fetchTokenClient(): String? {
        if (clientToken == null)
            clientToken = sharedPreferences.getString(CLIENT, null)
        return clientToken

    }

    fun fetchUID(): String? {
        if (uid == null)
            uid = sharedPreferences.getString(UID, null)
        return uid
    }

    fun fetchAuthorisation(): String? {
        if (auth == null)
            auth = sharedPreferences.getString(AUTHORISATION, null)
        return auth
    }


}