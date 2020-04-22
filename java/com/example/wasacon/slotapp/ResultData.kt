package com.example.wasacon.slotapp

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class ResultData : RealmObject() {
    @PrimaryKey
    var id: Long = 0
    var dateTime : Date = Date()
    var rank : Int = 0
    var result : Long = 0
    var address : String = ""
    var name : String = ""
    var postal : String = ""
}