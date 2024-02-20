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
import android.os.AsyncTask
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.widget.Button
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.Marker
import org.techtown.myapplication.Retrofit.ApiManager_BoardingBusStop
import org.techtown.myapplication.Retrofit.ApiManager_homeReservation
import org.techtown.myapplication.Retrofit.BoardingStop
import org.techtown.myapplication.Retrofit.HomeReservationResponse

class DropOffBusStop : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var mapView: MapView
    private var isMapReady = false
    private var dropOffBusStops: List<DropOffStop>? = null
    private var selectedDropOffBusStop: DropOffStop? = null
    private var busStops: List<DropOffStop>? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var busRouteAbrv : String? = null
    private var vehId : String? = null
    private var busRouteId  : String? = null
    private var userId : Long? = null
    private var arsId : String? = null
    private var boardingBusStop : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityDropOffBusStopBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.e("drop11", "omg")

        val backButton = findViewById<Button>(R.id.backButton5)
        backButton.setOnClickListener {
            val intent = Intent(this, BusSelection::class.java)
            startActivity(intent)
        }


        // 이전 페이지에서 넘어온 데이터 받기
        intent?.let { intent ->
            busRouteAbrv = intent.getStringExtra("busRouteAbrv")
            vehId = intent.getStringExtra("vehId")
            busRouteId = intent.getStringExtra("busRouteId")
            userId = intent.getLongExtra("userId", -1L)
            arsId = intent.getStringExtra("arsId")
            boardingBusStop = intent.getStringExtra("BoardingBusStop")
            Log.d("userIdCheck", "$userId")

            binding.selectedBusNum.text = busRouteAbrv
            binding.selectedBoardingStop.text = boardingBusStop
        } ?: run {
            // 인텐트가 null인 경우 처리할 내용 추가
            Log.e("IntentError", "Intent is null")
        }

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
                    Toast.makeText(this, "Location permission denied.", Toast.LENGTH_SHORT).show()
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
                        "Location permission denied.",
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
                getDropOffBusStops(arsId, busRouteId)
            } else {
                Toast.makeText(this, "Unable to get the current location.", Toast.LENGTH_SHORT).show()
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
                        Log.e("Ok", "Unsuccessful response: ${response.code()}")
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

            cardView.setOnClickListener {
                if (busStop != null) {
                    Log.d("BusStopInfo", "Clicked bus stop: ${busStop.name}, ARS 번호: ${busStop.arsId}")
                    navigateToReservedPage(busStop)
                } else {
                    Log.e("ClickError", "busStop is null")
                }
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

            cardView.addView(textView)
            resultContainer.addView(cardView)

            // CardView를 클릭했을 때 BusSelection 페이지로 이동하는 클릭 리스너 추가
            cardView.setOnClickListener {
                if (busStop != null) {
                    //selectedDropOffBusStop = busStop // 클릭된 하차 정류장 설정
                    Log.d("BusStopInfo", "Clicked bus stop: ${busStop.name}, ARS 번호: ${busStop.arsId}")
                    navigateToReservedPage(busStop)
                } else {
                    Log.e("ClickError", "busStop is null")
                }
            }

            // CardView 크기 조정
            val params = cardView.layoutParams as LinearLayout.LayoutParams
            params.height = resources.getDimensionPixelSize(R.dimen.cardview_height)
            cardView.layoutParams = params
        }
    }

    private fun sendReservationToServer(reservation: ReservationComplete) {
        Log.d("ReservationInfo_last", "BoardingStop: ${reservation.boardingStop}, DropStop: ${reservation.dropStop}, VehId: ${reservation.vehId}, BusRouteNm: ${reservation.busRouteNm}")
        val apiManager = ApiManager_ReservationComplete.create()
        // Retrofit을 사용하여 서버 통신을 위한 인터페이스 생성

        // 서버에 예약 정보 전송
        apiManager.createReservation(reservation)
            .enqueue(object : Callback<ApiResponse> {
                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                    Log.d("Retrofit123", "Response code: ${response.code()}")
                    Log.d("Retrofit123", "Response body: ${response.body()}")
                    if (response.isSuccessful) {
                        Log.d("status12", "connected11")
                        // 서버 응답이 성공적으로 수신된 경우
                        val apiResponse = response.body()

                        // 여기에서 apiResponse를 활용하여 성공 또는 실패에 대한 처리를 수행
                        if (apiResponse != null) {
                            // 성공적으로 예약이 생성된 경우
                            // apiResponse의 상세 정보에 따라 적절한 작업 수행
                            if (apiResponse.status == "CREATED") {
                                checkReservation(userId)
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
                        Log.e("RetrofitError", "Unsuccessful response: ${response.code()}")
                        Toast.makeText(applicationContext, "API 호출에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                    if (response.body() == null) {
                        Log.e("RetrofitError", "Response body is null")
                    }
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    // 서버에 예약 정보 전송 실패
                    // 에러 메시지를 출력하거나 다른 예외 처리 작업을 수행
                    Log.e("RetrofitError", "API call failed", t)
                    Toast.makeText(applicationContext, "API 호출에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            })
    }

    // showToast 함수는 사용자에게 메시지를 보여주는 함수입니다.
    fun showToast(message: String) {
        // TODO: 사용자에게 메시지를 보여주는 로직을 여기에 추가
    }


    private fun navigateToReservedPage(busStop:DropOffStop) {
        Log.d("ClickEvent", "CardView clicked")
        try {
            if (busStop == null) {
                Log.e("NavigationError", "selectedDropOffBusStop is null")
                return // null 일 경우 함수 종료
            }
            // 여기에 예약 확인창 및 이동 관련 로직 추가
            //확인창 띄우기
            Log.d("BusStopInfo", "Clicked bus stop: ${busStop.name}, ARS 번호: ${busStop.arsId}")
            Log.d("BusStopInfo", "$arsId")
            Log.d("BusStopInfo", "$busRouteAbrv")
            //Log.d("BusStopInfo", "Clicked bus stop: ${busStop.name}, ARS 번호: ${busStop.arsId}")
            val builder = AlertDialog.Builder(this)

            builder.setTitle("Booking confirmation window")
                .setMessage("Would you really like to make a reservation?")
                .setPositiveButton("Yes") { dialog, which ->

                    val reservation = ReservationComplete(
                        userId = userId,
                        boardingStop = arsId,  //승차 정류소 고유 번호
                        dropStop = busStop.arsId,
                        vehId = "108018089",  //버스 ID
                        busRouteNm = busRouteAbrv  //버스 노선 번호
                    )

                    Log.d("ReservationUserId", "$userId")

                    // 서버에 예약 정보 전송
                    sendReservationToServer(reservation)
                }
                .setNegativeButton("No") { dialog, which ->
                    // 취소 또는 다른 작업을 수행할 수 있습니다.
                }
                .show()
        } catch (e: Exception) {
            Log.e("NavigationError", "Error navigating to Reserved page: ${e.message}")
            e.printStackTrace()
        }
    }

    // 예약 내역 확인 함수
    private fun checkReservation(userId: Long?) {
        // AsyncTask를 사용하여 백그라운드 스레드에서 네트워크 작업 실행
        val checkReservationTask = CheckReservationTask()
        checkReservationTask.execute(userId)

        // AsyncTask가 완료될 때까지 기다리지 않고 바로 다음으로 진행하지 않도록 수정
        val homeReservationResponse = checkReservationTask.get()

        if (homeReservationResponse != null && homeReservationResponse.isSuccessful) {
            val response = homeReservationResponse.body()

            if (response?.data != null) {
                // 예약 내역이 있을 경우 ReservedActivity로 이동
                val reservedIntent = Intent(this, Reserved::class.java)
                Log.d("lastpagereservationdata", "$response")
                reservedIntent.putExtra("reservationData", response.data)
                startActivity(reservedIntent)
                finish() // 현재 액티비티 종료
            } else {
                /*
                // 예약 내역이 없을 경우 NoReservationActivity로 이동
                val noReservationIntent = Intent(this@MainActivity, NoReservation::class.java)
                // 사용자 ID를 전달
                noReservationIntent.putExtra("userId", userId)
                startActivity(noReservationIntent)
                finish() // 현재 액티비티 종료*/
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
