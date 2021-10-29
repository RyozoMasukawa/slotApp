package com.example.wasacon.slotapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.wasacon.slotapp.realmObjects.AccountData
import com.example.wasacon.slotapp.realmObjects.BallData
import com.example.wasacon.slotapp.realmObjects.FourthData
import com.example.wasacon.slotapp.realmObjects.ResultData
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_confirm.*

class ConfirmActivity : AppCompatActivity() {
    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm)
        realm = Realm.getDefaultInstance()

        confirmBtn.setOnClickListener {
            realm.executeTransaction {
                realm.where(ResultData::class.java)
                    .findAll()
                    .deleteAllFromRealm()

                realm.where(FourthData::class.java)
                    .findAll()
                    .deleteAllFromRealm()

                realm.where(AccountData::class.java)
                    .findAll()
                    .deleteAllFromRealm()

                realm.where(BallData::class.java)
                    .findAll()
                    .deleteAllFromRealm()
            }

            val pref = getSharedPreferences(getString(R.string.num_balls), Context.MODE_PRIVATE)
            val editor = pref.edit()
            editor.putInt("numBalls", 0).apply()

            showToast("All data deleted!")

            finish()
        }

        cancelBtn.setOnClickListener {
            val intentToSetting = Intent(this, SettingActivity::class.java)
            startActivity(intentToSetting)
        }
    }

    private fun showToast(msg : String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}
