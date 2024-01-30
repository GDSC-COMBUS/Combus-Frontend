package org.techtown.myapplication

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import org.techtown.myapplication.Retrofit.ApiManager_homeReservation
import org.techtown.myapplication.Retrofit.ApiManager_login
import org.techtown.myapplication.Retrofit.HomeReservationResponse
import org.techtown.myapplication.Retrofit.LoginRequest
import org.techtown.myapplication.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // 음성인식 기능
    private lateinit var speechRecognizer: SpeechRecognizer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startButton.setOnClickListener {
            val intent = Intent(this@MainActivity, Reserved::class.java)
            startActivity(intent)

//            val intent = Intent(this@MainActivity, NoReservation::class.java)
//            startActivity(intent)

            // 사용 예시
            val userService = ApiManager_login.create().loginUser(LoginRequest("1234"))
            val response = userService.execute()

            if (response.isSuccessful) {
                val loginResponse = response.body()
                if (loginResponse?.status == "OK") {
                    // 성공적으로 로그인한 경우
                    val userData = loginResponse.data
                    // userData.id, userData.name, userData.loginId 등으로 사용자 정보에 접근할 수 있습니다.

                    // API 호출 예시
                    val homeReservationService = ApiManager_homeReservation.create().getHomeReservation()
                    val call = homeReservationService.enqueue(object :
                        Callback<HomeReservationResponse> {
                        override fun onResponse(
                            call: Call<HomeReservationResponse>,
                            response: Response<HomeReservationResponse>
                        ) {
                            if (response.isSuccessful) {
                                val homeReservationResponse = response.body()
                                if (homeReservationResponse?.status == "OK") {
                                    // 홈 예약 내역이 존재하는 경우
                                    val reservationData = homeReservationResponse.data
                                    // reservationData.id, reservationData.boardingStop 등으로 예약 정보에 접근할 수 있습니다.
                                    Log.d("MainActivity", "Reservation exists: ${reservationData?.boardingStop}")
                                    // 다른 화면으로 전환
                                    // Intent를 통해 예약 정보를 ReservedActivity로 전달
                                    val intent = Intent(this@MainActivity, Reserved::class.java)
                                    intent.putExtra("reservationData", reservationData) // 다른 화면으로 데이터 전달 예시
                                    startActivity(intent)
                                    finish() // 현재 화면 종료

                                } else {
                                    // 홈 예약 내역이 존재하지 않는 경우
                                    Log.d("MainActivity", "No reservation exists")
                                    // 다른 화면으로 전환
                                    val intent = Intent(this@MainActivity, NoReservation::class.java)
                                    //intent.putExtra("userId", userData.id) // 다른 화면으로 데이터 전달 예시
                                    startActivity(intent)
                                    finish() // 현재 화면 종료
                                }
                            } else {
                                // 네트워크 오류 또는 서버 응답 오류
                                Log.d("MainActivity", "Network error or server response error: ${response.code()}")
                            }
                        }

                        override fun onFailure(call: Call<HomeReservationResponse>, t: Throwable) {
                            // 통신 실패 시
                            Log.d("MainActivity", "Call failed: ${t.message}")
                        }
                    })

                    // 다른 화면으로 전환
                    val intent = Intent(this@MainActivity, Reserved::class.java)
                    //intent.putExtra("userId", userData.id) // 다른 화면으로 데이터 전달 예시
                    startActivity(intent)
                    finish() // 현재 화면 종료
                } else {
                    // 로그인 실패한 경우
                    val detail = loginResponse?.detail ?: "Unknown error"
                    println("Login failed: $detail")
                }
            } else {
                // 네트워크 오류 또는 서버 응답 오류
                println("Network error or server response error: ${response.code()}")
            }
        }

        // 음성인식 기능 구현 시작

        //권한 설정
        requestPermission()

        // RecognizerIntent 생성
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)    // 여분의 키
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")         // 언어 설정

        // <말하기> 버튼 눌러서 음성인식 시작
        binding.speakButton.setOnClickListener {
            // 새 SpeechRecognizer 를 만드는 팩토리 메서드
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this@MainActivity)
            speechRecognizer.setRecognitionListener(recognitionListener)    // 리스너 설정
            speechRecognizer.startListening(intent)                         // 듣기 시작
        }
    }

    // 권한 설정 메소드(음성 인식 기능)
    private fun requestPermission() {
        // 버전 체크, 권한 허용했는지 체크
        if (Build.VERSION.SDK_INT >= 23 &&
            ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this@MainActivity,
                arrayOf(Manifest.permission.RECORD_AUDIO), 0)
        }
    }

    // 리스너 설정(음성 인식 기능)
    private val recognitionListener: RecognitionListener = object : RecognitionListener {
        // 말하기 시작할 준비가되면 호출
        override fun onReadyForSpeech(params: Bundle) {
            Toast.makeText(applicationContext, "음성인식 시작", Toast.LENGTH_SHORT).show()
            binding.tvState.text = "이제 말씀하세요!"
        }
        // 말하기 시작했을 때 호출
        override fun onBeginningOfSpeech() {
            binding.tvState.text = "잘 듣고 있어요."
        }
        // 입력받는 소리의 크기를 알려줌
        override fun onRmsChanged(rmsdB: Float) {}
        // 말을 시작하고 인식이 된 단어를 buffer에 담음
        override fun onBufferReceived(buffer: ByteArray) {}
        // 말하기를 중지하면 호출
        override fun onEndOfSpeech() {
            binding.tvState.text = "끝!"
        }
        // 오류 발생했을 때 호출
        override fun onError(error: Int) {
            val message = when (error) {
                SpeechRecognizer.ERROR_AUDIO -> "오디오 에러"
                SpeechRecognizer.ERROR_CLIENT -> "클라이언트 에러"
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "퍼미션 없음"
                SpeechRecognizer.ERROR_NETWORK -> "네트워크 에러"
                SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "네트웍 타임아웃"
                SpeechRecognizer.ERROR_NO_MATCH -> "찾을 수 없음"
                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "RECOGNIZER 가 바쁨"
                SpeechRecognizer.ERROR_SERVER -> "서버가 이상함"
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "말하는 시간초과"
                else -> "알 수 없는 오류임"
            }
            binding.tvState.text = "에러 발생: $message"
        }
        // 인식 결과가 준비되면 호출
        override fun onResults(results: Bundle) {
            // 말을 하면 ArrayList에 단어를 넣고 textView에 단어를 이어줌
            val matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            for (i in matches!!.indices) binding.textView7.text = matches[i]
        }
        // 부분 인식 결과를 사용할 수 있을 때 호출
        override fun onPartialResults(partialResults: Bundle) {}
        // 향후 이벤트를 추가하기 위해 예약
        override fun onEvent(eventType: Int, params: Bundle) {}
    }
}

