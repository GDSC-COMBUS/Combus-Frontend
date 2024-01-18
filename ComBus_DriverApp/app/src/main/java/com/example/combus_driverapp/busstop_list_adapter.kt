package com.example.combus_driverapp

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.combus_driverapp.databinding.HomeListItemBinding
import timber.log.Timber

class busstop_list_adapter(private val busstoplist:List<busstop>,/*bookpersonlist:List<busstop_book_person>,alightpersonlist:List<busstop_alight_person>*/):RecyclerView.Adapter<busstop_list_adapter.busstop_list_ViewHolder>() {

    val book = 0
    val alight = 0
    val chair = false

    fun count(busstoplist: List<busstop>,bookpersonlist: List<busstop_book_person>,alightpersonlist: List<busstop_alight_person>){
        for (i in 1..bookpersonlist.size){

        }
    }

    class busstop_list_ViewHolder(val binding: HomeListItemBinding):
            RecyclerView.ViewHolder(binding.root){
        private val context = binding.root.context
                fun bind(busstop:busstop){
                    binding.txtBusstopNameItem.text = busstop.busstop_name
                    binding.txtBusstopNumItem.text = busstop.busstop_num

                    itemView.setOnClickListener {
                        val intent = Intent(context,busstop_detail::class.java)
                        intent.putExtra("busstop_name",busstop.busstop_name)
                        intent.putExtra("busstop_num",busstop.busstop_num)
                        intent.run { context.startActivity(this) }
                    }
                }
            }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): busstop_list_ViewHolder {
        Timber.d("onCreateViewHolder")
        return busstop_list_ViewHolder(
            HomeListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }

    override fun getItemCount(): Int = busstoplist.size

    override fun onBindViewHolder(holder: busstop_list_ViewHolder, position: Int) {
        Timber.d("onBindViewHolder")
        val currentMaster = busstoplist[position]
        holder.bind(currentMaster)
    }

}