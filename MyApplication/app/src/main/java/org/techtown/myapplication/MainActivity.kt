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
    private val RECORD_AUDIO_PERMISSION_CODE = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 음성인식 기능 구현 시작

        // 권한 설정
        requestPermission()

        // <말하기> 버튼 눌러서 음성인식 시작
        binding.speakButton.setOnClickListener {
            // 기존의 SpeechRecognizer가 존재하는지 확인하고 중지 및 해제
            if (::speechRecognizer.isInitialized) {
                speechRecognizer.stopListening()
                speechRecognizer.cancel()
            }

            // SpeechRecognizer 다시 생성 및 시작
            initializeSpeechRecognizer()
            speechRecognizer.startListening(intent)
        }

        binding.startButton.setOnClickListener {
            val intent = Intent(this@MainActivity, Reserved::class.java)
            startActivity(intent)

            val userService = ApiManager_login.create().loginUser(LoginRequest("1234"))
            val response = userService.execute()

            if (response.isSuccessful) {
                val loginResponse = response.body()
                if (loginResponse?.status == "OK") {
                    val userData = loginResponse.data

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
                                    val reservationData = homeReservationResponse.data
                                    Log.d("MainActivity", "Reservation exists: ${reservationData?.boardingStop}")
                                    val intent = Intent(this@MainActivity, Reserved::class.java)
                                    intent.putExtra("reservationData", reservationData)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    Log.d("MainActivity", "No reservation exists")
                                    val intent = Intent(this@MainActivity, NoReservation::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                            } else {
                                Log.d("MainActivity", "Network error or server response error: ${response.code()}")
                            }
                        }

                        override fun onFailure(call: Call<HomeReservationResponse>, t: Throwable) {
                            Log.d("MainActivity", "Call failed: ${t.message}")
                        }
                    })

                    val intent = Intent(this@MainActivity, Reserved::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val detail = loginResponse?.detail ?: "Unknown error"
                    println("Login failed: $detail")
                }
            } else {
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
    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= 23 &&
            ContextCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                RECORD_AUDIO_PERMISSION_CODE
            )
        } else {
            // 이미 권한이 허용된 경우 또는 권한을 허용한 경우 처리
            // 여기에 추가적인 로직을 넣을 수 있습니다.
            initializeSpeechRecognizer()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            RECORD_AUDIO_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 사용자가 권한을 허용한 경우 처리
                    // 여기에 추가적인 로직을 넣을 수 있습니다.
                    initializeSpeechRecognizer()
                } else {
                    // 사용자가 권한을 거부한 경우 메시지 표시 또는 다른 처리
                    Toast.makeText(
                        applicationContext,
                        "음성인식 권한이 필요합니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            // 다른 권한에 대한 처리 추가 가능
        }
    }


    private val recognitionListener: RecognitionListener by lazy {
        object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle) {
                Toast.makeText(applicationContext, "음성인식 시작", Toast.LENGTH_SHORT).show()
                binding.tvState.text = "이제 말씀하세요!"
            }

            override fun onBeginningOfSpeech() {
                binding.tvState.text = "잘 듣고 있어요."
            }

            override fun onRmsChanged(rmsdB: Float) {}

            override fun onBufferReceived(buffer: ByteArray) {}

            override fun onEndOfSpeech() {
                binding.tvState.text = "끝!"
            }

            override fun onError(error: Int) {
                val message = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> "오디오 에러"
                    SpeechRecognizer.ERROR_CLIENT -> "클라이언트 에러"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> {
                        runOnUiThread {
                            Toast.makeText(applicationContext, "음성인식 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
                        }
                        "퍼미션 없음"
                    }
                    SpeechRecognizer.ERROR_NETWORK -> "네트워크 에러"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "네트웍 타임아웃"
                    SpeechRecognizer.ERROR_NO_MATCH -> "찾을 수 없음"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> {
                        runOnUiThread {
                            Toast.makeText(applicationContext, "Recognition service busy. Retrying...", Toast.LENGTH_SHORT).show()
                            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this@MainActivity)
                            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this@MainActivity)
                            speechRecognizer.setRecognitionListener(recognitionListener)
                            speechRecognizer.startListening(intent)
                        }
                        "RECOGNIZER 가 바쁨"
                    }
                    SpeechRecognizer.ERROR_SERVER -> "서버가 이상함"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "말하는 시간초과"
                    else -> "알 수 없는 오류임"
                }
                binding.tvState.text = "에러 발생: $message"
            }

            override fun onResults(results: Bundle) {
                val matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val resultText = matches?.joinToString("\n")

                runOnUiThread {
                    binding.textView7.text = resultText
                    Log.d("MainActivity", "Recognition result: $resultText")
                }
            }

            override fun onPartialResults(partialResults: Bundle) {}

            override fun onEvent(eventType: Int, params: Bundle) {}
        }
    }

    private fun initializeSpeechRecognizer() {
        // RecognizerIntent 생성
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)    // 여분의 키
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")               // 언어 설정
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)

        // SpeechRecognizer 초기화 및 시작
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this@MainActivity)
        speechRecognizer.setRecognitionListener(recognitionListener)
    }


    override fun onDestroy() {
        super.onDestroy()
        if (::speechRecognizer.isInitialized) {
            speechRecognizer.stopListening()
            speechRecognizer.cancel()
        }
    }
}