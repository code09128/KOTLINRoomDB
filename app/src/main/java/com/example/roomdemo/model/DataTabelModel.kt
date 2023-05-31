package com.example.roomdemo.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "MyTabelData")
class DataTabelModel(
    @ColumnInfo(name = "name")
    var Name: String,

    @ColumnInfo(name = "phone")
    var Phone: String,

    @ColumnInfo(name = "hobby")
    var Hobby: String
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var Id: Int? = null
}