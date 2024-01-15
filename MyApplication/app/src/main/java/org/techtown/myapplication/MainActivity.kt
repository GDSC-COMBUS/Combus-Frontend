package org.techtown.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.techtown.myapplication.databinding.ActivityMainBinding
import org.techtown.myapplication.databinding.ActivityNoReservationBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater);
        setContentView(binding.root)

        val loginManager = LoginManager()
        //로그인 예시
        //"1234"는 사용자의 회원 번호(loginId)를 나타냅니다.
        // 이 부분은 사용자가 입력한 회원 번호를 서버에 전송하여 로그인을 시도하는 부분입니다. 실제 앱에서는 사용자가 입력한 값을 여기에 넣어주어야 합니다.
        //사용자 인터페이스(UI)에서는 로그인 화면에서 회원 번호를 입력하는 입력 필드가 있을 것이고,
        // 그 값을 코드에서 사용할 때 해당 입력 값을 가져와서 "loginManager.loginUser()" 메소드에 전달해주어야 합니다.
        // 사용자가 입력한 값을 동적으로 처리하도록 구현해야 합니다.
        loginManager.loginUser("1234", object : LoginCallback {
            override fun onLoginSuccess() {
                // 로그인이 성공한 경우에만 예약 내역 조회 시작
                val reservationManager = ReservationManager()
                reservationManager.getReservationStatus()
                //Retrofit을 사용하여 홈-예약 내역 API를 호출하고, 응답을 처리하는 기본 템플릿입니다.
                // 화면에 표시하는 로직은 예약 내역이 존재하는 경우와 존재하지 않는 경우에 따라 적절히 처리해주셔야 합니다.
            }

            override fun onLoginFailure() {
                // 로그인 실패 시 처리
                // 예를 들어, 에러 메시지 표시 등의 작업 수행
            }
        })

        binding.startButton.setOnClickListener {
            // 화면 전환 로직 추가
            val intent = Intent(this, Reserved::class.java)
            startActivity(intent)
        }
    }
}