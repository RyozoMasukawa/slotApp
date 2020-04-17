package com.example.wasacon.slotapp

import android.graphics.Color
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.realm.RealmResults

class CustomRecyclerViewAdapter(realmResults: RealmResults<ResultData>) : RecyclerView.Adapter<ViewHolder>(){

    private val rResults : RealmResults<ResultData> = realmResults

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.one_result, parent, false)
        val viewHolder = ViewHolder(view)
        return viewHolder
    }

    override fun getItemCount(): Int {
        return rResults.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val resultData = rResults[position]
        holder.dateText?.text = DateFormat.format("yyyy/MM/dd kk:mm", resultData?.dateTime)
        holder.rankText?.text = "${resultData?.rank.toString()}等"
        holder.resultText?.text = "¥${resultData?.result.toString()}"
        holder.postalText?.text = "${resultData?.postal}"
        holder.addressText?.text = "${resultData?.address}"
        holder.nameText?.text = "${resultData?.name}"
        holder.itemView.setBackgroundColor(if (position % 2 == 0) Color.LTGRAY else Color.WHITE)
    }
}