package org.techtown.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.techtown.myapplication.Retrofit.ApiManager_BoardingBusStop
import org.techtown.myapplication.Retrofit.BoardingStop
import org.techtown.myapplication.Retrofit.LocationRequest
import org.techtown.myapplication.databinding.ActivityBoardingBusStopBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BoardingBusStop : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var mapView: MapView
    private lateinit var nearbyBusStops: List<BoardingStop> // 추가: 가까운 정류장 리스트 저장 변수

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityBoardingBusStopBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 위치 권한 체크
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            initializeMap() // 위치 권한이 있는 경우 지도 초기화
        } else {
            // 위치 권한이 없을 경우 권한을 요청
            requestLocationPermission()
        }

        // EditText에 대한 이벤트 핸들러 추가
        val searchBox = findViewById<EditText>(R.id.searchBox_boarding)
        searchBox.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // 텍스트가 변경된 후 호출되는 부분
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // 텍스트 변경 전 호출되는 부분
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 텍스트가 변경될 때 호출되는 부분
                val query = s.toString()
                searchBusStops(query)
            }
        })
    }

    // 권한 요청 메서드
    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_LOCATION_PERMISSION
        )
    }

    // 권한 요청 결과 처리
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_LOCATION_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initializeMap()  // 권한 허용된 경우 지도 초기화
                } else {
                    // 권한이 거부된 경우 처리
                    // 사용자에게 권한이 필요하다는 안내 메시지를 보여주거나 다른 처리를 수행
                }
            }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 1001
    }

    @SuppressLint("WrongViewCast")
    private fun initializeMap() {
        // 위치 권한 확인
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // 맵뷰 초기화
            mapView = findViewById(R.id.mapView)
            mapView.onCreate(null) // savedInstanceState 대신 null을 전달
            mapView.getMapAsync(this)

            // 예약 정보 가져오기 (초기에 100m 이내의 승차 정류장을 가져와 지도에 표시)
            getNearbyBusStopsForInitial()

            // EditText에 대한 이벤트 핸들러 추가
            val searchBox = findViewById<EditText>(R.id.searchBox_boarding)
            searchBox.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    // 텍스트가 변경된 후 호출되는 부분
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    // 텍스트 변경 전 호출되는 부분
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // 텍스트가 변경될 때 호출되는 부분
                    val query = s.toString()
                    searchBusStops(query)
                }
            })
        } else {
            // 위치 권한이 없을 경우 권한을 요청
            requestLocationPermission()
        }
    }

    // 초기에 100m 이내의 승차 정류장을 가져와 지도에 표시하는 함수
    private fun getNearbyBusStopsForInitial() {
        // 위치 권한 확인
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // 사용자의 현재 위치 가져오기
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

            location?.let {
                // 사용자의 실제 위치 좌표를 사용
                val locationRequest = LocationRequest(it.longitude, it.latitude)
                val busStopService = ApiManager_BoardingBusStop.create()

                busStopService.getNearbyBusStops(locationRequest)
                    .enqueue(object : Callback<List<BoardingStop>> {
                        override fun onResponse(call: Call<List<BoardingStop>>, response: Response<List<BoardingStop>>) {
                            if (response.isSuccessful) {
                                nearbyBusStops = response.body() ?: emptyList()

                                // 여기에서 nearbyBusStops를 활용하여 UI 업데이트 또는 다른 작업 수행

                                // 구글 지도에 버스 정류장 마커 표시
                                for (busStop in nearbyBusStops) {
                                    val busStopLatLng = LatLng(busStop.latitude, busStop.longitude)
                                    googleMap.addMarker(MarkerOptions().position(busStopLatLng).title(busStop.name))
                                }

                                // 초기에 가까운 정류장 리스트를 UI에 표시
                                updateUIWithNearbyBusStops(nearbyBusStops)
                            } else {
                                // API 호출은 성공했지만 서버에서 오류 응답을 받은 경우
                                // response.code(), response.message() 등을 활용하여 처리
                            }
                        }

                        override fun onFailure(call: Call<List<BoardingStop>>, t: Throwable) {
                            // API 호출에 실패한 경우
                            // 에러 메시지를 출력하거나 다른 예외 처리 작업을 수행
                        }
                    })
            }
        }
    }


    // 초기에 가까운 정류장 리스트를 UI에 표시하는 함수
    private fun updateUIWithNearbyBusStops(busStops: List<BoardingStop>) {
        val resultContainer = findViewById<LinearLayout>(R.id.resultContainer)

        // 기존의 뷰들을 모두 제거
        resultContainer.removeAllViews()

        // 검색 결과를 LinearLayout에 추가
        busStops.forEach { busStop ->
            // CardView로 감싼 레이아웃 추가
            val cardView = CardView(this)
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.topMargin = 8 // 간격 조절
            cardView.layoutParams = layoutParams
            cardView.radius = 16f // 둥근 모서리 반경 설정
            cardView.useCompatPadding = true // 내용물과 패딩을 호환성 있게 사용

            // TextView 추가
            val textView = TextView(this)
            textView.text = busStop.name
            textView.setOnClickListener {
                // 여기에 해당 승차 정류장을 클릭했을 때의 동작을 추가
                // 예를 들어, 클릭한 정류장의 위치로 지도를 이동하거나 해당 정류장의 상세 정보를 표시하는 등의 동작을 추가할 수 있음
                navigateToDetailsActivity(busStop)
            }

            // TextView를 CardView에 추가
            cardView.addView(textView)

            // CardView를 resultContainer에 추가
            resultContainer.addView(cardView)
        }
    }



    override fun onMapReady(gMap: GoogleMap) {
        googleMap = gMap

        // 위치 권한 확인
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // 위치 정보 가져오기 및 지도에 표시
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

            if (location != null) {
                val currentLatLng = LatLng(location.latitude, location.longitude)
                googleMap.addMarker(MarkerOptions().position(currentLatLng).title("현재 위치"))
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
            }
        } else {
            // 위치 권한이 없을 경우 권한을 요청
            requestLocationPermission()
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    private fun searchBusStops(query: String) {
        val locationRequest = LocationRequest(126.9723, 37.5562)
        val busStopService = ApiManager_BoardingBusStop.create()

        busStopService.getNearbyBusStops(locationRequest)
            .enqueue(object : Callback<List<BoardingStop>> {
                override fun onResponse(call: Call<List<BoardingStop>>, response: Response<List<BoardingStop>>) {
                    if (response.isSuccessful) {
                        val busStops = response.body()
                        val filteredBusStops = busStops?.filter { it.name.contains(query, ignoreCase = true) }

                        updateUIWithSearchResults(filteredBusStops)
                    } else {
                        // API 호출은 성공했지만 서버에서 오류 응답을 받은 경우
                        // response.code(), response.message() 등을 활용하여 처리
                    }
                }

                override fun onFailure(call: Call<List<BoardingStop>>, t: Throwable) {
                    // API 호출에 실패한 경우
                    // 에러 메시지를 출력하거나 다른 예외 처리 작업을 수행
                }
            })
    }

    private fun updateUIWithSearchResults(busStops: List<BoardingStop>?) {
        val resultContainer = findViewById<LinearLayout>(R.id.resultContainer)

        // 기존의 뷰들을 모두 제거
        resultContainer.removeAllViews()

        // 검색 결과를 LinearLayout에 추가
        busStops?.forEach { busStop ->
            // CardView로 감싼 레이아웃 추가
            val cardView = CardView(this)
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.topMargin = 8 // 간격 조절
            cardView.layoutParams = layoutParams
            cardView.radius = 16f // 둥근 모서리 반경 설정
            cardView.useCompatPadding = true // 내용물과 패딩을 호환성 있게 사용

            // TextView 추가
            val textView = TextView(this)
            textView.text = busStop.name
            textView.setOnClickListener {
                // 여기에 해당 승차 정류장을 클릭했을 때의 동작을 추가
                // 예를 들어, 클릭한 정류장의 위치로 지도를 이동하거나 해당 정류장의 상세 정보를 표시하는 등의 동작을 추가할 수 있음
                navigateToDetailsActivity(busStop)
            }

            // TextView를 CardView에 추가
            cardView.addView(textView)

            // CardView를 resultContainer에 추가
            resultContainer.addView(cardView)
        }
    }
    // 클릭한 정류장의 정보를 다른 액티비티로 전달하면서 액티비티 전환
    private fun navigateToDetailsActivity(busStop: BoardingStop) {
        val intent = Intent(this, BusSelection::class.java)
        intent.putExtra("busStop", busStop)
        startActivity(intent)
    }
}
