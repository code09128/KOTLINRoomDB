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