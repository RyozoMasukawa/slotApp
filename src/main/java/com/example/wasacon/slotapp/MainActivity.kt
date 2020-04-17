package com.example.wasacon.slotapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var realm : Realm

    private val FORTH = "四等"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        haltServiceBtn.visibility = View.INVISIBLE

        realm = Realm.getDefaultInstance()

        var isForthButtonPressed : Boolean = false

        var result = 0

        var countForthButtonClick = 0

        val intentRewardActivity = Intent(this, RewardActicity::class.java)

        val buttons : Array<Button> = arrayOf(button0
            ,button1
            ,button2
            ,button3
            ,button4
            ,button5
            ,button6
            ,button7
            ,button8
            ,button9)

        forthBtn.setOnClickListener {
            val count = confirmTxt.text.toString().toInt()

            Log.d("Number of 4th:", count.toString())
            if (confirmTxt.text.equals("0")) {
                result += 50
                priceTxt.text = result.toString()
            } else {
                result = count * 50
                priceTxt.text = result.toString()
            }

            realm.executeTransaction {
                val maxId = realm.where<ResultData>().max("id")
                val nextId = (maxId?.toLong() ?: 0L) + 1L
                val resultData = realm.createObject<ResultData>(nextId)
                resultData.dateTime = Date()
                resultData.result = result.toLong()
                resultData.rank = 4
                resultData.address = FORTH
                resultData.name = FORTH
                resultData.postal = FORTH
            }
        }

        for (i in 0..9) {
            buttons[i].setOnClickListener {
                if (confirmTxt.text.equals("0")) {
                    confirmTxt.text = ""
                }
                confirmTxt.text = confirmTxt.text.toString() + i.toString()
            }
        }

        deleteBtn.setOnClickListener {
            confirmTxt.text = "0"
            priceTxt.text = "0"
            result = 0
        }

        startServiceBtn.setOnClickListener {
            val intent = Intent(this, FileUpdateService::class.java)
                .putExtra("shouldContinue", true)

            if (Build.VERSION.SDK_INT >= 23) {
                checkPermission(intent)
            } else {
                startService(intent)
            }
            startServiceBtn.visibility = View.INVISIBLE
            haltServiceBtn.visibility = View.VISIBLE
        }

        haltServiceBtn.setOnClickListener {
            intent.putExtra("shouldContinue", false)
            stopService(intent)
            startServiceBtn.visibility = View.VISIBLE
            haltServiceBtn.visibility = View.INVISIBLE
        }

        firstBtn.setOnClickListener {
            intentRewardActivity
                .putExtra("no", 1)
                .putExtra("first_min", 10000)
                .putExtra("first_max", 99000)

            startActivity(intentRewardActivity)
        }

        secondBtn.setOnClickListener {
            intentRewardActivity
                .putExtra("no", 2)
                .putExtra("second_min", 5000)
                .putExtra("second_max", 20000)
            startActivity(intentRewardActivity)
        }

        thirdBtn.setOnClickListener {
            intentRewardActivity
                .putExtra("no", 3)
                .putExtra("third_min", 1000)
                .putExtra("third_max", 5000)
            startActivity(intentRewardActivity)
        }

        histBtn.setOnClickListener {
            val intent = Intent(this, DataActivity::class.java)
            startActivity(intent)
        }
    }

    private fun checkPermission(intent : Intent) {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            startService(intent)
        } else {
            requestPermission()
        }
    }

    private fun requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                1024)
        } else {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                1024)

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startService(intent)
        } else {
            Toast.makeText(this, "Request Denied", Toast.LENGTH_SHORT).show()
        }
    }
}
