package com.orange.bamboo.knot.insect.burning.thethirdrecording.show.four

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.orange.bamboo.knot.insect.burning.thethirdrecording.R
import com.orange.bamboo.knot.insect.burning.thethirdrecording.data.RecordingInfo

class HistoryAdapter (private var dataList: List<RecordingInfo>) :
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {
    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun onEditClick(position: Int)
        fun onDeleteClick(position: Int)
    }

    private var listener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgEdit: ImageView = itemView.findViewById(R.id.img_edit)
        val imgDelete: ImageView = itemView.findViewById(R.id.img_delete)
        val tvName: TextView = itemView.findViewById(R.id.tv_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_record, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvName.text = dataList[position].name
        holder.itemView.setOnClickListener {
            listener?.onItemClick(position)
        }
        holder.imgEdit.setOnClickListener {
            listener?.onEditClick(position)
        }

        // Delete button click
        holder.imgDelete.setOnClickListener {
            listener?.onDeleteClick(position)
        }

    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun setData(dataList: List<RecordingInfo>) {
        this.dataList = dataList
        notifyDataSetChanged()
    }

}