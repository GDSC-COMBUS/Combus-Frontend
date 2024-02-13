package org.techtown.myapplication

import android.Manifest
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
import android.graphics.Color
import android.location.Criteria
import android.location.Location
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.widget.Button
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.Marker
import org.techtown.myapplication.Retrofit.ApiManager_BoardingBusStop
import org.techtown.myapplication.Retrofit.BoardingStop

class DropOffBusStop : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var mapView: MapView
    private var isMapReady = false
    private var dropOffBusStops: List<DropOffStop>? = null
    private var selectedDropOffBusStop: DropOffStop? = null
    private var busStops: List<DropOffStop>? = null
    private var userId: Long = -1L // 사용자 ID를 저장할 변수 추가
    private var arsId_boarding: String? = null
    private var busRouteId: String? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityDropOffBusStopBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val backButton = findViewById<Button>(R.id.backButton5)
        backButton.setOnClickListener {
            val intent = Intent(this, NoReservation::class.java)
            startActivity(intent)
        }

        // Intent를 통해 전달된 데이터 받기
        userId = intent.getLongExtra("userId", -1L) // 사용자 ID를 받아옴

        val searchBox = findViewById<EditText>(R.id.searchBox_drop_off)
        searchBox.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()
                searchBusStops(query)
            }
        })

        mapView = findViewById(R.id.map_drop_off)
        mapView.onCreate(null)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // 위치 권한 체크 및 초기화 함수 호출을 onMapReady 콜백 내부로 이동
        initializeMap()
    }

    private fun checkLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            val dialogBuilder = androidx.appcompat.app.AlertDialog.Builder(this)
            dialogBuilder.setMessage("이 앱을 사용하려면 위치 권한이 필요합니다. 권한을 부여하시겠습니까?")
                .setCancelable(false)
                .setPositiveButton("네") { dialog, id ->
                    ActivityCompat.requestPermissions(
                        this@DropOffBusStop,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        DropOffBusStop.REQUEST_LOCATION_PERMISSION
                    )
                }
                .setNegativeButton("아니오") { dialog, id ->
                    Toast.makeText(this, "위치 권한이 거부되었습니다.", Toast.LENGTH_SHORT).show()
                }
            val alert = dialogBuilder.create()
            alert.setTitle("권한 요청")
            alert.show()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                DropOffBusStop.REQUEST_LOCATION_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            DropOffBusStop.REQUEST_LOCATION_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initializeMap()
                } else {
                    Toast.makeText(
                        this,
                        "위치 권한이 거부되었습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
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

    private fun initializeMap() {
        mapView.getMapAsync(this)
    }

    private fun getCurrentLocationAndLoadBusStops() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val criteria = Criteria()
        criteria.accuracy = Criteria.ACCURACY_FINE // 정확도를 설정합니다. ACCURACY_FINE은 GPS를 사용합니다.

        val locationProvider: String? = locationManager.getBestProvider(criteria, true)

        if (checkLocationPermission()) {
            val location: Location? = locationProvider?.let { locationManager.getLastKnownLocation(it) }

            if (location != null) {
                Log.d("CurrentLocation", "Latitude: ${location.latitude}, Longitude: ${location.longitude}")
                val currentLatLng = LatLng(location.latitude, location.longitude)
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                getDropOffBusStops(arsId_boarding, busRouteId)
            } else {
                Toast.makeText(this, "현재 위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        } else {
            requestLocationPermission()
        }
    }

    private fun getDropOffBusStops(arsId_boarding: String?, busRouteId: String?) {
        val busStopService = ApiManager_DropOffBusStop.create()

        busStopService.getDropOffBusStops(arsId_boarding, busRouteId)
            .enqueue(object : Callback<List<DropOffStop>> {
                override fun onResponse(call: Call<List<DropOffStop>>, response: Response<List<DropOffStop>>) {
                    if (response.isSuccessful) {
                        // 성공적인 응답을 로깅합니다.
                        Log.d("OMG", "Successful response: ${response.body()}")
                        dropOffBusStops = response.body()

                        val message = "주변 버스 정류장 수: ${dropOffBusStops?.size ?: 0}"
                        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()

                        dropOffBusStops?.let { busStops ->
                            for (busStop in busStops) {
                                val busStopLatLng = LatLng(busStop.latitude, busStop.longitude)
                                val marker = googleMap.addMarker(MarkerOptions().position(busStopLatLng).title(busStop.name))
                                marker?.snippet = "ARS 번호: ${busStop.arsId}"
                            }
                            googleMap.setOnMarkerClickListener { clickedMarker ->
                                showInfoWindow(clickedMarker)
                                true
                            }
                            updateUIWithDropOffBusStops(busStops)
                        }
                    } else {
                        // Handle unsuccessful response
                        // 실패한 응답을 로깅합니다.
                        Log.e("OMG", "Unsuccessful response: ${response.code()}")
                        Toast.makeText(applicationContext, "API 호출에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<DropOffStop>>, t: Throwable) {
                    // Handle failure
                    // 호출 실패를 로깅합니다.
                    Log.e("OMG", "API call failed", t)
                    Toast.makeText(applicationContext, "API 호출에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun showInfoWindow(marker: Marker) {
        marker.showInfoWindow()
    }

    private fun searchBusStops(query: String) {
        val filteredBusStops = dropOffBusStops?.filter { it.name.contains(query, ignoreCase = true) }
        updateUIWithSearchResults(filteredBusStops)
    }

    private fun updateUIWithDropOffBusStops(busStops: List<DropOffStop>) {
        val resultContainer = findViewById<LinearLayout>(R.id.resultContainer_drop_off)
        resultContainer.removeAllViews()

        busStops.forEach { busStop ->
            val cardView = CardView(this)
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.topMargin = 8
            cardView.layoutParams = layoutParams
            cardView.radius = 16f
            cardView.useCompatPadding = true
            cardView.isClickable = true // 이 부분을 추가합니다.

            val textView = TextView(this)
            textView.text = "${busStop.name} (${busStop.arsId})"
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f) // 글씨 크기 조정
            textView.gravity = Gravity.CENTER_VERTICAL // 세로로 중앙 정렬
            textView.setTextColor(Color.BLACK) // 텍스트 색상을 검정색으로 설정

            cardView.addView(textView)
            resultContainer.addView(cardView)

            // CardView를 클릭했을 때 BusSelection 페이지로 이동하는 클릭 리스너 추가
            cardView.setOnClickListener {
                navigateToReservedPage()
            }

            // CardView 크기 조정
            val params = cardView.layoutParams as LinearLayout.LayoutParams
            params.height = resources.getDimensionPixelSize(R.dimen.cardview_height)
            cardView.layoutParams = params
        }
    }

    private fun updateUIWithSearchResults(busStops: List<DropOffStop>?) {
        val resultContainer = findViewById<LinearLayout>(R.id.resultContainer_drop_off)
        resultContainer.removeAllViews()

        busStops?.forEach { busStop ->
            val cardView = CardView(this)
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.topMargin = 8
            cardView.layoutParams = layoutParams
            cardView.radius = 16f
            cardView.useCompatPadding = true

            val textView = TextView(this)
            textView.text = "${busStop.name} (${busStop.arsId})"
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            textView.gravity = Gravity.CENTER_VERTICAL
            textView.setTextColor(Color.BLACK)
            textView.setOnClickListener {
                navigateToReservedPage()
            }

            cardView.addView(textView)
            resultContainer.addView(cardView)

            // CardView 크기 조정
            val params = cardView.layoutParams as LinearLayout.LayoutParams
            params.height = resources.getDimensionPixelSize(R.dimen.cardview_height)
            cardView.layoutParams = params
        }
    }

    /*private fun navigateToDetailsActivity(busStop: DropOffStop) {
        val intent = Intent(this, Reserved::class.java)
        intent.putExtra("busStop", busStop)
        startActivity(intent)
    }*/

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

    override fun onMapReady(gMap: GoogleMap) {
        googleMap = gMap
        isMapReady = true

        // 위치 권한 체크 후 현재 위치 정보를 가져와서 버스 정류장 로드
        if (checkLocationPermission()) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    getCurrentLocationAndLoadBusStops()
                }
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
}
