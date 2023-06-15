# KOTLINRoomDB
Room DB demo application 

<!-- ![Screenshot_1685417904](https://github.com/code09128/KOTLINRoomDB/assets/32324308/df476dbf-dd0f-4ecc-8c31-90e4ef8c6444) -->
<div  align="center"> 
<img src="https://github.com/code09128/KOTLINRoomDB/assets/32324308/df476dbf-dd0f-4ecc-8c31-90e4ef8c6444" width="350px"/>
</div>

## ROOM Create Database 
```kotlin
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
```

## DAO DataBase 
```kotlin
@Dao
interface DataDao {

    /**簡易新增所有資料的方法*/
    @Insert(onConflict = OnConflictStrategy.REPLACE) //預設萬一執行出錯，REPLACE為覆蓋
    suspend fun InsertData(dataTabelModel: DataTabelModel)

    @Query("SELECT * FROM MyTabelData WHERE Name =:name")
    fun getDataDetails(name: String?): LiveData<DataTabelModel>

    /**撈取全部資料 */
    @Query("SELECT * FROM  MyTabelData")
    suspend fun displayAll(): List<DataTabelModel>

    /**簡易更新資料的方法 */
    @Update
    suspend fun updateData(myData: DataTabelModel?)

    /**刪除資料*/
    @Query("DELETE  FROM MyTabelData WHERE id = :id")
    suspend fun deleteData(id: Int)

    @Query("DELETE FROM MyTabelData")
    suspend fun deleteAllData()
}
```

## DataTabelModel Table ColumnInfo
``` kotlin
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
```

## DataViewModel
```kotlin
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
```

## Repository setting database 
```kotlin
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
```

## MainActivity
```kotlin
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
```

## Adapter
```kotlin
package com.example.roomdemo.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.roomdemo.R
import com.example.roomdemo.model.DataTabelModel
import com.example.roomdemo.room.DataDemobase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyAdapter(var context: MainActivity, var datas:MutableList<DataTabelModel>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.item, parent, false)

        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.tvName.text = datas[position].Name
        holder.tvPhone.text = datas[position].Phone
        holder.tvHobby.text = datas[position].Hobby
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    /**刷新*/
    fun refreshView(){
        CoroutineScope(Dispatchers.IO).launch{
            val data: List<DataTabelModel> = DataDemobase.getDatabaseClient(context).DataDao().displayAll()
            datas = data as MutableList<DataTabelModel>
        }

        notifyDataSetChanged()
    }

    /**刪除*/
    fun deleteAll(){
        datas.clear()
        notifyDataSetChanged()
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvPhone: TextView = itemView.findViewById(R.id.tvPhone)
        val tvHobby:TextView = itemView.findViewById(R.id.tvHobby)
    }
}
```
