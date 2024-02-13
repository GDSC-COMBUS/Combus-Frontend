package org.techtown.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import org.techtown.myapplication.connection.RetrofitClient
import org.techtown.myapplication.databinding.ActivityBusSelectionBinding
import retrofit2.Call
import retrofit2.Response

class BusSelection : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityBusSelectionBinding
    private lateinit var googleMap: GoogleMap
    private lateinit var mapView: MapView
    private var currentMarker: Marker? = null
    private var busstop_name: String = ""
    private var arsId: String = ""
    private var busstop_X: Double = 0.0
    private var busstop_Y: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("wow", "BusSelection onCreate called")
        binding = ActivityBusSelectionBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val extras = intent.extras
        busstop_name = extras?.getString("busStop_name") ?: ""
        arsId = extras?.getString("arsId") ?: ""
        busstop_X = extras?.getDouble("gpsX") ?: 0.0
        busstop_Y = extras?.getDouble("gpsY") ?: 0.0

        binding.textView5.text = busstop_name

        this.mapView = binding.mapView2
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this@BusSelection)

        val arsId = binding.searchBox2.text.toString()

        val call = RetrofitObject.getRetrofitService.BusSelection(arsId)

        call.enqueue(object : retrofit2.Callback<List<RetrofitClient.ResponseBusSelection>> {
            override fun onResponse(
                call: Call<List<RetrofitClient.ResponseBusSelection>>,
                response: Response<List<RetrofitClient.ResponseBusSelection>>
            ) {
                if (response.isSuccessful) {
                    Log.d("status","connected11")
                    val busSelectionResponse = response.body()
                    val dataList1: List<RetrofitClient.ResponseBusSelection>? = busSelectionResponse
                    binding.busList.adapter = BusSelection_adapter(dataList1)
                    initializeViews()
                } else {
                    // 오류 처리
                    Log.d("Retrofit", "false")
                }
            }

            override fun onFailure(call: Call<List<RetrofitClient.ResponseBusSelection>>, t: Throwable) {
                // 네트워크 오류 처리
                val errorMessage = "Call Failed: ${t.message} "
                Log.d("Retrofit", errorMessage)
            }
        })
    }
    private fun initializeViews(){
        binding.busList.layoutManager = LinearLayoutManager(this)

    }
    private fun setupMarker(locationLatLngEntity: LatLngEntity): Marker? {

        val positionLatLng = LatLng(locationLatLngEntity.latitude!!,locationLatLngEntity.longitude!!)
        val markerOption = MarkerOptions().apply {
            position(positionLatLng)
            title("위치")
            snippet("선택 정류장")
        }
        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL  // 지도 유형 설정
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(positionLatLng, 15f))  // 카메라 이동
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15f))  // 줌의 정도 - 1 일 경우 세계지도 수준, 숫자가 커질 수록 상세지도가 표시됨
        return googleMap.addMarker(markerOption)

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

    data class LatLngEntity(
        var latitude: Double?,
        var longitude: Double?
    )

    override fun onMapReady(p0: GoogleMap) {
        this.googleMap = p0

        currentMarker = setupMarker(LatLngEntity(busstop_X, busstop_Y))  // default 서울역
        currentMarker?.showInfoWindow()
    }
}