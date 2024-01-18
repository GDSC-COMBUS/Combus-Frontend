package org.techtown.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.techtown.myapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val loginManager = LoginManager()
        val membershipNumberEditText = findViewById<EditText>(R.id.membershipNumber)

        binding.startButton.setOnClickListener {
            val membershipNumber = membershipNumberEditText.text.toString()

            loginManager.loginUser(membershipNumber, object : LoginCallback {
                override fun onLoginSuccess() {
                    // 로그인이 성공한 경우에만 예약 내역 조회 시작
                    if (membershipNumber == "1234") {
                        // 화면 전환 로직 추가
                        val intent = Intent(this@MainActivity, Reserved::class.java)
                        startActivity(intent)
                    } else {
                        // 로그인 실패 시 토스트 메시지 띄우기
                        onLoginFailure()
                    }
                }

                override fun onLoginFailure() {
                    // 로그인 실패 시 토스트 메시지 띄우기
                    showToast("로그인에 실패했습니다. 올바른 회원번호를 입력해주세요.") //UserService.kt에 있는 거에서 긁어와서 띄우기
                }
            })
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
