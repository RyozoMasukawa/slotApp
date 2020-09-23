package com.example.wasacon.slotapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import io.realm.Realm
import io.realm.Sort
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_edit.*
import java.util.*

class EditActivity : AppCompatActivity() {

    private lateinit var realm : Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        realm = Realm.getDefaultInstance()

        saveBtn.setOnClickListener {
            realm.executeTransaction {
                Log.d("EditActivity:result", intent.getLongExtra("result", 0L).toString())

                val maxId = realm.where<ResultData>().max("id")
                val nextId = (maxId?.toLong() ?: 0L) + 1L
                val resultData = realm.createObject<ResultData>(nextId)
                resultData.dateTime = Date()
                resultData.result = intent.getLongExtra("result", 0L)
                resultData.rank = intent.getIntExtra("rank", 0)

                if (!addressEdit.text.isNullOrEmpty()) {
                    resultData.address = addressEdit.text.toString()
                }

                if (!nameEdit.text.isNullOrEmpty()) {
                    resultData.name = nameEdit.text.toString()
                }

                if (!postalEdit.text.isNullOrEmpty()) {
                    resultData.postal = postalEdit.text.toString()
                }

                val maxBallId = realm.where<BallData>().max("id")
                val previousNumBalls = realm.where<BallData>()
                    .sort("dateTime", Sort.DESCENDING)
                    .findFirst()?.numBalls ?: 1

                val nextBallId = (maxBallId?.toLong() ?: 0L) + 1L
                val ballData = realm.createObject<BallData>(nextBallId)
                ballData.numBalls = previousNumBalls - 1
                ballData.dateTime = Date()
            }
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}
