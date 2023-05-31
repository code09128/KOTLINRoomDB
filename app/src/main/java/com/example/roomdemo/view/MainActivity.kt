package com.example.roomdemo.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.roomdemo.R
import com.example.roomdemo.model.DataTabelModel
import com.example.roomdemo.room.DataDemobase
import com.example.roomdemo.viewmodel.DataViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    lateinit var dataViewModel: DataViewModel
    lateinit var context: Context
    lateinit var adapter: MyAdapter

    private lateinit var strName: String
    private lateinit var strPhone: String
    private lateinit var strHobby: String

    var dataList: MutableList<DataTabelModel> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        context = this

        init()

        dataViewModel = ViewModelProvider(this).get(DataViewModel::class.java)

        //新增
        button_Create.setOnClickListener {
            strName = editText_Name.text.toString().trim()
            strPhone = editText_Phone.text.toString().trim()
            strHobby = editText_Hobby.text.toString().trim()

            if (strName.isEmpty()) {
                editText_Name.error = "Please enter the name"
            } else if (strPhone.isEmpty()) {
                editText_Phone.error = "Please enter the phone"
            } else if (strHobby.isEmpty()) {
                editText_Hobby.error = "Please enter the hobby"
            } else {
                dataViewModel.insertData(context, strName, strPhone, strHobby)
                Toast.makeText(context, "Inserted Successfully", Toast.LENGTH_SHORT).show()

                adapter.refreshView()
            }
        }

        //清空
        button_Clear.setOnClickListener {
            dataViewModel.delete(context)

            adapter.deleteAll()
        }

        button_get.setOnClickListener {
            val datas = dataViewModel.getAllData(context)
            Toast.makeText(context, datas?.size?.toString(),Toast.LENGTH_SHORT).show()
            Log.d("dataListDB", datas?.size.toString())
        }
    }

    /**初始化取得資料*/
    private fun init() {
        CoroutineScope(Dispatchers.IO).launch {
            val data: List<DataTabelModel> = DataDemobase.getDatabaseClient(this@MainActivity).DataDao().displayAll()
            dataList = data as MutableList<DataTabelModel>

            recyclerView.layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
            adapter = MyAdapter(this@MainActivity, dataList)
            recyclerView.adapter = adapter
        }
    }
}