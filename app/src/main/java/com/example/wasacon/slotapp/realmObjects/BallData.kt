package com.example.wasacon.slotapp.realmObjects

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class BallData : RealmObject() {
    @PrimaryKey
    var id: Long = 0
    var dateTime : Date = Date()
    var numBalls : Int = 0
}