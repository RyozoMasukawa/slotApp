package com.example.wasacon.slotapp.data

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wasacon.slotapp.history.HistoryActivity
import com.example.wasacon.slotapp.R
import com.example.wasacon.slotapp.realmObjects.ResultData
import io.realm.Realm
import io.realm.Sort

import kotlinx.android.synthetic.main.activity_data.*
import kotlinx.android.synthetic.main.content_data.*
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class DataActivity : AppCompatActivity() {
    private lateinit var realm : Realm
    private lateinit var adapter: CustomRecyclerViewAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data)
        setSupportActionBar(toolbar)

        realm = Realm.getDefaultInstance()

        rtrnBtn.setOnClickListener { view ->
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()

        val dStartEnd = toDate(intent.getStringExtra("dateTime"))

        val rank = intent.getIntExtra("rank", 0)

        val realmResults = realm.where(ResultData::class.java)
            .equalTo("rank", intent.getIntExtra("rank", 0))
            .greaterThanOrEqualTo("dateTime", dStartEnd.first)
            .findAll()
            .where()
            .lessThanOrEqualTo("dateTime", dStartEnd.second)
            .findAll()
            .sort("id", Sort.DESCENDING)

        layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        adapter = CustomRecyclerViewAdapter(realmResults)
        recyclerView.adapter = this.adapter
        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL))
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
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
}
