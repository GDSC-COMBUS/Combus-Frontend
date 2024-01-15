package org.techtown.myapplication

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginManager {
    private val userService: UserService

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("BASE_URL_HERE")  // 여기에 서버의 base URL을 넣어주세요
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        userService = retrofit.create(UserService::class.java)
    }

    fun loginUser(loginId: String, callback: LoginCallback) {
        val request = LoginRequest(loginId)

        userService.loginUser(request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    // 로그인 성공 시
                    callback.onLoginSuccess()
                } else {
                    // 로그인 실패 시
                    callback.onLoginFailure()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                // 네트워크 통신 실패 시 처리
                callback.onLoginFailure()
            }
        })
    }
}
