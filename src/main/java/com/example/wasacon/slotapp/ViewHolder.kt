package com.example.wasacon.slotapp

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_edit.view.*
import kotlinx.android.synthetic.main.one_result.view.*
import org.w3c.dom.Text

class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
    var dateText : TextView? = null
    var rankText : TextView? = null
    var resultText : TextView? = null
    var addressText : TextView? = null
    var nameText : TextView? = null
    var postalText : TextView? = null

    init {
        dateText = itemView.dateText
        rankText = itemView.rankText
        resultText = itemView.resultText
        addressText = itemView.addressText
        nameText = itemView.nameText
        postalText = itemView.postalText
    }
}