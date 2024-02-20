package org.techtown.myapplication

import android.content.Intent
import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.techtown.myapplication.connection.RetrofitClient
import org.techtown.myapplication.databinding.BusSelectionItemBinding

class BusSelection_adapter(
    private val buslist: List<RetrofitClient.ResponseBusSelection>?,
    private val userId: Long, // userId를 생성자 매개변수로 추가합니다.
    private val busstop_name:String,
    private val arsId : String
) : RecyclerView.Adapter<BusSelection_adapter.BusSelection_viewholder>() {
    inner class BusSelection_viewholder(private val binding: BusSelectionItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(bus: RetrofitClient.ResponseBusSelection) {
            binding.menuValue.text = bus.busRouteAbrv
            binding.busType.visibility = if (bus.low > 0) {
                ViewGroup.VISIBLE
            } else {
                ViewGroup.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusSelection_viewholder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = BusSelectionItemBinding.inflate(inflater, parent, false)
        return BusSelection_viewholder(binding)
    }

    override fun getItemCount(): Int = buslist?.size ?: 0

    override fun onBindViewHolder(holder: BusSelection_viewholder, position: Int) {
        buslist?.let { list ->
            if (position < list.size) {
                val bus = list[position]
                val itemView = holder.itemView
                holder.bind(bus)

                // padding을 설정합니다. 여기서는 상하 padding만 설정했습니다.
                val paddingInPixels = convertDpToPixel(8) // dp를 pixel로 변환하는 함수를 사용하여 설정합니다.
                itemView.setPadding(0, paddingInPixels, 0, paddingInPixels)

                // 클릭 이벤트 처리
                holder.itemView.setOnClickListener {
                    val intent = Intent(holder.itemView.context, DropOffBusStop::class.java)
                    intent.putExtra("busRouteAbrv", bus.busRouteAbrv) // 클릭한 버스 노선 약어를 인텐트에 추가
                    intent.putExtra("vehId", bus.vehId) // 클릭한 버스 차량 ID를 인텐트에 추가
                    intent.putExtra("busRouteId", bus.busRouteId) // 버스 노선 Id
                    intent.putExtra("userId", userId)
                    intent.putExtra("arsId", arsId)
                    intent.putExtra("BoardingBusStop", busstop_name)

                    Log.d("vehId", "${bus.vehId}")

                    holder.itemView.context.startActivity(intent)
                }
            }
        }
    }

    // dp를 pixel로 변환하는 함수입니다.
    private fun convertDpToPixel(dp: Int): Int {
        val density = Resources.getSystem().displayMetrics.density
        return (dp * density).toInt()
    }

}
