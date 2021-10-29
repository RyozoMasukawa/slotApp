package com.example.wasacon.slotapp.history

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.one_forth_history.view.*

class FourthHistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var resultView : TextView? = null

    init {
        resultView = itemView.resultText
    }
}