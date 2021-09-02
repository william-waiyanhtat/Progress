package com.celestial.progress.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.celestial.progress.R
import com.celestial.progress.data.model.Counter
import com.celestial.progress.databinding.ProgressItemBinding

class ItemAdapter(): RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    var itemList = ArrayList<Counter>()


    init {

    }

    inner class ItemViewHolder(val binding: ProgressItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ProgressItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ItemViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
       with(holder){
            this.binding.itemTitleId.text = position.toString()

       }
    }

    override fun getItemCount(): Int {
       return 100
    }

}