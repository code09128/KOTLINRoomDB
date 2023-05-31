package com.example.roomdemo.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.roomdemo.model.DataTabelModel

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