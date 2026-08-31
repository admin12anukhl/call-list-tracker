package com.calltracker.app.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.calltracker.app.R
import com.calltracker.app.data.CallRecord
import java.text.SimpleDateFormat
import java.util.*

class CallAdapter : ListAdapter<CallRecord, CallAdapter.VH>(DIFF) {
    companion object {
        val DIFF = object : DiffUtil.ItemCallback<CallRecord>() {
            override fun areItemsTheSame(oldItem: CallRecord, newItem: CallRecord): Boolean = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: CallRecord, newItem: CallRecord): Boolean = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_call, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNumber: TextView = itemView.findViewById(R.id.tvNumber)
        private val tvMeta: TextView = itemView.findViewById(R.id.tvMeta)

        fun bind(r: CallRecord) {
            tvNumber.text = r.number
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val t = sdf.format(Date(r.timestamp))
            val loc = if (r.latitude != null && r.longitude != null) "lat=${r.latitude}, lon=${r.longitude}" else "location=N/A"
            tvMeta.text = "${r.type} • $t • $loc"
        }
    }
}
