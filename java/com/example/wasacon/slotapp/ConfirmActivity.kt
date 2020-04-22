package com.example.wasacon.slotapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
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
            }

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
