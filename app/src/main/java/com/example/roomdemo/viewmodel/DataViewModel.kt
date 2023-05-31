package com.example.roomdemo.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.roomdemo.model.DataTabelModel
import com.example.roomdemo.repository.Repository


class DataViewModel:ViewModel() {

    var liveDataTable: LiveData<DataTabelModel>? = null
    var datas: List<DataTabelModel>? = null

    /**新增*/
    fun insertData(context: Context, name: String, phone:String, hobby:String){
        Repository.insertData(context,name,phone,hobby)
    }

    /**刪除*/
    fun delete(context: Context){
        Repository.deleteAllData(context)
    }

    fun getAllData(context: Context):List<DataTabelModel>? {
        datas = Repository.getAllData(context)
        return datas
    }

    fun getDataDetail(context: Context, name: String): LiveData<DataTabelModel>? {
        liveDataTable = Repository.getDataDetails(context,name)
        return liveDataTable
    }
}