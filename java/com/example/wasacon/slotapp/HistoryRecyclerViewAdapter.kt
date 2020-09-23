package com.example.wasacon.slotapp

import android.content.Intent
import android.graphics.Color
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.realm.RealmResults
import kotlin.math.roundToInt

class HistoryRecyclerViewAdapter(realmResults : RealmResults<ResultData>, numBalls : Int?) : RecyclerView.Adapter<HistoryViewHolder>() {


    private var nBalls = numBalls ?: 0

    val results : Array<RealmResults<ResultData>> = initialize_Set(realmResults)

    val minus = realmResults.where().equalTo("rank", -4 as Int).findAll()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.one_history, parent, false)
        val historyViewHolder = HistoryViewHolder(view)
        return historyViewHolder
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        if (position < results.size) {
            val result = results[position]
            //気に入らない
            holder.rankText?.text = (result[0]?.rank).toString() + "等"

            if (result[0]?.rank != 4) {
                holder.numText?.text = "本数 : " + result.size.toString() + "本"
                holder.avgText?.text = "平均 : ¥%d".format(result.average("result").roundToInt())
                holder.sumText?.text = "合計 : ¥%d".format(result.sum("result"))
            } else {
                holder.numText?.text = "本数 : " + (result.size - minus.size).toString() + "本"
                val sum = result.sum("result").toInt() + minus.sum("result").toInt()
                holder.sumText?.text = "合計 : ¥%d".format(sum)
            }

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
        } else {
            var sumOfResults = 0L
            var sumOfCounts = 0
            for (result in results) {
                sumOfResults += result.sum("result") as Long
                sumOfCounts += result.count()
            }
            sumOfResults += minus.sum("result") as Long
            sumOfCounts -= minus.count()
            holder.rankText?.text = "合計"
            holder.numText?.text = "本数 : " + sumOfCounts.toString() + "本"
            holder.avgText?.text = "残玉数 : " + nBalls.toString()

            //holder.itemView.context.getSharedPreferences(holder.itemView.context.getString(R.string.num_balls), Context.MODE_PRIVATE).getInt("numBalls", 0)
            /*(if (nBalls - sumOfCounts > 0) { nBalls - sumOfCounts } else 0)*/

            holder.sumText?.text = "¥%d".format(sumOfResults)
        }
    }

    override fun getItemCount(): Int {
        return results.size + 1
    }

    private fun initialize_Set(realmResults: RealmResults<ResultData>) : Array<RealmResults<ResultData>> {
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