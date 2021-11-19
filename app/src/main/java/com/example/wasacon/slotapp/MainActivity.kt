package com.example.wasacon.slotapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wasacon.slotapp.data.CustomRecyclerViewAdapter
import com.example.wasacon.slotapp.history.FourthFragment
import com.example.wasacon.slotapp.history.FourthHistoryRecyclerViewAdapter
import com.example.wasacon.slotapp.history.HistoryActivity
import com.example.wasacon.slotapp.history.HistoryRecyclerViewAdapter
import com.example.wasacon.slotapp.realmObjects.AccountData
import com.example.wasacon.slotapp.realmObjects.BallData
import com.example.wasacon.slotapp.realmObjects.FourthData
import io.realm.Realm
import io.realm.Sort
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_data.*
import java.lang.StringBuilder
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var realm : Realm

    private val FORTH = ""
    private val MINUS = "マイナス"
    private val FIRST_MAX = 70000
    private val FIRST_MIN = 30000
    private val SECOND_MAX = 20000
    private val SECOND_MIN = 10000
    private val THIRD_MAX = 2000
    private val THIRD_MIN = 1000

    // omitted in Nov 29 2020
    // private val fourthHistoryFragment = FourthFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /* omitted in Nov 29 2020
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.fragment_container, fourthHistoryFragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()

         */

        setContentView(R.layout.activity_main)
        //haltServiceBtn.visibility = View.INVISIBLE

        realm = Realm.getDefaultInstance()

        //マイナスボタンがクリックされたか
        var isMinusBtnClicked : Boolean = false

        //賞金
        var result = 0

        //スロットゲーム画面への遷移先
        val intentRewardActivity = Intent(this, RewardActicity::class.java)

        //数字キーの配列
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

        val dPair = DateAdministrator.bindDay(Date())

        //四等
        forthBtn.setOnClickListener {
            val count = confirmTxt.text.toString().toInt()

            if (!isMinusBtnClicked && getBalance(dPair) - 50 * count  <= 0) {

                val inCorrect = MediaPlayer.create(this, R.raw.incorrect1)
                inCorrect.seekTo(0)
                inCorrect.start()
                Toast.makeText(applicationContext, "エラー!, 入金してください!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!confirmTxt.text.equals("0")) {
                if (!isMinusBtnClicked) {

                    /*Log.d("Number of 4th:", count.toString())
                    if (confirmTxt.text.equals("0")) {
                        result += 50
                        priceTxt.text = result.toString()
                    }*/

                    result = count * 50
                    priceTxt.text = result.toString()

                    //for (i in 1..count) {
                    realm.executeTransaction {
                        val date = Date()
                        val maxId = realm.where<FourthData>().max("id")
                        val nextId = (maxId?.toLong() ?: 0L) + 1L
                        val fourthData = realm.createObject<FourthData>(nextId)
                        fourthData.dateTime = date
                        fourthData.result = 50 * count
                        fourthData.count = count

                        val maxBallId = realm.where<BallData>().max("id")

                        val previousNumBalls = realm.where<BallData>()
                            .sort("dateTime", Sort.DESCENDING)
                            .findFirst()?.numBalls ?: 0

                        val nextBallId = (maxBallId?.toLong() ?: 0L) + 1L
                        val ballData = realm.createObject<BallData>(nextBallId)
                        ballData.numBalls = previousNumBalls - count
                        ballData.dateTime = date

                        //*残高
                        val maxAccountId = realm.where<AccountData>().max("id")

                        val mostRecentBalance = realm.where(AccountData::class.java)
                            .greaterThanOrEqualTo("dateTime", dPair.first)
                            .findAll()
                            .where()
                            .lessThanOrEqualTo("dateTime", dPair.second)
                            .sort("dateTime", Sort.DESCENDING)
                            .findFirst()?.balance ?: 0

                        /*
                        val mostRecentBalance = realm.where<AccountData>()
                            .sort("dateTime", Sort.DESCENDING)
                            .findFirst()?.balance ?: 0

                         20201119*/

                        val nextAccountId = (maxAccountId?.toLong() ?: 0L) + 1L
                        val accountData = realm.createObject<AccountData>(nextAccountId)
                        accountData.balance = mostRecentBalance - 50 * count
                        accountData.dateTime = date
                    }
                } else {

                    Log.d("Number of 4th:", count.toString())
                    /*if (confirmTxt.text.equals("0")) {
                        result -= 50
                        priceTxt.text = result.toString()
                    } else {*/

                    result = count * -50
                    priceTxt.text = result.toString()

                    //for (i in 1..count) {
                    realm.executeTransaction {
                        val maxId = realm.where<FourthData>().max("id")
                        val nextId = (maxId?.toLong() ?: 0L) + 1L
                        val fourthData = realm.createObject<FourthData>(nextId)
                        fourthData.dateTime = Date()
                        fourthData.result = result
                        fourthData.count = -count

                        val maxBallId = realm.where<BallData>().max("id")

                        val previousNumBalls = realm.where<BallData>()
                            .sort("dateTime", Sort.DESCENDING)
                            .findFirst()?.numBalls ?: 0

                        val nextBallId = (maxBallId?.toLong() ?: 0L) + 1L
                        val ballData = realm.createObject<BallData>(nextBallId)
                        ballData.numBalls = previousNumBalls + count
                        ballData.dateTime = Date()

                        //*残高
                        val maxAccountId = realm.where<AccountData>().max("id")
                        val mostRecentBalance = realm.where<AccountData>()
                            .sort("dateTime", Sort.DESCENDING)
                            .findFirst()?.balance ?: 0

                        val nextAccountId = (maxAccountId?.toLong() ?: 0L) + 1L
                        val accountData = realm.createObject<AccountData>(nextAccountId)
                        accountData.balance = mostRecentBalance + 50 * count
                        accountData.dateTime = Date()

                    }
                    //}

                    for (i in 0..9) {
                        buttons[i].setBackgroundResource(R.drawable.key_btn_state)
                        deleteBtn.setBackgroundResource(R.drawable.key_btn_state)
                        forthBtn.setBackgroundResource(R.drawable.btn_forth_state)
                        forthBtn.setTextColor(Color.BLACK)
                    }

                    isMinusBtnClicked = false
                }

                if (queueOfFourthResults.size < 10) {
                    queueOfFourthResults.add(result)
                } else {
                    queueOfFourthResults.removeFirst()
                    queueOfFourthResults.add(result)
                }

                val history4th = StringBuilder()

                val last = queueOfFourthResults[0]

                for (fhistory in queueOfFourthResults.reversed()) {
                    history4th.append("¥" + fhistory.toString() + " : " + fhistory / 50 + "本\n")
                }

                fourthHistoryTxt.text = history4th.toString()

                val handler = Handler()

                val clearDataOperation = object : Runnable{
                    override fun run() {
                        confirmTxt.text = "0"
                        priceTxt.text = "0"
                        result = 0
                    }
                }


                handler.postDelayed(clearDataOperation, 200)
            }
        }

        //電卓の各数字キーが押された時の処理
        for (i in 0..9) {
            buttons[i].setOnClickListener {
                if (confirmTxt.text.equals("0")) {
                    confirmTxt.text = ""
                }
                confirmTxt.text = confirmTxt.text.toString() + i.toString()
            }
        }

        //四等の本数をクリア
        deleteBtn.setOnClickListener {
            confirmTxt.text = "0"
            priceTxt.text = "0"
            result = 0
        }

        //四等の修正
        minusBtn.setOnClickListener {
            isMinusBtnClicked = true

            for (i in 0..9) {
                buttons[i].setBackgroundResource(R.drawable.btn_minus_state)
                deleteBtn.setBackgroundResource(R.drawable.btn_minus_state)
                forthBtn.setBackgroundResource(R.drawable.button_state)
                forthBtn.setTextColor(Color.WHITE)
            }
        }

        /*
        //一定周期でデータをバックアップするサービス開始ボタン
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

        //一定周期でデータをバックアップするサービス停止ボタン
        haltServiceBtn.setOnClickListener {
            intent.putExtra("shouldContinue", false)
            stopService(intent)
            startServiceBtn.visibility = View.VISIBLE
            haltServiceBtn.visibility = View.INVISIBLE
        }*/

        //一等
        firstBtn.setOnClickListener {
            if (getBalance(dPair) <= FIRST_MIN) {
                val inCorrect = MediaPlayer.create(this, R.raw.incorrect1)
                inCorrect.seekTo(0)
                inCorrect.start()
                Toast.makeText(applicationContext, "エラー!, 入金してください!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            intentRewardActivity
                .putExtra("no", 1)
                .putExtra("first_min", FIRST_MIN)
                .putExtra("first_max", FIRST_MAX)

            startActivity(intentRewardActivity)
        }

        //二等
        secondBtn.setOnClickListener {
            if (getBalance(dPair) <= SECOND_MIN) {
                val inCorrect = MediaPlayer.create(this, R.raw.incorrect1)
                inCorrect.seekTo(0)
                inCorrect.start()
                Toast.makeText(applicationContext, "エラー!, 入金してください!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            intentRewardActivity
                .putExtra("no", 2)
                .putExtra("second_min", SECOND_MIN)
                .putExtra("second_max", SECOND_MAX)
            startActivity(intentRewardActivity)
        }

        //三等
        thirdBtn.setOnClickListener {
            if (getBalance(dPair) <= THIRD_MIN) {
                val inCorrect = MediaPlayer.create(this, R.raw.incorrect1)
                inCorrect.seekTo(0)
                inCorrect.start()
                Toast.makeText(applicationContext, "エラー!, 入金してください!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            intentRewardActivity
                .putExtra("no", 3)
                .putExtra("third_min", THIRD_MIN)
                .putExtra("third_max", THIRD_MAX)
            startActivity(intentRewardActivity)
        }

        //履歴
        histBtn.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }

        //管理者画面へ遷移
        adminBtn.setOnClickListener {
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        realm.close()
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

    private fun getBalance(dPair: Pair<Date, Date>) : Int {
        return realm.where(AccountData::class.java)
            .greaterThanOrEqualTo("dateTime", dPair.first)
            .findAll()
            .where()
            .lessThanOrEqualTo("dateTime", dPair.second)
            .sort("dateTime", Sort.DESCENDING)
            .findFirst()?.balance ?: 0

    }

    companion object {
        val queueOfFourthResults : LinkedList<Int> = LinkedList()
    }
}
