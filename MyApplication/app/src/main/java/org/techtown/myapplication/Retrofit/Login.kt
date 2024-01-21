package org.techtown.myapplication.Retrofit
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

data class LoginRequest(
    @SerializedName("loginId") val loginId: String
)

data class LoginResponse(
    @SerializedName("timestamp") val timestamp: String,
    @SerializedName("code") val code: String,
    @SerializedName("status") val status: String,
    @SerializedName("detail") val detail: String,
    @SerializedName("data") val data: UserData?
)

data class UserData(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("loginId") val loginId: String
)

interface UserService {
    @POST("users/login")
    fun loginUser(@Body request: LoginRequest): Call<LoginResponse>
}

class ApiManager_login {
    companion object {
        private const val BASE_URL = "https://your-api-base-url.com/" // 실제 API의 베이스 URL로 변경

        fun create(): UserService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(UserService::class.java)
        }
    }
}