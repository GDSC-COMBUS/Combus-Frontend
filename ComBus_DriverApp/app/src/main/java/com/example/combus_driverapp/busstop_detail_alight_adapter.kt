package com.example.combus_driverapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.combus_driverapp.databinding.DetailListItemBinding
import timber.log.Timber

class busstop_detail_alight_adapter(private val alightlist:List<alight>):RecyclerView.Adapter<busstop_detail_alight_adapter.detail_alight_ViewHolder>() {

    class detail_alight_ViewHolder(val binding: DetailListItemBinding):
            RecyclerView.ViewHolder(binding.root){
                fun bind(alight:alight){
                    binding.txtType.text = alight.type
                    binding.txtIn.text = alight.board
                    binding.txtOut.text = alight.alight
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