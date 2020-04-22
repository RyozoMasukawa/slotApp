package com.example.wasacon.slotapp

import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.os.Environment.getExternalStorageDirectory
import android.os.Handler
import android.os.IBinder
import android.text.format.DateFormat
import android.util.Log
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import java.lang.Exception
import java.util.*
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.os.Environment.getExternalStorageState
import androidx.core.app.ActivityCompat
import java.io.*
import java.nio.charset.StandardCharsets
import android.Manifest
import android.widget.Toast


class FileUpdateService : Service() {
    private lateinit var realm: Realm

    private val DELAY : Long = 30000

    private val CSV_HEADER = "id,ランク,日付時刻,名前,郵便番号,住所,賞金"

    private val tag : String = "Writing File:"

    private var numData : Int = 0

    override fun onCreate() {
        super.onCreate()
        realm = Realm.getDefaultInstance()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val realmResults = realm.where(ResultData::class.java)
            .findAll()
            .sort("id", Sort.DESCENDING)
        if (intent != null){
            writeFile(realmResults, intent.getBooleanExtra("shouldContinue", true))
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun writeFile(results: RealmResults<ResultData>?, shouldContinue : Boolean) {

        val handler = Handler()

        val fileWriting = object : Runnable {

            lateinit var file : File
            lateinit var filename : String

            override fun run() {
                val path : File? = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
                if (isExternalStorageWritable()) {
                    try {
                        Log.d("path : ", path.toString())

                        filename = "resultData" + DateFormat.format( "yyyy:MM:dd:kk:mm", Date()) + ".csv"

                        file = File(path, filename)

                        val fileOutputStream = FileOutputStream(file, true)
                        val outputStreamWriter = OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8)
                        val bw = BufferedWriter(outputStreamWriter)

                        bw.write(CSV_HEADER)
                        bw.write(",")

                        if (results != null) {
                            for (result in results) {
                                bw.write(result.id.toString())
                                bw.write(",")
                                bw.write(result.rank.toString())
                                bw.write(",")
                                bw.write(result.dateTime.toString())
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
                        numData++
                        Log.d(tag, "Writing " + file.toString() + " completed!")
                        Log.d("Number of data", numData.toString())
                    } catch (e: Exception) {
                        Log.d(tag, "Failed in writing file")
                        e.printStackTrace()
                    }
                    if (shouldContinue){
                        handler.postDelayed(this, DELAY)
                    } else {
                        Log.d("Service", "Service Finish")
                        stopSelf()
                    }
                }
            }
        }
        handler.post(fileWriting)
    }

    override fun onDestroy() {
        writeFile(null, false)
        stopSelf()
        super.onDestroy()
    }

    private fun isExternalStorageWritable() : Boolean {
        val state : String = getExternalStorageState()
        return (Environment.MEDIA_MOUNTED.equals(state));
    }

    private fun showToast(msg : String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    /*private fun sendEmail(address : Uri, subject : String, fileWriter : FileWriter) {
        val intent = Intent(Intent.ACTION_SEND, address)
            .putExtra(Intent.EXTRA_SUBJECT, subject)
            .putExtra(Intent.EXTRA_TEXT, FileWriter.)
            .putExtra(Intent.EXTRA_FROM_STORAGE, fileWriter)
    }*/
}
