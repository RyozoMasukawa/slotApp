package com.example.wasacon.slotapp.data

import android.graphics.Color
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.wasacon.slotapp.DateAdministrator
import com.example.wasacon.slotapp.R
import com.example.wasacon.slotapp.realmObjects.ResultData
import com.example.wasacon.slotapp.realmObjects.AccountData
import com.example.wasacon.slotapp.realmObjects.BallData
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import java.util.*

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

        holder.itemView.setOnLongClickListener {
            val pop = PopupMenu(holder.itemView.context, it)
            pop.inflate(R.menu.remove_context)

            val row_index = position
            if(row_index != null && row_index==position){
                holder.itemView.setBackgroundColor(Color.LTGRAY)
            } else {
                holder.itemView.setBackgroundColor(Color.WHITE)
            }

            pop.setOnMenuItemClickListener {
                    item ->
                when(item.itemId)
                {

                    R.id.remove -> {
                        val realm = Realm.getDefaultInstance()
                        realm.executeTransaction {
                            val dPair = DateAdministrator.bindDay(Date())

                            val previousAccountData : AccountData? = realm.where(AccountData::class.java)
                                .greaterThanOrEqualTo("dateTime", dPair.first)
                                .findAll()
                                .where()
                                .lessThanOrEqualTo("dateTime", dPair.second)
                                .sort("dateTime", Sort.DESCENDING)
                                .findFirst()


                            val previousBalance = previousAccountData?.balance ?: 0;

                            val deposit = rResults[position]?.result?.toInt() ?: 0

                            if (deposit + previousBalance >= 0) {
                                val maxId = realm.where<AccountData>().max("id")
                                val nextId = (maxId?.toLong() ?: 0L) + 1L
                                val accountData = realm.createObject<AccountData>(nextId)
                                accountData.dateTime = Date()
                                accountData.balance = deposit + previousBalance
                            }

                            val numBalls = -1

                            val maxId = realm.where<BallData>().max("id")?.toLong()

                            val previousBallData = realm.where(BallData::class.java)
                                .equalTo("id", maxId).findFirst()

                            val previousNumBalls = previousBallData?.numBalls ?: 0

                            if (numBalls + previousNumBalls >= 0) {                                 val maxId = realm.where<BallData>().max("id")
                                val nextId = (maxId?.toLong() ?: 0L) + 1L
                                val ballData = realm.createObject<BallData>(nextId)
                                ballData.dateTime = Date()
                                ballData.numBalls = numBalls + previousNumBalls
                            }

                            realm.where(ResultData::class.java)?.equalTo("id", rResults[position]?.id)
                                ?.findAll()?.deleteFirstFromRealm()
                        }
                        notifyItemRemoved(position)
                    }

                    R.id.cancel -> {
                        holder.itemView.setBackgroundColor(Color.WHITE)
                    }
                }
                true
            }

            pop.setOnDismissListener {
                holder.itemView.setBackgroundColor(Color.WHITE)

            }

            pop.show()
            true
        }
    }
}