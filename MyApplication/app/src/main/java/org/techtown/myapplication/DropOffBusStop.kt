package org.techtown.myapplication

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
import org.techtown.myapplication.Retrofit.ApiManager_DropOffBusStop
import org.techtown.myapplication.Retrofit.ApiManager_ReservationComplete
import org.techtown.myapplication.Retrofit.ApiResponse
import org.techtown.myapplication.Retrofit.DropOffStop
import org.techtown.myapplication.Retrofit.LocationRequest
import org.techtown.myapplication.Retrofit.ReservationComplete
import org.techtown.myapplication.databinding.ActivityDropOffBusStopBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.app.AlertDialog
import android.location.Location

class DropOffBusStop : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var mapView: MapView
    private var selectedDropOffBusStop: DropOffStop? = null
    private var busStops: List<DropOffStop>? = null
    private var userId: Long = -1L // 사용자 ID를 저장할 변수 추가

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityDropOffBusStopBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Intent를 통해 전달된 데이터 받기
        userId = intent.getLongExtra("userId", -1L) // 사용자 ID를 받아옴

        // 위치 권한이 허용되었는지 확인
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            initializeMap()

            // 사용자의 현재 위치를 가져와 하차 정류소 목록을 업데이트
            fetchAndDisplayDropOffBusStops()
        } else {
            requestLocationPermission()
        }

        val searchBox = findViewById<EditText>(R.id.searchBox_drop_off)
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

        val resultContainer = findViewById<LinearLayout>(R.id.resultContainer)

        for (i in 0 until resultContainer.childCount) {
            val cardView = resultContainer.getChildAt(i) as? CardView
            cardView?.setOnClickListener {
                //선택한 하차 정류소에 대한 정보를 저장
                val position = resultContainer.indexOfChild(cardView)
                selectedDropOffBusStop = busStops?.get(position)

                navigateToReservedPage()
                //예약 페이지로 이동+서버에 예약 정보를 전송+확인창 띄우기
            }
        }
    }

    private fun fetchAndDisplayDropOffBusStops() {
        // 위치 권한이 허용된 경우에만 현재 위치 정보를 가져옴
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

            location?.let {
                val locationRequest = LocationRequest(it.longitude, it.latitude)
                val busStopService = ApiManager_DropOffBusStop.create()

                busStopService.getDropOffBusStops(locationRequest)
                    .enqueue(object : Callback<List<DropOffStop>> {
                        override fun onResponse(
                            call: Call<List<DropOffStop>>,
                            response: Response<List<DropOffStop>>
                        ) {
                            if (response.isSuccessful) {
                                busStops = response.body()

                                // 구글 지도에 버스 정류장 마커 표시
                                for (busStop in busStops.orEmpty()) {
                                    val busStopLatLng = LatLng(busStop.latitude, busStop.longitude)
                                    googleMap.addMarker(
                                        MarkerOptions().position(busStopLatLng).title(busStop.name)
                                    )?.snippet = "정류장 이름: ${busStop.name}"
                                }

                                // 초기에 가져온 목록을 UI에 표시
                                updateUIWithSearchResults(busStops)
                            } else {
                                // API 호출은 성공했지만 서버에서 오류 응답을 받은 경우
                                // response.code(), response.message() 등을 활용하여 처리
                            }
                        }

                        override fun onFailure(call: Call<List<DropOffStop>>, t: Throwable) {
                            // API 호출에 실패한 경우
                            // 에러 메시지를 출력하거나 다른 예외 처리 작업을 수행
                        }
                    })
            }
        }
    }


    private fun navigateToDetailsActivity(busStop: DropOffStop) {
        val intent = Intent(this, BusSelection::class.java)
        intent.putExtra("busStop", busStop)
        startActivity(intent)
    }

    private fun sendReservationToServer(reservation: ReservationComplete) {
        val apiManager = ApiManager_ReservationComplete.create()
        // Retrofit을 사용하여 서버 통신을 위한 인터페이스 생성

        // 서버에 예약 정보 전송
        apiManager.createReservation(reservation)
            .enqueue(object : Callback<ApiResponse> {
                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                    if (response.isSuccessful) {
                        // 서버 응답이 성공적으로 수신된 경우
                        val apiResponse = response.body()

                        // 여기에서 apiResponse를 활용하여 성공 또는 실패에 대한 처리를 수행
                        if (apiResponse != null) {
                            // 성공적으로 예약이 생성된 경우
                            // apiResponse의 상세 정보에 따라 적절한 작업 수행
                            if (apiResponse.status == "CREATED") {
                                // 예약이 성공적으로 생성된 경우
                                // 예약 성공 메시지를 사용자에게 보여줄 수 있습니다.
                                showToast(apiResponse.detail)
                            } else {
                                // 예약이 실패한 경우
                                // 실패 상세 정보에 따라 적절한 에러 메시지를 사용자에게 보여줄 수 있습니다.
                                showToast(apiResponse.detail)
                            }
                        }
                    } else {
                        // 서버에서 오류 응답을 받음
                        // response.code(), response.message() 등을 활용하여 처리
                        showToast("서버 오류: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    // 서버에 예약 정보 전송 실패
                    // 에러 메시지를 출력하거나 다른 예외 처리 작업을 수행
                    showToast("예약 실패: ${t.message}")
                }
            })
    }

    // showToast 함수는 사용자에게 메시지를 보여주는 함수입니다.
    fun showToast(message: String) {
        // TODO: 사용자에게 메시지를 보여주는 로직을 여기에 추가
    }


    private fun navigateToReservedPage() {
        //확인창 띄우기
        val builder = AlertDialog.Builder(this)

        builder.setTitle("예약 확인창")
            .setMessage("정말로 예약하시겠습니까?")
            .setPositiveButton("네") { dialog, which ->
                // 처리할 작업을 여기에 추가하세요.
                // 예를 들어, 어떤 동작을 수행하거나 다른 함수를 호출할 수 있습니다.
                // 예: performAction()
                // 서버로 예약 정보 전송하기
                // 예약 정보 생성
                val reservation = ReservationComplete(
                    boardingStop = "승차 정류소 고유 번호",  // TODO: 실제 데이터로 대체
                    dropStop = selectedDropOffBusStop?.arsId ?: "",  // 수정된 부분
                    vehId = "버스 ID",  // TODO: 실제 데이터로 대체
                    busRouteNm = "버스 노선 번호"  // TODO: 실제 데이터로 대체
                )

                // 서버에 예약 정보 전송
                sendReservationToServer(reservation)

                //예약 페이지(Reserved 액티비티)로 이동
                val intent = Intent(this, Reserved::class.java)
                intent.putExtra("dropOffBusStop", selectedDropOffBusStop)
                startActivity(intent)
            }
            .setNegativeButton("아니요") { dialog, which ->
                // 취소 또는 다른 작업을 수행할 수 있습니다.
            }
            .show()
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_LOCATION_PERMISSION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_LOCATION_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initializeMap()
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

    private fun initializeMap() {
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mapView = findViewById(R.id.map_boarding)
            mapView.onCreate(null)
            mapView.getMapAsync(this)
            // 사용자의 현재 위치를 가져와 하차 정류소 목록을 업데이트
            fetchAndDisplayDropOffBusStops()
        } else {
            requestLocationPermission()
        }
    }


    /*
    private fun initializeMap() {
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val locationRequest = LocationRequest(126.9723, 37.5562)
            val busStopService = ApiManager_DropOffBusStop.create()

            // 하차 정류소 정보 가져오기
            busStopService.getDropOffBusStops()
                .enqueue(object : Callback<List<DropOffStop>> {
                    override fun onResponse(
                        call: Call<List<DropOffStop>>,
                        response: Response<List<DropOffStop>>
                    ) {
                        if (response.isSuccessful) {
                            busStops = response.body()

                            // 구글 지도에 버스 정류장 마커 표시
                            for (busStop in busStops.orEmpty()) {
                                val busStopLatLng = LatLng(busStop.latitude, busStop.longitude)
                                googleMap.addMarker(
                                    MarkerOptions().position(busStopLatLng).title(busStop.name)
                                )
                            }
                        } else {
                            // API 호출은 성공했지만 서버에서 오류 응답을 받은 경우
                            // response.code(), response.message() 등을 활용하여 처리
                        }
                    }

                    override fun onFailure(call: Call<List<DropOffStop>>, t: Throwable) {
                        // API 호출에 실패한 경우
                        // 에러 메시지를 출력하거나 다른 예외 처리 작업을 수행
                    }
                })

            mapView = findViewById(R.id.mapView)
            mapView.onCreate(null)
            mapView.getMapAsync(this)
        } else {
            requestLocationPermission()
        }
    }*/

    override fun onMapReady(gMap: GoogleMap) {
        googleMap = gMap

        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

            if (location != null) {
                val currentLatLng = LatLng(location.latitude, location.longitude)
                googleMap.addMarker(MarkerOptions().position(currentLatLng).title("현재 위치"))
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
            }
        } else {
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
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var location: Location? = null

        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            // 위치 정보를 사용하는 코드...
        } else {
            // 위치 권한이 없는 경우 처리
            // 사용자에게 권한을 요청하거나 다른 처리를 수행
            requestLocationPermission()
        }

        location?.let {
            val locationRequest = LocationRequest(it.longitude, it.latitude)
            val busStopService = ApiManager_DropOffBusStop.create()

            busStopService.getDropOffBusStops(locationRequest)
                .enqueue(object : Callback<List<DropOffStop>> {
                    override fun onResponse(
                        call: Call<List<DropOffStop>>,
                        response: Response<List<DropOffStop>>
                    ) {
                        if (response.isSuccessful) {
                            val busStops = response.body()
                            val filteredBusStops = busStops?.filter { it.name.contains(query, ignoreCase = true) }

                            updateUIWithSearchResults(filteredBusStops)
                        } else {
                            // API 호출은 성공했지만 서버에서 오류 응답을 받은 경우
                            // response.code(), response.message() 등을 활용하여 처리
                        }
                    }

                    override fun onFailure(call: Call<List<DropOffStop>>, t: Throwable) {
                        // API 호출에 실패한 경우
                        // 에러 메시지를 출력하거나 다른 예외 처리 작업을 수행
                    }
                })
        }
    }


    private fun updateUIWithSearchResults(busStops: List<DropOffStop>?) {
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
            layoutParams.topMargin = 8
            cardView.layoutParams = layoutParams
            cardView.radius = 16f
            cardView.useCompatPadding = true

            // TextView 추가
            val textView = TextView(this)
            textView.text = busStop.name
            textView.setOnClickListener {
                navigateToDetailsActivity(busStop)
            }

            // TextView를 CardView에 추가
            cardView.addView(textView)

            // CardView를 resultContainer에 추가
            resultContainer.addView(cardView)
        }
    }

    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 1001
    }
}
