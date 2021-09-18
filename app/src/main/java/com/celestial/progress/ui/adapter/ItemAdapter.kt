package com.celestial.progress.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.celestial.progress.R
import com.celestial.progress.data.model.Counter
import com.celestial.progress.databinding.ProgressItemBinding
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

class ItemAdapter<T : Any>(val expandCollapse: ((Counter) -> Unit)? = null,
                           val itemMenuShow: ((Counter, View) -> Unit?)? = null,
                           val t: KClass<T>,
                           val selectCounter: ((Counter) -> Unit?)? = null) :
        ListAdapter<Counter, RecyclerView.ViewHolder>(DIFF_UTIL) {


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

    inner class ItemViewHolder(binding: ProgressItemBinding) : TopLevelHolder(binding) {
        override fun bind() {
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

    inner class WidgetSelectionViewHolder(binding: ProgressItemBinding) : TopLevelHolder(binding) {
        override fun bind() {
            super.bind()
            binding.imgvWidgetChkbox.visibility = View.VISIBLE

            itemView.setOnClickListener {
                    for(c in currentList){
                        if(c.isCheckedForWidget){
                            c.isCheckedForWidget = false
                        }
                    }
                model?.isCheckedForWidget = true
                notifyDataSetChanged()
            }

            if(model?.isCheckedForWidget!!){
                binding.imgvWidgetChkbox.setImageDrawable(itemView.context.getDrawable(R.drawable.ic_check))
                selectCounter?.invoke(model!!)
            }else{
                binding.imgvWidgetChkbox.setImageDrawable(null)
            }

        }



    }

    abstract inner class TopLevelHolder(val binding: ProgressItemBinding) : RecyclerView.ViewHolder(binding.root) {
        var model: Counter? = null
        open fun bind() {
            model = getItem(adapterPosition)
            binding.itemTitleId.text = model?.title
            binding.tvCounting.text = model?.getDetail()

            binding.itemProgressBarId.indeterminate = model?.isElapsed!! && !model?.isArchived!!
            binding.itemProgressBarId.color1 = model?.color!!

            Log.d(TAG, "${model?.title} - " + model?.startDate + " : ${model?.getDetail()} -")

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ProgressItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        when (t) {
            ItemViewHolder::class -> {
                Log.d(TAG, "ITEMVIEW HOlder")
                return ItemViewHolder(binding)
            }
            WidgetSelectionViewHolder::class -> {
                Log.d(TAG, "Widget HOlder")
                return WidgetSelectionViewHolder(binding)
            }
        }
        return ItemViewHolder(binding)

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ItemAdapter<*>.ItemViewHolder -> {
                holder.bind()
            }
            is ItemAdapter<*>.WidgetSelectionViewHolder -> {
                holder.bind()
            }
        }
    }


}