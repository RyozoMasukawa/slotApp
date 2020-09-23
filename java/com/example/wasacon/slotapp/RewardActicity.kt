package com.example.wasacon.slotapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import kotlinx.android.synthetic.main.activity_reward_acticity.*
import java.util.*
import kotlin.random.Random
import android.os.Looper
import android.media.MediaPlayer
import android.os.Handler
import io.realm.Realm
import io.realm.Sort
import io.realm.kotlin.createObject
import io.realm.kotlin.where


class RewardActicity : AppCompatActivity() {

    //The result of lottery
    private var value : Int  = 0

    private val THIRD : String = ""

    //SE player of switching each digits
    private var c6 : MediaPlayer = MediaPlayer()
    //SE player when showing result
    private var fanfare : MediaPlayer = MediaPlayer()

    private var rewardNo : Int = 0

    private lateinit var realm : Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reward_acticity)

        editBtn.visibility = View.INVISIBLE
        praiseTxt.visibility = View.INVISIBLE
        imageView2.visibility = View.INVISIBLE



                realm = Realm.getDefaultInstance()

        val drawables : Array<Int> = arrayOf(R.drawable.w0
            , R.drawable.w1
            , R.drawable.w2
            , R.drawable.w3
            , R.drawable.w4
            , R.drawable.w5
            , R.drawable.w6
            , R.drawable.w7
            , R.drawable.w8
            , R.drawable.w9
        )

        val imageViews : Array<ImageView> = arrayOf(digit1
            ,digit2
            ,digit3
            ,digit4
            ,digit5
            ,digit6
        )

        rewardNo = intent.getIntExtra("no", 0)
        returnBtn.visibility = View.INVISIBLE

        depositBtn.setOnClickListener {
            var min : Int = 0
            var max : Int = 100000
            var div : Int = 1

            //Set the range of random value
            when (rewardNo) {
                1 -> {
                    min = intent.getIntExtra("first_min", 0)
                    max = intent.getIntExtra("first_max", 100000)
                    div = 1000
                }

                2 -> {
                    min = intent.getIntExtra("second_min", 0)
                    max = intent.getIntExtra("second_max", 100000)
                    div = 1000
                }

                3 -> {
                    min = intent.getIntExtra("third_min", 0)
                    max = intent.getIntExtra("third_max", 100000)
                    div = 10
                }
            }
            value = (Random.nextInt(min, max) / div) * div

            Log.d("¥", value.toString())

            showResult(drawables, imageViews, value)
            //showToast("結果を保存しました！！")
            depositBtn.visibility = View.INVISIBLE
        }

        returnBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            c6.release()
            fanfare.release()
        }

        editBtn.setOnClickListener {
            c6.release()
            fanfare.release()

            val intent = Intent(this, EditActivity::class.java)
                .putExtra("result", value.toLong())
                .putExtra("rank", rewardNo)

            startActivity(intent)
        }
    }

    private fun getDigits(value : Int) : List<Int> {
        val ls : MutableList<Int> = mutableListOf()
        var x : Int = value
        while (x > 0) {
            ls.add(x % 10)
            x /= 10
        }
        return  ls.toList()
    }

    private fun showResult(drawables : Array<Int>, imageViews : Array<ImageView>, resultValue : Int) {
        val resultList: List<Int> = getDigits(resultValue)

        val handler = Handler(Looper.getMainLooper())

        val showResultImages = object : Runnable {
            override fun run() {
                showImage(resultList, drawables, imageViews)
                if (resultValue < 100000) {
                    imageViews[0].visibility = View.INVISIBLE
                }

                if (resultValue < 10000) {
                    imageViews[0].visibility = View.INVISIBLE
                    imageViews[1].visibility = View.INVISIBLE
                }
            }
        }

        c6 = MediaPlayer.create(this, R.raw.cursor6)
        fanfare = MediaPlayer.create(this, R.raw.decision24)

        val shuffle = object : Runnable {
            var cnt = 0
            var i = imageViews.size - 1

            override fun run() {
                cnt++
                if (cnt < 25) {
                    for (j in 0..(imageViews.size - 1)) {
                        imageViews[j].setImageResource(drawables[(cnt + j) % 10])
                    }
                    c6.seekTo(0)
                    c6.start()
                    handler.postDelayed(this, 100)
                } else {
                    imageViews[i].setImageResource(drawables[(cnt + i) % 10])
                    handler.post(showResultImages)
                }
            }
        }

        handler.post(shuffle)
    }

    private fun showImage(resultList: List<Int>, drawables : Array<Int>, imageViews : Array<ImageView>) {
        var i = imageViews.size - 1
        val handler = Handler()

        val imageTick = object : Runnable {
            var cnt = 0

            override fun run() {
                var count = 0
                for (j in 0..(i - 1)) {
                    imageViews[j].setImageResource(drawables[count++])
                }
                imageViews[i].setImageResource(drawables[resultList[cnt % resultList.size] % drawables.size])
            }
        }

        val setImage = object : Runnable {
            var cnt = 0
            override fun run() {
                if (cnt < resultList.size) {
                    imageTick.cnt = cnt
                    handler.postDelayed(imageTick, 100)
                    imageViews[i].setImageResource(drawables[resultList[cnt]])
                    i--
                    cnt++
                    c6.seekTo(0)
                    c6.start()
                    handler.postDelayed(this, 400)
                } else {
                    if (rewardNo == 3) {
                        executeData()
                    } else {
                        editBtn.visibility = View.VISIBLE
                    }
                    fanfare.seekTo(0)
                    fanfare.start()
                    imageView2.visibility = View.VISIBLE

                    returnBtn.visibility = View.VISIBLE
                    praiseTxt.visibility = View.VISIBLE
                }
            }
        }
        handler.post(setImage)
    }


    /*private fun showToast(msg : String) {
        val toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT)
        toast.show()
    }*/

    override fun onDestroy() {
        super.onDestroy()
        c6.release()
        fanfare.release()
    }

    private fun executeData() {
        realm.executeTransaction {
            val maxId = realm.where<ResultData>().max("id")
            val nextId = (maxId?.toLong() ?: 0L) + 1L
            val resultData = realm.createObject<ResultData>(nextId)
            resultData.dateTime = Date()
            resultData.result = value.toLong()
            resultData.rank = rewardNo
            resultData.address = THIRD
            resultData.name = THIRD
            resultData.postal = THIRD

            val maxBallId = realm.where<BallData>().max("id")
            val previousNumBalls = realm.where<BallData>()
                .sort("dateTime", Sort.DESCENDING)
                .findFirst()?.numBalls ?: 1

            val nextBallId = (maxBallId?.toLong() ?: 0L) + 1L
            val ballData = realm.createObject<BallData>(nextBallId)
            ballData.numBalls = previousNumBalls - 1
            ballData.dateTime = Date()
        }
    }
}
