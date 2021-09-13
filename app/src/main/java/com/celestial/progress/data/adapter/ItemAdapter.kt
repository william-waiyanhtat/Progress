package com.celestial.progress.data.adapter

import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.celestial.progress.R
import com.celestial.progress.data.model.Counter
import com.celestial.progress.databinding.ProgressItemBinding

class ItemAdapter: ListAdapter<Counter, ItemAdapter.ItemViewHolder>(DIFF_UTIL) {

    companion object{
        val DIFF_UTIL = object: DiffUtil.ItemCallback<Counter>() {
            override fun areItemsTheSame(oldItem: Counter, newItem: Counter): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Counter, newItem: Counter): Boolean {
               return oldItem.title == newItem.title && oldItem.createdDate == newItem.createdDate
            }
        }
    }

    inner class ItemViewHolder(val binding: ProgressItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(){
            binding.itemTitleId.text = getItem(adapterPosition).title
            binding.tvCounting.text = getItem(adapterPosition).endDate

            itemView.setOnClickListener {
                val tv = binding.tvDetail

                if(tv.visibility == View.GONE){
                    tv.visibility = View.VISIBLE
                    notifyItemChanged(-1)
                }else{

                    tv.visibility = View.GONE
                    notifyItemChanged(adapterPosition)
                }





            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ProgressItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ItemViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
       with(holder){
           bind()
       }
    }


}