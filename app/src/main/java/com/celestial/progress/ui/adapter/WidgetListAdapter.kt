package com.celestial.progress.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.celestial.progress.R
import com.celestial.progress.data.model.Counter
import com.celestial.progress.databinding.ListviewItemBinding

class WidgetListAdapter(val context: Context, list: ArrayList<Counter>): BaseAdapter() {

    var list: ArrayList<Counter> = ArrayList()

    var inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getCount(): Int {
       return list.size
    }

    override fun getItem(position: Int): Any {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
       return list[position].id?.toLong()!!
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val binding = ListviewItemBinding.inflate(inflater,parent,false)
        return binding.root
    }
}