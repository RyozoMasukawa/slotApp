package com.example.wasacon.slotapp.history

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wasacon.slotapp.R

class HistoryBalanceRecyclerViewAdapter(private val sumOfCounts : Int,
                                        private val sumOfResults : Int,
                                        private val currentBalance : Int,
                                        private val startBalance : Int) : RecyclerView.Adapter<HistoryViewHolder>() {
    override fun getItemCount(): Int {
        return 1
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.rankText?.text = "現金"
        holder.avgText?.text = "開始時金額 : ¥%d\n入金 : ¥%d\n出金 : ¥%d\n残高 : ¥%d".format(startBalance, currentBalance + sumOfResults, sumOfResults,currentBalance)
        //holder.itemView.setBackgroundColor(Color.RED)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.one_history, parent, false)
        val historyViewHolder = HistoryViewHolder(view)
        return historyViewHolder
    }
}