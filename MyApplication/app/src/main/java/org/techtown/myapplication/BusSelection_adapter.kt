package org.techtown.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.techtown.myapplication.connection.RetrofitClient
import org.techtown.myapplication.databinding.BusSelectionItemBinding

class BusSelection_adapter(private val buslist: List<RetrofitClient.ResponseBusSelection>?): RecyclerView.Adapter<BusSelection_adapter.BusSelection_viewholder>() {
    class BusSelection_viewholder(private val binding: BusSelectionItemBinding):
            RecyclerView.ViewHolder(binding.root){
                private val context = binding.root.context
        fun bind(bus:RetrofitClient.ResponseBusSelection){
            val busnum = bus.busRouteAbrv
            val bustype = bus.low
            binding.menuValue.text = busnum
            if (bustype == true){
                binding.busType.visibility = View.VISIBLE
            }
            else
                binding.busType.visibility = View.GONE
        }
            }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusSelection_viewholder {
        return BusSelection_viewholder(BusSelectionItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int = buslist!!.size

    override fun onBindViewHolder(holder: BusSelection_viewholder, position: Int) {
        holder.bind(buslist!![position])
    }
}