package com.example.combus_driverapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.combus_driverapp.connection.RetrofitClient
import com.example.combus_driverapp.databinding.DetailListItemBinding
import timber.log.Timber

class busstop_detail_alight_adapter(private val alightlist: List<RetrofitClient.offdetailInfolist>):RecyclerView.Adapter<busstop_detail_alight_adapter.detail_alight_ViewHolder>() {

    class detail_alight_ViewHolder(val binding: DetailListItemBinding):
            RecyclerView.ViewHolder(binding.root){
                fun bind(alight: RetrofitClient.offdetailInfolist){
                    if (alight.type == "시각 장애인")
                        binding.txtType.text = "Blind"
                    else
                        binding.txtType.text = "Wheelchair"
                    binding.txtIn.text = alight.boardingStop
                    binding.txtOut.text = alight.dropStop
                }
            }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): detail_alight_ViewHolder {
        Timber.d("onCreatViewHolder")
        return detail_alight_ViewHolder(
            DetailListItemBinding.inflate(
                LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int = alightlist.size

    override fun onBindViewHolder(holder: detail_alight_ViewHolder, position: Int) {
        Timber.d("onBindViewHolder")
        val currentMaster = alightlist[position]
        holder.bind(currentMaster)

    }

}