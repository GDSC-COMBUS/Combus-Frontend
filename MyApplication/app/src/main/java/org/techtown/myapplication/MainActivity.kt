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
import android.os.AsyncTask
import org.techtown.myapplication.Retrofit.LoginResponse
import org.techtown.myapplication.Retrofit.ReservationData

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // 음성인식 기능
    private lateinit var speechRecognizer: SpeechRecognizer
    private val RECORD_AUDIO_PERMISSION_CODE = 1

    // 예약 정보를 담는 변수 선언
    private var reservationData: ReservationData? = null


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

        binding.startButton.setOnClickListener {
            // AsyncTask를 사용하여 백그라운드 스레드에서 네트워크 작업 실행
            NetworkTask().execute()
        }
    }

    private inner class NetworkTask : AsyncTask<Void, Void, Response<LoginResponse>>() {

        override fun doInBackground(vararg params: Void?): Response<LoginResponse>? {
            try {
                // Retrofit을 사용하여 네트워크 호출
                val service = ApiManager_login.create()
                val membershipNumberEditText = findViewById<EditText>(R.id.membershipNumber)
                val loginId = membershipNumberEditText.text.toString()

                val loginRequest = LoginRequest(loginId)
                return service.loginUser(loginRequest).execute()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(result: Response<LoginResponse>?) {
            super.onPostExecute(result)
            // 네트워크 작업이 완료된 후에 UI를 업데이트하는 코드
            if (result != null && result.isSuccessful) {
                // 성공적으로 응답을 받았을 때의 처리
                val loginResponse = result.body()
                handleNetworkResponse(loginResponse)
            } else {
                // 네트워크 작업 실패 시의 처리
                // 실패 상황에 대한 메시지 또는 로직 추가
                Toast.makeText(
                    applicationContext,
                    "로그인 api 네트워크 작업 실패",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun handleNetworkResponse(response: LoginResponse?) {
        // 응답을 처리하는 코드
        if (response != null && response.code == "SIGNIN_SUCCESS") {
            // 로그인 성공 처리
            val userData = response.data
            // 여기서 userData를 사용하여 세션 생성 또는 화면 전환 등을 수행할 수 있습니다.

            // 예시로 성공한 경우 Toast 메시지 출력
            Toast.makeText(
                applicationContext,
                "로그인 성공 - 사용자: ${userData?.name}",
                Toast.LENGTH_SHORT
            ).show()

            // 추가적인 로그
            Log.d("MainActivity", "로그인 성공 - 사용자: ${userData?.name}")

            if (userData?.id != null) {
                checkReservation(userData.id)
            }

            userData?.cookie?.let {
                ApiManager_homeReservation.setUserCookie(it)
                // 여기에서 예약 내역을 확인하도록 수정
            }
        } else {
            // 로그인 실패 처리
            // response.detail에 실패 이유가 들어있을 수 있습니다.
            Toast.makeText(
                applicationContext,
                "로그인 실패: ${response?.detail ?: "알 수 없는 이유"}",
                Toast.LENGTH_SHORT
            ).show()

            // 추가적인 로그
            Log.d("MainActivity", "로그인 실패: ${response?.detail ?: "알 수 없는 이유"}")
        }
    }

    // 예약 내역 확인 함수
    private fun checkReservation(userId: Long?) {
        // AsyncTask를 사용하여 백그라운드 스레드에서 네트워크 작업 실행
        val checkReservationTask = CheckReservationTask()
        checkReservationTask.execute(userId)

        // AsyncTask가 완료될 때까지 기다리지 않고 바로 다음으로 진행하지 않도록 수정
        val homeReservationResponse = checkReservationTask.get()

        Toast.makeText(
            applicationContext,
            "예약 api가 실행되긴 하나봐",
            Toast.LENGTH_SHORT
        ).show()

        if (homeReservationResponse != null && homeReservationResponse.isSuccessful) {
            val response = homeReservationResponse.body()

            /*
            Toast.makeText(
                applicationContext,
                "예약 api 응답이 과연 오는가 두둥탁",
                Toast.LENGTH_SHORT
            ).show()*/

            Toast.makeText(
                applicationContext,
                "로그인 성공 - 사용자: ${response?.data?.date}",
                Toast.LENGTH_SHORT
            ).show()

            /*
            if (response?.detail == "예약 내역을 성공적으로 불러왔습니다") {
                // 예약 내역이 있을 경우 ReservedActivity로 이동
                val reservedIntent = Intent(this@MainActivity, Reserved::class.java)
                startActivity(reservedIntent)
                finish() // 현재 액티비티 종료
            } else if (response?.detail == "예약 내역 존재하지 않습니다") {
                // 예약 내역이 없을 경우 NoReservationActivity로 이동
                val noReservationIntent = Intent(this@MainActivity, NoReservation::class.java)
                startActivity(noReservationIntent)
                finish() // 현재 액티비티 종료
            }*/


            if (response?.data != null) {
                // 예약 내역이 있을 경우 ReservedActivity로 이동
                val reservedIntent = Intent(this@MainActivity, Reserved::class.java)
                reservedIntent.putExtra("reservationData", response.data)
                startActivity(reservedIntent)
                finish() // 현재 액티비티 종료
            } else {
                // 예약 내역이 없을 경우 NoReservationActivity로 이동
                val noReservationIntent = Intent(this@MainActivity, NoReservation::class.java)
                // 사용자 ID를 전달
                noReservationIntent.putExtra("userId", userId)
                startActivity(noReservationIntent)
                finish() // 현재 액티비티 종료
            }
        } else {
            // 네트워크 작업 실패 시의 처리
            // 실패 상황에 대한 메시지 또는 로직 추가
            Toast.makeText(
                applicationContext,
                "예약 api 네트워크 작업 실패",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private inner class CheckReservationTask : AsyncTask<Long?, Void, Response<HomeReservationResponse>>() {

        override fun doInBackground(vararg params: Long?): Response<HomeReservationResponse>? {
            try {


                // Retrofit을 사용하여 네트워크 호출
                val service = ApiManager_homeReservation.create()
                return service.getHomeReservation(params.firstOrNull()).execute()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
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