package org.techtown.myapplication

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
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
    private var isMapReady = false
    private var nearbyBusStops: List<BoardingStop>? = null
    private var userId: Long = -1L
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityBoardingBusStopBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val backButton = findViewById<Button>(R.id.backButton2)
        backButton.setOnClickListener {
            val intent = Intent(this, NoReservation::class.java)
            startActivity(intent)
        }

        userId = intent.getLongExtra("userId", -1L)

        val searchBox = findViewById<EditText>(R.id.searchBox_boarding)
        searchBox.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()
                searchBusStops(query)
            }
        })

        mapView = findViewById(R.id.map_boarding)
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
            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setMessage("이 앱을 사용하려면 위치 권한이 필요합니다. 권한을 부여하시겠습니까?")
                .setCancelable(false)
                .setPositiveButton("네") { dialog, id ->
                    ActivityCompat.requestPermissions(
                        this@BoardingBusStop,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        REQUEST_LOCATION_PERMISSION
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
                REQUEST_LOCATION_PERMISSION
            )
        }
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
                getNearbyBusStops(location)
            } else {
                Toast.makeText(this, "현재 위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        } else {
            requestLocationPermission()
        }
    }


    private fun getNearbyBusStops(location: Location) {

        // 토스트 메시지로 gpsX와 gpsY 값을 확인
        //Toast.makeText(applicationContext, "Request Body: gpsX=${locationRequest.gpsX}, gpsY=${locationRequest.gpsY}", Toast.LENGTH_SHORT).show()

        val busStopService = ApiManager_BoardingBusStop.create()

        busStopService.getNearbyBusStops(location.longitude, location.latitude)
            .enqueue(object : Callback<List<BoardingStop>> {
                override fun onResponse(call: Call<List<BoardingStop>>, response: Response<List<BoardingStop>>) {
                    if (response.isSuccessful) {
                        // 성공적인 응답을 로깅합니다.
                        Log.d("OMG", "Successful response: ${response.body()}")
                        nearbyBusStops = response.body()

                        val message = "주변 버스 정류장 수: ${nearbyBusStops?.size ?: 0}"
                        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()

                        nearbyBusStops?.let { busStops ->
                            for (busStop in busStops) {
                                val busStopLatLng = LatLng(busStop.latitude, busStop.longitude)
                                val marker = googleMap.addMarker(MarkerOptions().position(busStopLatLng).title(busStop.name))
                                marker?.snippet = "ARS 번호: ${busStop.arsId}"
                            }
                            googleMap.setOnMarkerClickListener { clickedMarker ->
                                showInfoWindow(clickedMarker)
                                true
                            }
                            updateUIWithNearbyBusStops(busStops)
                        }
                    } else {
                        // Handle unsuccessful response
                        // 실패한 응답을 로깅합니다.
                        Log.e("OMG", "Unsuccessful response: ${response.code()}")
                        Toast.makeText(applicationContext, "API 호출에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<BoardingStop>>, t: Throwable) {
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
        val filteredBusStops = nearbyBusStops?.filter { it.name.contains(query, ignoreCase = true) }
        updateUIWithSearchResults(filteredBusStops)
    }



    private fun updateUIWithNearbyBusStops(busStops: List<BoardingStop>) {
        val resultContainer = findViewById<LinearLayout>(R.id.resultContainer)
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
                navigateToDetailsActivity(busStop)
            }

            // CardView 크기 조정
            val params = cardView.layoutParams as LinearLayout.LayoutParams
            params.height = resources.getDimensionPixelSize(R.dimen.cardview_height)
            cardView.layoutParams = params
        }
    }



    private fun updateUIWithSearchResults(busStops: List<BoardingStop>?) {
        val resultContainer = findViewById<LinearLayout>(R.id.resultContainer)
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
                navigateToDetailsActivity(busStop)
            }

            cardView.addView(textView)
            resultContainer.addView(cardView)

            // CardView 크기 조정
            val params = cardView.layoutParams as LinearLayout.LayoutParams
            params.height = resources.getDimensionPixelSize(R.dimen.cardview_height)
            cardView.layoutParams = params
        }
    }


    private fun navigateToDetailsActivity(busStop: BoardingStop) {
        Log.d("wow", "Navigating to BusSelection page...")
        val intent = Intent(this, BusSelection::class.java)
        intent.putExtra("busStop_name", busStop.name) // 버스 정류장 이름 전달
        intent.putExtra("arsId", busStop.arsId) // arsId 전달
        intent.putExtra("gpsX", busStop.latitude) // gpsX 전달
        intent.putExtra("gpsY", busStop.longitude) // gpsY 전달
        startActivity(intent)
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
