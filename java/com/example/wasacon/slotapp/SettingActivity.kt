package com.example.wasacon.slotapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.text.format.DateFormat
import android.util.Log
import android.widget.Button
import android.widget.Toast
import io.realm.Realm
import io.realm.RealmQuery
import io.realm.RealmResults
import io.realm.Sort
import kotlinx.android.synthetic.main.activity_setting.*
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.lang.Exception
import java.nio.charset.StandardCharsets
import java.util.*

class SettingActivity : AppCompatActivity() {
    private lateinit var realm : Realm
    private val tag : String = "Writing File:"

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
        returnBtn.setOnClickListener {
            finish()
        }

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
    }

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
                    for (result in results) {
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

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}
