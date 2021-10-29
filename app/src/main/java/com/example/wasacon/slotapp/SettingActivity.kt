package com.example.wasacon.slotapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import com.example.wasacon.slotapp.realmObjects.AccountData
import com.example.wasacon.slotapp.realmObjects.BallData
import com.example.wasacon.slotapp.realmObjects.ResultData
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_setting.*
import kotlinx.android.synthetic.main.activity_setting.depositBtn
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.lang.Exception
import java.lang.NumberFormatException
import java.nio.charset.StandardCharsets
import java.util.*

class SettingActivity : FragmentActivity() {
    private lateinit var realm : Realm
    private val tag : String = "Writing File:"
    private var numBallsAppreared : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        realm = Realm.getDefaultInstance()

        val buttons : Array<Button> = arrayOf(firstExcelBtn, secondExcelBtn, thirdExcelBtn)

        //等ごとに分けたデータベース取得
        val arrayOfRankQuery : Array<RealmResults<ResultData>?> = arrayOfNulls(5)

        for (i : Int in 0..3) {
            arrayOfRankQuery[i] = realm.where(ResultData::class.java)
                .equalTo("rank", i + 1).findAll()
            Log.d("count = ", arrayOfRankQuery[i]?.count().toString())
        }

        //マイナスのデータベース取得
        arrayOfRankQuery[4] = realm.where(ResultData::class.java)
            .equalTo("rank", -4 as Int).findAll()

        //集計データ取得
        payOffBtn.setOnClickListener {
            writeFileGeneral(arrayOfRankQuery)
        }

        //戻る
        /*returnBtn.setOnClickListener {
            finish()
        }*/

        for (i in 0..2) {
            buttons[i].setOnClickListener {
                writeFileForEachRank(arrayOfRankQuery[i],i + 1)
            }
        }

        //データクリア
        dataClearBtn.setOnClickListener {
            val intentConfirm = Intent(this, ConfirmActivity::class.java)
            startActivity(intentConfirm)
        }

        //玉追加
        addBtn.setOnClickListener {
            setNumBallsAlert(false)
        }

        //入金
        depositBtn.setOnClickListener {
            setAccountAlert(false)
        }

