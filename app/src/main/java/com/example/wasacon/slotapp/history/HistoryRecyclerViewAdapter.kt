package com.example.wasacon.slotapp.history

import android.content.Intent
import android.graphics.Color
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wasacon.slotapp.data.DataActivity
import com.example.wasacon.slotapp.R
import com.example.wasacon.slotapp.realmObjects.FourthData
import com.example.wasacon.slotapp.realmObjects.ResultData
import io.realm.RealmResults
import kotlin.math.roundToInt

open class HistoryRecyclerViewAdapter(realmResults : RealmResults<ResultData>, private val fourthResults : RealmResults<FourthData>, currentBalance : Int?, startBalance : Int?
                                      , private val sumOfResults : Int, private val sumOfCounts : Int) : RecyclerView.Adapter<HistoryViewHolder>() {


    private var currentBalance = currentBalance ?: 0

    private var startBalance = startBalance ?: 0

    val results : Array<RealmResults<ResultData>> = initializeSet(realmResults)

    val minus = realmResults.where().equalTo("rank", -4 as Int).findAll()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.one_history, parent, false)
        val historyViewHolder = HistoryViewHolder(view)
        return historyViewHolder
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        if (results.isNotEmpty() && position < results.size) {
            val result = results[position]
            //気に入らない
            holder.rankText?.text = (result[0]?.rank).toString() + "等"


            holder.numText?.text = "本数 : " + result.size.toString() + "本"
            holder.avgText?.text = "平均 : ¥%d".format(result.average("result").roundToInt())
            holder.sumText?.text = "合計 : ¥%d".format(result.sum("result"))

            holder.itemView.setOnClickListener {
                val intent = Intent(it.context, DataActivity::class.java)
                    .putExtra("rank", result[0]?.rank)
                    .putExtra("dateTime", DateFormat.format("yyyy/MM/dd", result[0]?.dateTime).toString())
                it.context.startActivity(intent)

                val row_index = position
                if(row_index != null && row_index==position){
                    holder.itemView.setBackgroundColor(Color.LTGRAY)
                } else {
                    holder.itemView.setBackgroundColor(Color.WHITE);
                }
            }
        } else if (position == results.size) {
            holder.rankText?.text = "4等"
            val sumOfCount = fourthResults.sum("count").toInt()
            val sumOfResult = fourthResults.sum("result")
            holder.numText?.text = "本数 : " + (sumOfCount).toString() + "本"
            holder.sumText?.text = "合計 : ¥%d".format(sumOfResult)
        } else {
            /*
            var sumOfResults = 0L
            var sumOfCounts = 0
            for (result in results) {
                sumOfResults += result.sum("result") as Long
                sumOfCounts += result.count()
            }
            sumOfResults += minus.sum("result") as Long
            sumOfCounts -= minus.count()*/
            holder.rankText?.text = "合計"
            holder.numText?.text = "本数合計 : %d本".format(sumOfCounts)
            holder.avgText?.text = "賞金合計 : ¥%d".format(sumOfResults)
            //holder.itemView.context.getSharedPreferences(holder.itemView.context.getString(R.string.num_balls), Context.MODE_PRIVATE).getInt("numBalls", 0)
            /*(if (currentBalance - sumOfCounts > 0) { currentBalance - sumOfCounts } else 0)*/
        }
    }

    override fun getItemCount(): Int {
        return results.size + 2
    }

    private fun initializeSet(realmResults: RealmResults<ResultData>) : Array<RealmResults<ResultData>> {
        val rankSet = mutableSetOf<Int>()

        for (result in realmResults) {
            if (result.rank > 0) {
                rankSet.add(result.rank)
            }
        }

        val resultSet = mutableSetOf<RealmResults<ResultData>>()

        val rankSorted = rankSet.sorted()

        for (rank in rankSorted) {
            resultSet.add(realmResults
                .where()
                .equalTo("rank", rank)
                .findAll()
            )
        }

        return resultSet.toTypedArray()
    }
}