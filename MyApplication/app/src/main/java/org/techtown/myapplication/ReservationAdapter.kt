package org.techtown.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ReservationAdapter(val menuList:ArrayList<Reservation>) : RecyclerView.Adapter<ReservationAdapter.CustomViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservationAdapter.CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_item, parent, false)
        return CustomViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReservationAdapter.CustomViewHolder, position: Int) {
        holder.menu.text = menuList.get(position).menu
        holder.menu_value.text = menuList.get(position).menu_value
    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    class CustomViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val menu = itemView.findViewById<TextView>(R.id.menu) //메뉴
        val menu_value = itemView.findViewById<TextView>(R.id.menu_value) //해당 메뉴의 값
    }

}