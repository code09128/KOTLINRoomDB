package com.example.roomdemo.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.roomdemo.model.DataTabelModel
import com.example.roomdemo.room.DataDemobase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class Repository {
    companion object {
        var dataDemobase: DataDemobase? = null
        var dataTabelModel: LiveData<DataTabelModel>? = null
        var datas: List<DataTabelModel>? = null

        /**初始取得DB*/
        fun initalDB(context: Context): DataDemobase {
            return DataDemobase.getDatabaseClient(context)
        }

        /**新增資料*/
        fun insertData(context: Context, name: String, phone: String, hobby: String) {
            dataDemobase = initalDB(context)

            CoroutineScope(IO).launch {
                val dataDemo = DataTabelModel(name, phone, hobby)
                dataDemobase!!.DataDao().InsertData(dataDemo)
            }
        }


        fun getAllData(context: Context): List<DataTabelModel>?{
            dataDemobase = initalDB(context)

            CoroutineScope(IO).launch {
                datas = dataDemobase!!.DataDao().displayAll()
            }
            return datas
        }

        /**刪除全部資料*/
        fun deleteAllData(context: Context) {
            dataDemobase = initalDB(context)

            CoroutineScope(IO).launch {
                dataDemobase!!.DataDao().deleteAllData()
            }
        }

        fun getDataDetails(context: Context, name: String): LiveData<DataTabelModel>? {
            dataDemobase = initalDB(context)

            dataTabelModel = dataDemobase!!.DataDao().getDataDetails(name)

            return dataTabelModel
        }
    }
}