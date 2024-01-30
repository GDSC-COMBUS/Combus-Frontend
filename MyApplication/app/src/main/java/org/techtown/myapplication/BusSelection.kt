package org.techtown.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

    val extras = intent.extras
    val busstop_name = extras!!["busStop_name"].toString()
    val busstop_X = extras!!["gpsX"]
    val busstop_Y = extras!!["gpsY"]
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityBusSelectionBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.textView5.text = busstop_name

        this.mapView = binding.mapView2
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this@BusSelection)

        val strSrch = binding.searchBox2.text.toString()

        val call = RetrofitObject.getRetrofitService.BusSelection(RetrofitClient.requestBusSelection(strSrch))

        call.enqueue(object : retrofit2.Callback<RetrofitClient.responseBusSelection> {
            override fun onResponse(
                call: Call<RetrofitClient.responseBusSelection>,
                response: Response<RetrofitClient.responseBusSelection>
            ) {
                if (response.isSuccessful) {
                    val busSelectionResponse = response.body()
                    // 여기서 busSelectionResponse를 사용하여 RecyclerView에 데이터를 추가할 수 있습니다.
                    // 예를 들어, busSelectionResponse에서 필요한 정보를 추출하여 adapter에 추가합니다.
                    // busListAdapter.addData(busSelectionResponse) 등의 방법을 사용할 수 있습니다.
                    val dataList1: RetrofitClient.responseBusSelection? = busSelectionResponse
                    binding.busList.adapter = BusSelection_adapter(dataList1)
                    initializeViews()

                } else {
                    // 오류 처리
                }
            }

            override fun onFailure(call: Call<RetrofitClient.responseBusSelection>, t: Throwable) {
                // 네트워크 오류 처리
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
        this.googleMap = googleMap

        currentMarker = setupMarker(LatLngEntity(busstop_X as Double?, busstop_Y as Double?))  // default 서울역
        currentMarker?.showInfoWindow()
    }
}