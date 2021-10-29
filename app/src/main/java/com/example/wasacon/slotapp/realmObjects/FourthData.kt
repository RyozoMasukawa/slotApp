package com.example.wasacon.slotapp.realmObjects

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class FourthData : RealmObject()  {
    @PrimaryKey
    var id: Long = 0
    var dateTime : Date = Date()
    var result : Int = 0
    var count : Int = 0
}