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

class busstop_list_adapter(private var busstoplist: List<RetrofitClient.homebusStopList>, private var stSeq:Int, private var stopFlag:Boolean):RecyclerView.Adapter<busstop_list_adapter.busstop_list_ViewHolder>() {

    //private val dialog = Dialog(context:Context)
    class busstop_list_ViewHolder(val binding: HomeListItemBinding):
            RecyclerView.ViewHolder(binding.root){
        private val context = binding.root.context
                fun bind(busstop:RetrofitClient.homebusStopList,stSeq:Int, stopFlag:Boolean){
                    binding.txtBusstopNameItem.text = busstop.name
                    binding.txtBusstopNumItem.text = busstop.arsId.toString()
                    if (busstop.wheelchair == true)
                        binding.wheelchairIcon.visibility = View.VISIBLE
                    else binding.wheelchairIcon.visibility = View.GONE
                    if (stSeq-1 == busstop.seq){
                        if (stopFlag == false)
                            binding.imageView7.setImageResource(R.drawable.home_bus)
                        //else binding.imageView7.setImageResource(R.drawable.home_busstop_base)
                    }
                    else binding.imageView7.setImageResource(R.drawable.home_busstop_base)

                    if (busstop.reserved_cnt>0) {
                        binding.txtBusstopBookNumItem.visibility = View.VISIBLE
                        binding.txtBusstopBookNumItem.text = "Reservation ${busstop.reserved_cnt}"
                    }
                    else binding.txtBusstopBookNumItem.visibility = View.GONE

                    if (busstop.drop_cnt>0) {
                        binding.txtBusstopOutNumItem.visibility = View.VISIBLE
                        binding.txtBusstopOutNumItem.text = "Dropping off ${busstop.drop_cnt}"
                    }
                    else binding.txtBusstopOutNumItem.visibility = View.GONE



                    itemView.setOnClickListener {
                        val intent = Intent(context,busstop_detail::class.java)
                        intent.putExtra("arsId",busstop.arsId)
                        intent.putExtra("busstop_name",busstop.name)
                        intent.putExtra("busstop_num",busstop.arsId)
                        intent.putExtra("boarding_num",busstop.reserved_cnt)
                        intent.putExtra("drop_num",busstop.drop_cnt)
                        intent.run { context.startActivity(this) }
                    }
                }
            }
    fun updateData(busStopList: List<RetrofitClient.homebusStopList>, stSeq:Int, stopFlag:Boolean) {
        this.busstoplist = busStopList
        this.stSeq = stSeq
        this.stopFlag = stopFlag
        notifyDataSetChanged() // 데이터 변경을 RecyclerView에 알림
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
        val currentMaster2 = stSeq
        val currentMaster3 = stopFlag
        holder.bind(currentMaster1,currentMaster2,currentMaster3)
    }

}