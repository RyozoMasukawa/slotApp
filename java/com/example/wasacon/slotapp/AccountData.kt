package com.example.wasacon.slotapp

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class AccountData : RealmObject() {
    @PrimaryKey
    var id : Long = 0
    var dateTime : Date = Date()
    var balance : Int = 0
}