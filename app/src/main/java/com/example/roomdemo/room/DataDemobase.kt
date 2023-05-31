package com.example.roomdemo.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.roomdemo.model.DataTabelModel

@Database(entities = arrayOf(DataTabelModel::class), version = 1, exportSchema = false)
abstract class DataDemobase:RoomDatabase() {

    abstract fun DataDao() : DataDao

    companion object{

        @Volatile
        private var INSTANCE:DataDemobase? = null

        fun getDatabaseClient(context: Context):DataDemobase{
            if (INSTANCE != null) return INSTANCE!!

            synchronized(this) {

                INSTANCE = Room
                    .databaseBuilder(context, DataDemobase::class.java, "DataDemobase_database")
                    .fallbackToDestructiveMigration()
                    .build()

                return INSTANCE!!
            }
        }

    }
}