        numBallsAppreared = 0
        for (i in 0..(arrayOfRankQuery.size - 1)) {
            if (i == arrayOfRankQuery.size - 1) {
                numBallsAppreared -= arrayOfRankQuery[i]?.count() as Int
            } else {
                numBallsAppreared += arrayOfRankQuery[i]?.count() as Int
            }
        }
    }

    /*
    override fun onDialogPositiveClick(dialog: DialogFragment) {
        val input = dialog.view?.findViewById<EditText>(R.id.numBallsEdit)
        if (!input?.text.isNullOrEmpty()) {
            this.numBalls += input?.text.toString().toInt()
        }
        Log.d("numBalls = ", numBalls.toString())
    }*/

    //集計したcsvデータの書き込み
    private fun writeFileGeneral(results: Array<RealmResults<ResultData>?>) {
        val path : File? = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        val CSV_HEADER = "ランク,本数,金額,平均"
        lateinit var file : File
        lateinit var filename : String

        if (isExternalStorageWritable()) {
            try {
                Log.d("path : ", path.toString())

                filename = "集計データ" + DateFormat.format( "yyyy年MM月dd日kk時mm分", Date()) + ".csv"

                file = File(path, filename)

                val fileOutputStream = FileOutputStream(file, true)
                val outputStreamWriter = OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8)
                val bw = BufferedWriter(outputStreamWriter)
                var i : Int = 0

                bw.write(CSV_HEADER)
                bw.write("\n")

                if (results != null) {
                    var sumOfResult = 0L
                    var sumOfCount = 0
                    for (result in results) {
                        sumOfResult += result?.sum("result") as Long

                        i++

                        if(i == 5) {
                            break
                        }

                        if (result != null) {
                            bw.write(i.toString()  + "等")
                            bw.write(",")
                            if (i != 4) {
                                bw.write(result.count().toString())
                                bw.write(",")
                                bw.write(result.sum("result").toString())
                                bw.write(",")
                                bw.write(result.average("result").toString())
                                bw.write("\n")
                            } else {
                                bw.write((result.count() - results?.get(4)?.count() as Int).toString())
                                bw.write(",")
                                val sumOf4 = (result.sum("result") as Long + (results?.get(4)?.sum("result") as Long
                                    ?: 0L)).toString()
                                bw.write(sumOf4)
                                bw.write(",")
                                bw.write("50")
                                bw.write("\n")
                            }

                        }
                    }
                    bw.write("合計," + numBallsAppreared.toString() + "," + sumOfResult.toString() + ",,\n")

                    bw.flush()
                }
                Log.d(tag, "Writing " + file.toString() + " completed!")
                showToast("CSVファイル「" + filename + "」が保存されました！")
            } catch (e: Exception) {
                Log.d(tag, "Failed in writing file")
                e.printStackTrace()
            }
        }
    }

    private fun writeFileForEachRank(results: RealmResults<ResultData>?, rank : Int) {
        val path : File? = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        val CSV_HEADER = "日付時刻,名前,郵便番号,住所,賞金"
        lateinit var file : File
        lateinit var filename : String

        if (isExternalStorageWritable()) {
            try {
                Log.d("path : ", path.toString())

                filename = rank.toString() + "等データ" + DateFormat.format( "yyyy年MM月dd日kk時mm分", Date()) + ".csv"

                file = File(path, filename)

                val fileOutputStream = FileOutputStream(file, true)
                val outputStreamWriter = OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8)
                val bw = BufferedWriter(outputStreamWriter)

                bw.write(CSV_HEADER)
                bw.write("\n")

                if (results != null) {
                    for (result in results) {
                        bw.write(DateFormat.format( "yyyy年MM月dd日kk時mm分", result.dateTime).toString())
                        bw.write(",")
                        bw.write(result.name)
                        bw.write(",")
                        bw.write(result.postal)
                        bw.write(",")
                        bw.write(result.address)
                        bw.write(",")
                        bw.write(result.result.toString())
                        bw.write("\n")
                    }

                    bw.flush()
                }
                Log.d(tag, "Writing " + file.toString() + " completed!")
                showToast("CSVデータ「" + filename + "」が保存されました！")
            } catch (e: Exception) {
                Log.d(tag, "Failed in writing file")
                e.printStackTrace()
            }
        }
    }

    private fun isExternalStorageWritable() : Boolean {
        val state : String = Environment.getExternalStorageState()
        return (Environment.MEDIA_MOUNTED.equals(state));
    }

    private fun showToast(msg : String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun setNumBallsAlert(isError : Boolean) {
        val editText = EditText(this)
        val errorText : TextView = TextView(this)

        if (!isError) {
            errorText.visibility = View.INVISIBLE
        } else {
            errorText.setTextColor(Color.RED)
            errorText.setText(R.string.input_error)
            errorText.visibility = View.VISIBLE
        }

        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT)
        editText.setLayoutParams(layoutParams)
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.add_balls))
            .setView(errorText)
            .setView(editText)
            .setPositiveButton(R.string.confirm_text) { dialog, which ->
                val num = editText.text.toString()

                Log.d("numBalls = " , num)
                updateNumBalls(num)
            }
            .setNegativeButton(R.string.cancel_text) { dialog, which ->
                dialog.cancel()
            }
            .show()
    }

    private fun setAccountAlert(isError : Boolean) {
        val editText = EditText(this)
        editText.hint = "¥"
        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT)
        editText.setLayoutParams(layoutParams)
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.deposit))
            .setView(editText)
            .setPositiveButton(R.string.deposit) { dialog, which ->

                val deposit = editText.text.toString()
                Log.d("deposit = ", deposit)
                updateAccount(deposit)
            }
            .setNegativeButton(R.string.cancel_text) { dialog, which ->
                dialog.cancel()
            }
            .show()

    }

    private fun updateNumBalls(num : String) {
        try {
            num.toInt()
        } catch (e : NumberFormatException) {
            Toast.makeText(applicationContext, "エラー!数値を入力してください!",
                Toast.LENGTH_LONG).show()
            setNumBallsAlert(true)
            return
        }

        val numBalls = num.toInt()

        val maxId = realm.where<BallData>().max("id")?.toLong()

        val previousBallData = realm.where(BallData::class.java)
            .equalTo("id", maxId).findFirst()


        /*
        val dPair = DateAdministrator.bindDay(Date())

        val previousBallData : BallData? = realm.where(BallData::class.java)
            .greaterThanOrEqualTo("dateTime", dPair.first)
            .findAll()
            .where()
            .lessThanOrEqualTo("dateTime", dPair.second)
            .sort("dateTime", Sort.DESCENDING)
            .findFirst()
        */

        val previousNumBalls = previousBallData?.numBalls ?: 0

        if (numBalls + previousNumBalls >= 0) {
            realm.executeTransaction {
                val maxId = realm.where<BallData>().max("id")
                val nextId = (maxId?.toLong() ?: 0L) + 1L
                val ballData = realm.createObject<BallData>(nextId)
                ballData.dateTime = Date()
                ballData.numBalls = numBalls + previousNumBalls
            }
            Toast.makeText(applicationContext, "${num.toString()}玉追加しました！",
                Toast.LENGTH_LONG).show()

        } else {
            showToast("エラー！　玉数がマイナスになってしまいます！")
        }
        Log.d("numBalls = ", (num + previousNumBalls).toString())
    }

    private fun updateAccount(depo : String) {
        try {
            depo.toInt()
        } catch (e : NumberFormatException) {
            Toast.makeText(applicationContext, "エラー!数値を入力してください!",
                Toast.LENGTH_LONG).show()
            setAccountAlert(true)
            return
        }

        val deposit = depo.toInt()

        val dPair = DateAdministrator.bindDay(Date())

        val previousAccountData : AccountData? = realm.where(AccountData::class.java)
            .greaterThanOrEqualTo("dateTime", dPair.first)
            .findAll()
            .where()
            .lessThanOrEqualTo("dateTime", dPair.second)
            .sort("dateTime", Sort.DESCENDING)
            .findFirst()


        val previousBalance = previousAccountData?.balance ?: 0;

        if (deposit + previousBalance >= 0) {
            realm.executeTransaction {
                val maxId = realm.where<AccountData>().max("id")
                val nextId = (maxId?.toLong() ?: 0L) + 1L
                val accountData = realm.createObject<AccountData>(nextId)
                accountData.dateTime = Date()
                accountData.balance = deposit + previousBalance
            }
            Toast.makeText(applicationContext, "¥${deposit}入金しました！",
                Toast.LENGTH_LONG).show()
        } else {
            showToast("エラー！　残高がマイナスになってしまいます！")
        }
        Log.d("Current Balance : ¥", (deposit + previousBalance).toString())
    }

    private fun getCurrentNumBalls() : Int?{
        val maxId = realm.where<BallData>().max("id")?.toLong()
        if (maxId != null) {
            return realm.where<BallData>().equalTo("id", maxId)?.findFirst()?.numBalls

        }
        return 0
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}
