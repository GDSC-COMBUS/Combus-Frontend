package com.example.combus_driverapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.combus_driverapp.connection.RetrofitClient
import com.example.combus_driverapp.databinding.DetailListItemBinding
import timber.log.Timber

class busstop_detail_book_adapter(private val bookinglist: List<RetrofitClient.boardingdetailInfolist>):RecyclerView.Adapter<busstop_detail_book_adapter.detail_book_ViewHolder>() {

    class detail_book_ViewHolder(val binding: DetailListItemBinding):
            RecyclerView.ViewHolder(binding.root){
                fun bind(book: RetrofitClient.boardingdetailInfolist){
                    binding.txtType.text = book.type
                    binding.txtIn.text = book.boardingStop
                    binding.txtOut.text = book.dropStop
                }
            }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): detail_book_ViewHolder {
        Timber.d("onCreatViewHolder")
        return detail_book_ViewHolder(
            DetailListItemBinding.inflate(
                LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int = bookinglist.size

    override fun onBindViewHolder(holder: detail_book_ViewHolder, position: Int) {
        Timber.d("onBindViewHolder")
        val currentMaster = bookinglist[position]
        holder.bind(currentMaster)

    }

}