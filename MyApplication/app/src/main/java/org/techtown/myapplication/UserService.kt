package org.techtown.myapplication
//Retrofit2를 초기화하고 API 호출을 담당할 클래스(로그인 구현을 위해)
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface UserService {
    @POST("users/login")
    fun loginUser(@Body request: LoginRequest): Call<LoginResponse>
}

data class LoginRequest(val loginId: String)

data class LoginResponse(
    val timestamp: String,
    val code: String,
    val status: String,
    val detail: String,
    val data: UserData?
)

data class UserData(
    val id: Long,
    val name: String,
    val loginId: String
)
