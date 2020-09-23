package com.example.wasacon.slotapp

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.one_history.view.*

class HistoryViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
    var rankText : TextView? = null
    var numText : TextView? = null
    var avgText : TextView? = null
    var sumText : TextView? = null

    init {
        rankText = itemView.rankTxt
        numText = itemView.numberTxt
        avgText = itemView.avgTxt
        sumText = itemView.sumTxt
    }
}