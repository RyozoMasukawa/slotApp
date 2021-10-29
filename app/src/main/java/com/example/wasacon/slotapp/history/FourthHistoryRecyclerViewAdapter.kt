package com.example.wasacon.slotapp.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wasacon.slotapp.R
import com.example.wasacon.slotapp.data.ViewHolder
import java.util.*

open class FourthHistoryRecyclerViewAdapter(linkedList: List<Int>) : RecyclerView.Adapter<FourthHistoryViewHolder>() {
    val fourthHistoryQueue = linkedList
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FourthHistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.one_forth_history, parent, false)
        val viewHolder = FourthHistoryViewHolder(view)
        return viewHolder
    }

    override fun onBindViewHolder(holder: FourthHistoryViewHolder, position: Int) {
        if (position < fourthHistoryQueue.size) {
            holder.resultView?.text = fourthHistoryQueue[position].toString()
        }
    }

    override fun getItemCount(): Int {
        return fourthHistoryQueue.size
    }
}