package com.example.wasacon.slotapp

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

import kotlinx.android.synthetic.main.activity_history.*
import kotlinx.android.synthetic.main.activity_history.toolbar
import kotlinx.android.synthetic.main.content_data.*
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class HistoryActivity : AppCompatActivity() {
    private lateinit var realm : Realm
    private lateinit var hrvAdapter: HistoryRecyclerViewAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var realmResults : RealmResults<ResultData>


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

                realmResults =  realm.where(ResultData::class.java)
                    .greaterThanOrEqualTo("dateTime", dStartEnd.first)
                    .findAll()
                    .where()
                    .lessThanOrEqualTo("dateTime", dStartEnd.second)
                    .findAll()

                var numBalls : Int? = realm.where<BallData>()
                    .greaterThanOrEqualTo("dateTime", dStartEnd.first)
                    .findAll()
                    .where()
                    .lessThanOrEqualTo("dateTime", dStartEnd.second)
                    .sort("dateTime", Sort.DESCENDING)
                    .findFirst()
                    ?.numBalls ?: 0

                recyclerView.layoutManager = layoutManager
                hrvAdapter = HistoryRecyclerViewAdapter(realmResults, numBalls)
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
            }
        }
        recyclerView.layoutManager = layoutManager
    }

    private fun createDateSet() : MutableSet<String> {
        val dates = realm.where<ResultData>()
            .sort("dateTime", Sort.DESCENDING)
            .findAll()
        val ret : MutableSet<String> = mutableSetOf()

        for (date in dates) {
            ret.add(DateFormat.format("yyyy/MM/dd", date?.dateTime).toString())
        }

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

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}
