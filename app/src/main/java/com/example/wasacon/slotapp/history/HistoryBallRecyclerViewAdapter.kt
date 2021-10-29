package com.example.wasacon.slotapp.history

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wasacon.slotapp.R

class HistoryBallRecyclerViewAdapter(
    private val currentNumBalls : Int,
    private val startNumBalls : Int,
    private val sumOfCounts : Int) : RecyclerView.Adapter<HistoryViewHolder>() {

    override fun getItemCount(): Int {
        return 1
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.rankText?.text = "玉数"
        holder.avgText?.text = "開始玉数 : %d個\n本日出玉数 : %d個\n追加玉数 : %d個\n本日残玉数 : %d個".format(startNumBalls, sumOfCounts, currentNumBalls - startNumBalls + sumOfCounts,currentNumBalls)
        //holder.itemView.setBackgroundColor(Color.YELLOW)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.one_history, parent, false)
        val historyViewHolder = HistoryViewHolder(view)
        return historyViewHolder
    }
}