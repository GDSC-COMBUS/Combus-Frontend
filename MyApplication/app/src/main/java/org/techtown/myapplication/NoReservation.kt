package org.techtown.myapplication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.techtown.myapplication.Retrofit.ApiManager_ReservationSTT
import org.techtown.myapplication.Retrofit.ReservationSTTRequest
import org.techtown.myapplication.databinding.ActivityNoReservationBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NoReservation : AppCompatActivity() {

    private lateinit var binding: ActivityNoReservationBinding

    // 음성인식 기능
    private lateinit var speechRecognizer: SpeechRecognizer
    private val RECORD_AUDIO_PERMISSION_CODE = 1

    // 사용자가 말한 정보를 저장할 변수들
    private var userId: Long? = null // 기존의 userId 변수는 그대로 유지
    private var userIdInt: Int? = null // Int 타입의 userId 변수 추가
    private var boardingStop: String? = null
    private var dropStop: String? = null
    private var busRouteNm: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoReservationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // RecognizerIntent 생성
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)    // 여분의 키
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")         // 언어 설정

        // Intent를 통해 전달된 데이터 받기
        val noReservationIntent = getIntent()
        userId = noReservationIntent.getLongExtra("userId", -1L) // 기본값으로 -1L을 설정하여 오류 방지
        Log.d("userIdCheck_noReservation", "$userId")

        // 버튼 클릭 시 화면 전환(승차 정류장 선택 및 예약하기 화면으로)
        binding.buttonStartReservation.setOnClickListener {
            // Intent를 사용하여 화면 전환
            val boardingBusStopintent = Intent(this, BoardingBusStop::class.java)
            boardingBusStopintent.putExtra("userId", userId)
            startActivity(boardingBusStopintent)
        }

        //누르면 BoardingBusStopSTT화면으로 넘어가고, userId도 같이 전달
        binding.buttonStartReservationSTT.setOnClickListener {
            // Intent를 사용하여 화면 전환
            val boardingBusStopSTTintent = Intent(this, BoardingBusStopSTT::class.java)
            boardingBusStopSTTintent.putExtra("userId", userId)
            startActivity(boardingBusStopSTTintent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::speechRecognizer.isInitialized) {
            speechRecognizer.stopListening()
            speechRecognizer.cancel()
        }
    }
}
