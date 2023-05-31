# KOTLINRoomDB
Room DB demo application 

![Screenshot_1685417904](https://github.com/code09128/KOTLINRoomDB/assets/32324308/df476dbf-dd0f-4ecc-8c31-90e4ef8c6444)

#ROOM 
1.create Database 
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

-------------------------------------------------------------------------------------------------

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
