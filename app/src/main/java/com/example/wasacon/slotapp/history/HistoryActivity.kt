package com.example.wasacon.slotapp.history

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import io.realm.kotlin.where
import android.text.format.DateFormat
import com.example.wasacon.slotapp.*
import com.example.wasacon.slotapp.realmObjects.AccountData
import com.example.wasacon.slotapp.realmObjects.BallData
import com.example.wasacon.slotapp.realmObjects.FourthData
import com.example.wasacon.slotapp.realmObjects.ResultData

import kotlinx.android.synthetic.main.activity_history.*
import kotlinx.android.synthetic.main.activity_history.toolbar
import kotlinx.android.synthetic.main.content_data.recyclerView
import kotlinx.android.synthetic.main.content_history.*
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class HistoryActivity : AppCompatActivity() {
    private lateinit var realm : Realm

    private lateinit var hrvAdapter: HistoryRecyclerViewAdapter
    private lateinit var harvAdapter: HistoryBalanceRecyclerViewAdapter
    private lateinit var hbrvAdapter: HistoryBallRecyclerViewAdapter

    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var aggregateLayoutManager: RecyclerView.LayoutManager
    private lateinit var ballLayoutManager: RecyclerView.LayoutManager

    private lateinit var realmResults : RealmResults<ResultData>
    private lateinit var fourthResults : RealmResults<FourthData>




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        setSupportActionBar(toolbar)

        realm = Realm.getDefaultInstance()

        fab.setOnClickListener { view ->
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()

        val dateSet : MutableSet<String> = createDateSet()

        val adapter = ArrayAdapter(
            applicationContext,
            android.R.layout.simple_spinner_item,
            dateSet.toTypedArray()
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner.adapter = adapter

        layoutManager = LinearLayoutManager(this)
        aggregateLayoutManager = LinearLayoutManager(this)
        ballLayoutManager = LinearLayoutManager(this)


        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val spinnerParent = parent as Spinner
                val date = spinnerParent.selectedItem as String
                val dStartEnd = toDate(date)
                val dYesterday = yesterday(dStartEnd)


                //*その日の1~3等のデータ全てを入手
                realmResults =  realm.where(ResultData::class.java)
                    .greaterThanOrEqualTo("dateTime", dStartEnd.first)
                    .findAll()
                    .where()
                    .lessThanOrEqualTo("dateTime", dStartEnd.second)
                    .findAll()

                //*4等のデータ入手
                fourthResults =  realm.where(FourthData::class.java)
                    .greaterThanOrEqualTo("dateTime", dStartEnd.first)
                    .findAll()
                    .where()
                    .lessThanOrEqualTo("dateTime", dStartEnd.second)
                    .findAll()

                //*1~3等と4等の合計出金額を計算
                val sumOfResults = realmResults.sum("result").toInt() + fourthResults.sum("result").toInt()

                //*その日のでた本数計算
                val sumOfCounts = realmResults.count() + fourthResults.sum("count").toInt()

                //*現在の球数を得る
                val currentNumBalls : Int = realm.where<BallData>()
                    .greaterThanOrEqualTo("dateTime", dStartEnd.first)
                    .findAll()
                    .where()
                    .lessThanOrEqualTo("dateTime", dStartEnd.second)
                    .sort("dateTime", Sort.DESCENDING)
                    .findFirst()
                    ?.numBalls ?: 0

                //*開始時の球数を得る
                val startNumBalls : Int = realm.where<BallData>()
                    .greaterThanOrEqualTo("dateTime", dStartEnd.first)
                    .findAll()
                    .where()
                    .lessThanOrEqualTo("dateTime", dStartEnd.second)
                    .sort("dateTime", Sort.DESCENDING)
                    .findFirst()
                    ?.numBalls ?: 0

                //*最新の残高を得る
                val currentBalance : Int = realm.where(AccountData::class.java)
                    .greaterThanOrEqualTo("dateTime", dStartEnd.first)
                    .findAll()
                    .where()
                    .lessThanOrEqualTo("dateTime", dStartEnd.second)
                    .sort("dateTime", Sort.DESCENDING)
                    .findFirst()
                    ?.balance ?: 0

                //*開始時の金額を見る
                val startBalance : Int = realm.where(AccountData::class.java)
                    .greaterThanOrEqualTo("dateTime", dStartEnd.first)
                    .findAll()
                    .where()
                    .lessThanOrEqualTo("dateTime", dStartEnd.second)
                    .greaterThan("balance", 0)
                    .sort("dateTime", Sort.ASCENDING)
                    .findFirst()
                    ?.balance ?: 0

                recyclerView.layoutManager = layoutManager
                hrvAdapter = HistoryRecyclerViewAdapter(
                    realmResults,
                    fourthResults,
                    currentBalance,
                    startBalance,
                    sumOfResults,
                    sumOfCounts.toInt()
                )
                recyclerView.adapter = hrvAdapter
                recyclerView.addItemDecoration(
                    DividerItemDecoration(
                        recyclerView.context,
                        DividerItemDecoration.VERTICAL
                    )
                )
                recyclerView.addItemDecoration(
                    DividerItemDecoration(
                        recyclerView.context,
                        DividerItemDecoration.HORIZONTAL
                    )
                )

                //現金
                recyclerView2.layoutManager = aggregateLayoutManager
                harvAdapter = HistoryBalanceRecyclerViewAdapter(
                    sumOfCounts.toInt(),
                    sumOfResults,
                    currentBalance,
                    startBalance
                )
                recyclerView2.adapter = harvAdapter

                recyclerView2.addItemDecoration(
                    DividerItemDecoration(
                        recyclerView.context,
                        DividerItemDecoration.VERTICAL
                    )
                )
                recyclerView2.addItemDecoration(
                    DividerItemDecoration(
                        recyclerView.context,
                        DividerItemDecoration.HORIZONTAL
                    )
                )

                //玉
                recyclerView3.layoutManager = ballLayoutManager
                hbrvAdapter = HistoryBallRecyclerViewAdapter(
                    currentNumBalls,
                    startNumBalls,
                    sumOfCounts.toInt()
                )
                recyclerView3.adapter = hbrvAdapter

                recyclerView3.addItemDecoration(
                    DividerItemDecoration(
                        recyclerView.context,
                        DividerItemDecoration.VERTICAL
                    )
                )
                recyclerView3.addItemDecoration(
                    DividerItemDecoration(
                        recyclerView.context,
                        DividerItemDecoration.HORIZONTAL
                    )
                )
            }
        }
        recyclerView.layoutManager = layoutManager
    }

    private fun createDateSet() : MutableSet<String> {
        val dates = realm.where<AccountData>()
            .sort("dateTime", Sort.DESCENDING)
            .findAll()

        /*コメントアウト 20201128
        val fourthDates = realm.where<FourthData>()
            .sort("dateTime", Sort.DESCENDING)
            .findAll()

         */

        val ret : MutableSet<String> = mutableSetOf()

        for (date in dates) {
            ret.add(DateFormat.format("yyyy/MM/dd", date?.dateTime).toString())
        }

        /*コメントアウト 20201128
        for (fDate in fourthDates) {
            ret.add(DateFormat.format("yyyy/MM/dd", fDate?.dateTime).toString())
        }
        */

        return ret
    }

    private fun toDate(dateString : String) : Pair<Date, Date> {
        var date : LocalDate
        val formatter  = DateTimeFormatter.ofPattern("yyyy/MM/dd")

        val defaultZoneId = ZoneId.systemDefault()

        date = LocalDate.parse(dateString, formatter)
        Log.d("Print result: ", date.toString())
        val start = Date.from(date.atStartOfDay(defaultZoneId).toInstant())
        val end = Date.from(date.plusDays(1).atStartOfDay(defaultZoneId).toInstant())
        return Pair(start, end)
    }

    private fun yesterday(dpair : Pair<Date, Date>) : Pair<Date, Date> {
        val date = dpair.first

        val defaultZoneId = ZoneId.systemDefault()

        val localDate = date.toInstant().atZone(defaultZoneId).toLocalDate().minusDays(1)

        Log.d("Print result: ", date.toString())
        val start = Date.from(localDate.atStartOfDay(defaultZoneId).toInstant())
        val end = Date.from(localDate.plusDays(1).atStartOfDay(defaultZoneId).toInstant())
        return Pair(start, end)
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}
