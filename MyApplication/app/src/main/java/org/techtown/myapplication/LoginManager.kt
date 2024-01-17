package org.techtown.myapplication

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginManager {
    private val userService: UserService

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.example.com")  // 여기에 서버의 base URL을 넣어주세요(현재 예시 주소 넣어놓음)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        userService = retrofit.create(UserService::class.java)
    }

    fun loginUser(loginId: String, callback: LoginCallback) {
        // 서버 통신이 아닌 단순한 예시로 로그인 성공 여부를 검사
        if (loginId == "1234") {
            // 로그인 성공 시
            callback.onLoginSuccess()
        } else {
            // 로그인 실패 시
            Log.d("LoginManager", "로그인 실패. 입력된 회원번호: $loginId")
            callback.onLoginFailure()
        }
    }
}
