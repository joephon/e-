package com.newteo.eplus.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.newteo.eplus.R

class BuRecyclerViewAdaptor(private val list: List<GuaListItem>, private val itemLayout: Int = R.layout.gua_list_item_2) : RecyclerView.Adapter<BuRecyclerViewAdaptor.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val guaTitle: TextView = view.findViewById(R.id.guaTitle)
        val guaTip: TextView = view.findViewById(R.id.guaTip)
        val guaHint: TextView = view.findViewById(R.id.guaHint)
        val guaDes: TextView = view.findViewById(R.id.guaDes)
        val guaCover: ImageView = view.findViewById(R.id.guaCover)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(itemLayout, parent, false)

        val viewHolder = ViewHolder(view)
        viewHolder.itemView.setOnClickListener {
            val item = list[viewHolder.layoutPosition]
            GuaDetailsActivity.go(parent.context, item)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        list[position].apply {
            holder.guaTitle.text = this.title
            holder.guaTip.text = this.tip
            holder.guaHint.text = this.hint
            holder.guaDes.text = this.des
            holder.guaCover.setImageResource(this.picId)
        }
    }

    override fun getItemCount(): Int = list.size

}