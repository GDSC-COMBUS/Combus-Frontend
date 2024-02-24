package org.techtown.myapplication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.text.Editable
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.techtown.myapplication.databinding.ActivityBoardingBusStopSttBinding
import android.os.Handler
import android.speech.tts.TextToSpeech
import java.util.Locale

class BoardingBusStopSTT : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var binding: ActivityBoardingBusStopSttBinding
    private var userId: Long = -1L
    private var boardingStop: String? = null
    // 음성인식 기능
    private lateinit var speechRecognizer: SpeechRecognizer
    private val RECORD_AUDIO_PERMISSION_CODE = 1
    // TextToSpeech 객체 선언
    private lateinit var tts: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBoardingBusStopSttBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // TextToSpeech 초기화
        tts = TextToSpeech(this, this)

        userId = intent.getLongExtra("userId", -1L)
        Log.d("userId_BoardingBusStopSTT", "$userId")

        // 음성인식 권한 설정
        requestPermission()

        // <말하기> 버튼 눌러서 음성인식 시작
        binding.speakButtonBoardingBusStopSTT.setOnClickListener {
            // 기존의 SpeechRecognizer가 존재하는지 확인하고 중지 및 해제
            if (::speechRecognizer.isInitialized) {
                speechRecognizer.stopListening()
                speechRecognizer.cancel()
            }

            // SpeechRecognizer 다시 생성 및 시작
            initializeSpeechRecognizer()
            speechRecognizer.startListening(intent)
        }
    }

    // TextToSpeech 초기화 완료 시 호출되는 콜백
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // TextToSpeech 초기화 성공 시 설정
            val locale = Locale("en", "US") // 영어 설정
            val result = tts.setLanguage(locale)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "The Language specified is not supported!")
            } else {
                // TTS 설정 완료 후 "승차정류장을 말씀해주세요" 음성 출력
                speakOut("Please tell me a boarding stop")
            }
        } else {
            Log.e("TTS", "Initialization Failed!")
        }
    }

    // 음성으로 메시지 출력하는 함수
    private fun speakOut(text: String) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= 23 &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
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
                        "Voice recognition rights are required.",
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
                Toast.makeText(applicationContext, "Start voice recognition", Toast.LENGTH_SHORT).show()
                binding.tvStateBoardingBusStopSTT.text = "이제 말씀하세요!"
            }

            override fun onBeginningOfSpeech() {
                binding.tvStateBoardingBusStopSTT.text = "잘 듣고 있어요."
            }

            override fun onRmsChanged(rmsdB: Float) {}

            override fun onBufferReceived(buffer: ByteArray) {}

            override fun onEndOfSpeech() {
                binding.tvStateBoardingBusStopSTT.text = "끝!"
            }

            override fun onError(error: Int) {
                val message = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> "오디오 에러"
                    SpeechRecognizer.ERROR_CLIENT -> "클라이언트 에러"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> {
                        runOnUiThread {
                            Toast.makeText(applicationContext, "Voice recognition rights are required.", Toast.LENGTH_SHORT).show()
                        }
                        "퍼미션 없음"
                    }
                    SpeechRecognizer.ERROR_NETWORK -> "네트워크 에러"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "네트웍 타임아웃"
                    SpeechRecognizer.ERROR_NO_MATCH -> "찾을 수 없음"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> {
                        runOnUiThread {
                            Toast.makeText(applicationContext, "Recognition service busy. Retrying...", Toast.LENGTH_SHORT).show()
                            // SpeechRecognizer를 다시 초기화하고 음성인식을 다시 시작
                            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this@BoardingBusStopSTT)
                            speechRecognizer.setRecognitionListener(recognitionListener)
                            speechRecognizer.startListening(intent)
                        }
                        "RECOGNIZER 가 바쁨"
                    }
                    SpeechRecognizer.ERROR_SERVER -> "서버가 이상함"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "말하는 시간초과"
                    else -> "알 수 없는 오류임"
                }
                binding.tvStateBoardingBusStopSTT.text = "에러 발생: $message"
            }

            override fun onResults(results: Bundle) {
                val matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                var resultText = matches?.get(0) ?: ""

                // 공백 제거
                resultText = resultText.replace("\\s".toRegex(), "")

                runOnUiThread {
                    binding.textViewBoardingBusStopSTT.text = resultText
                    boardingStop = resultText
                    Log.d("MainActivity", "Recognition result: $resultText")

                    Handler().postDelayed({
                        // BusSelectionSTT 페이지로 이동하는 Intent 생성
                        val busSelectionSTTIntent = Intent(this@BoardingBusStopSTT, BusSelectionSTT::class.java)
                        busSelectionSTTIntent.putExtra("boardingStop", boardingStop)
                        busSelectionSTTIntent.putExtra("userId", userId)
                        startActivity(busSelectionSTTIntent)
                        finish() // 현재 액티비티 종료
                    }, 2000) // 2초(2000밀리초) 지연
                }
            }


            override fun onPartialResults(partialResults: Bundle) {}

            override fun onEvent(eventType: Int, params: Bundle) {}
        }
    }

    // String을 Editable로 변환하는 확장 함수 정의
    private fun String?.toEditable(): Editable {
        return Editable.Factory.getInstance().newEditable(this ?: "")
    }

    private fun initializeSpeechRecognizer() {
        // RecognizerIntent 생성
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)    // 여분의 키
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")               // 언어 설정
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)

        // SpeechRecognizer 초기화 및 시작
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizer.setRecognitionListener(recognitionListener)
    }


    override fun onDestroy() {
        super.onDestroy()
        // TextToSpeech 사용 후에는 반드시 종료해야 함
        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
        // SpeechRecognizer도 종료
        if (::speechRecognizer.isInitialized) {
            speechRecognizer.stopListening()
            speechRecognizer.cancel()
        }
    }
}