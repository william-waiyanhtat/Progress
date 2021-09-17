package com.celestial.progress.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.celestial.progress.data.model.Counter
import com.celestial.progress.databinding.ProgressItemBinding

class ItemAdapter(val expandCollapse: ((Counter) -> Unit)? = null, val itemMenuShow: ((Counter, View) -> Unit?)? = null) : ListAdapter<Counter, ItemAdapter.ItemViewHolder>(DIFF_UTIL) {


    val TAG = ItemAdapter::class.java.name

    companion object {
        val DIFF_UTIL = object : DiffUtil.ItemCallback<Counter>() {
            override fun areItemsTheSame(oldItem: Counter, newItem: Counter): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Counter, newItem: Counter): Boolean {
                return oldItem.title == newItem.title && oldItem.createdDate == newItem.createdDate && oldItem.order == newItem.order
            }
        }
    }

    inner class ItemViewHolder(val binding: ProgressItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            var model = getItem(adapterPosition)

            val expandGroup = binding.expandGroup

            binding.itemTitleId.text = model.title
            binding.tvCounting.text = model.getDetail()

            binding.itemProgressBarId.indeterminate = model.isElapsed!! && !model.isArchived


            binding.itemProgressBarId.color1 = model.color!!

            if (model.isExpand) {
                expandGroup.visibility = View.VISIBLE
            } else {
                expandGroup.visibility = View.GONE
            }

            //complete check
            if (model.isComplete()) {
                binding.completeBadge.visibility = View.VISIBLE
            } else {
                binding.completeBadge.visibility = View.GONE
            }

            Log.d(TAG, "${model.title} - " + model.startDate + " : ${model.getDetail()} -")

            binding.itemMenuBtn.setOnClickListener {
                itemMenuShow?.invoke(model, binding.itemMenuBtn)

            }

            itemView.setOnClickListener {


                if (!model.isExpand) {
                    expandGroup.visibility = View.VISIBLE
                    model.isExpand = true
                    //  notifyItemChanged(-1)

                } else {
                    expandGroup.visibility = View.GONE
                    model.isExpand = false
                    // notifyItemChanged(adapterPosition)
                }
                expandCollapse?.invoke(model)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ProgressItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        with(holder) {
            bind()
        }
    }


}