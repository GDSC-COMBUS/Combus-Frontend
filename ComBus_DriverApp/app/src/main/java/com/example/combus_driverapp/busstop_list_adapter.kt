package com.example.combus_driverapp

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.combus_driverapp.connection.RetrofitClient
import com.example.combus_driverapp.databinding.HomeListItemBinding
import timber.log.Timber

class busstop_list_adapter(private val busstoplist: List<RetrofitClient.homebusStopList>,private val busPos:List<RetrofitClient.homebusPos>):RecyclerView.Adapter<busstop_list_adapter.busstop_list_ViewHolder>() {

    //private val dialog = Dialog(context:Context)
    class busstop_list_ViewHolder(val binding: HomeListItemBinding):
            RecyclerView.ViewHolder(binding.root){
        private val context = binding.root.context
                fun bind(busstop:RetrofitClient.homebusStopList,busPos:RetrofitClient.homebusPos){
                    binding.txtBusstopNameItem.text = busstop.name
                    binding.txtBusstopNumItem.text = busstop.arsId.toString()
                    if (busstop.wheelchair == true)
                        binding.wheelchairIcon.visibility = View.VISIBLE
                    else binding.wheelchairIcon.visibility = View.GONE
                    if (busPos.arsId == busstop.arsId){
                        if (busPos.stopFlag == true)
                            binding.imageView7.setImageResource(R.drawable.home_bus)
                        //else binding.imageView7.setImageResource(R.drawable.home_busstop_base)
                    }
                    else binding.imageView7.setImageResource(R.drawable.home_busstop_base)
                    if (busstop.reserved_cnt>0) {
                        binding.txtBusstopBookNumItem.visibility = View.VISIBLE
                        binding.txtBusstopBookNumItem.text = "예약 ${busstop.reserved_cnt}"
                    }
                    else binding.txtBusstopBookNumItem.visibility = View.GONE
                    if (busstop.drop_cnt>0) {
                        binding.txtBusstopOutNumItem.visibility = View.VISIBLE
                        binding.txtBusstopOutNumItem.text = "하차 ${busstop.drop_cnt}"
                    }
                    else binding.txtBusstopBookNumItem.visibility = View.GONE



                    itemView.setOnClickListener {
                        val intent = Intent(context,busstop_detail::class.java)
                        intent.putExtra("busstop_name",busstop.name)
                        intent.putExtra("busstop_num",busstop.arsId)
                        intent.run { context.startActivity(this) }
                    }
                }
            }
    /*fun MyDig(){
        dialog
    }*/

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): busstop_list_ViewHolder {
        Timber.d("onCreateViewHolder")
        return busstop_list_ViewHolder(
            HomeListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }

    override fun getItemCount(): Int = busstoplist.size

    override fun onBindViewHolder(holder: busstop_list_ViewHolder, position: Int) {
        Timber.d("onBindViewHolder")
        val currentMaster1 = busstoplist[position]
        val currentMaster2 = busPos[position]
        holder.bind(currentMaster1,currentMaster2)
    }

}