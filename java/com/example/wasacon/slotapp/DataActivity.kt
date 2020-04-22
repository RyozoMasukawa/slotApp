package com.example.wasacon.slotapp

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.realm.Realm
import io.realm.Sort

import kotlinx.android.synthetic.main.activity_data.*
import kotlinx.android.synthetic.main.content_data.*

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
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        val realmResults = realm.where(ResultData::class.java)
            .findAll()
            .sort("id", Sort.DESCENDING)
        layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        adapter = CustomRecyclerViewAdapter(realmResults)
        recyclerView.adapter = this.adapter
    }

}
