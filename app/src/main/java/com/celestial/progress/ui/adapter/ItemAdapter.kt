package com.celestial.progress.ui.adapter


import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SwitchCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.celestial.progress.R
import com.celestial.progress.data.model.Counter
import com.celestial.progress.databinding.ProgressItemBinding
import com.celestial.progress.ui.component.DeviceUtils
import kotlin.reflect.KClass


class ItemAdapter<T : Any>(val expandCollapse: ((Counter) -> Unit)? = null,
                           val itemMenuShow: ((Counter, View) -> Unit?)? = null,
                           val t: KClass<T>,
                           val selectCounter: ((Counter) -> Unit?)? = null) :
        ListAdapter<Counter, RecyclerView.ViewHolder>(DIFF_UTIL){


    val TAG = ItemAdapter::class.java.name

    private lateinit var recyclerView: RecyclerView

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }


    companion object {
        val DIFF_UTIL = object : DiffUtil.ItemCallback<Counter>() {
            override fun areItemsTheSame(oldItem: Counter, newItem: Counter): Boolean {
                return oldItem.id == newItem.id && oldItem.order != newItem.order
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

            switchColor(binding.swNoti,model.color!!)


            if(model.isElapsed() && !model.isArchived){
                binding.itemProgressBarId.indeterminate = true
                binding.itemProgressBarId.starIndeterminateAnimation()
            }else{
                binding.itemProgressBarId.indeterminate = false
                binding.itemProgressBarId.starIndeterminateAnimation()
            }

            binding.itemProgressBarId.color1 = model.color!!

            if(!model.isElapsed()){
                val percent =  model.getPercent()?.toInt() ?: 100
                binding.itemProgressBarId.progress = percent
                binding.itemProgressBarId.indeterminate = false
                binding.itemPercentId.text = "$percent %"
            }else{
                binding.itemProgressBarId.progress = 100
                binding.itemPercentId.visibility = View.GONE
            }

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

              //  var model = currentList.get(adapterPosition)
                val layout = binding.progressItemParent
                val layoutTransition = layout.layoutTransition


                if (!model.isExpand) {
                    expandGroup.visibility = View.VISIBLE
                    model.isExpand = true
                    if(adapterPosition == currentList.size-1){
                        recyclerView?.scrollToPosition(adapterPosition)
                        notifyItemChanged(adapterPosition)
                    }else{
                        notifyItemChanged(-1)
                    }


                } else {
                    expandGroup.visibility = View.GONE
                    model.isExpand = false
                    if(adapterPosition == currentList.size-1){

                        notifyItemChanged(adapterPosition)
                    }else{
                        notifyItemChanged(-1)
                    }


                  //  notifyItemChanged(-1)
                   // notifyItemChanged(-1)
                }
              //  notifyItemChanged(adapterPosition)
           //     expandCollapse?.invoke(model)
             //   notifyItemChanged(-1)
//                layoutTransition.addTransitionListener(object : LayoutTransition.TransitionListener {
//                    override fun startTransition(transition: LayoutTransition?, container: ViewGroup?, view: View?, transitionType: Int) {
//                        Log.d(TAG, "startTransition: position $adapterPosition ")
//                        notifyItemChanged(-1)
//                     //   expandCollapse?.invoke(model)
//                        //  notifyItemChanged(adapterPosition)
//
//                    }
//
//                    override fun endTransition(transition: LayoutTransition?, container: ViewGroup?, view: View?, transitionType: Int) {
//                        Log.d(TAG, "endTransition: ")
//                       //expandCollapse?.invoke(model)
//                  //      notifyItemChanged(-1)
//
//                    }
//                })



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

            binding.itemProgressBarId.indeterminate = model?.isElapsed()!! && !model?.isArchived!!
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

    fun switchColor(switchCompat: SwitchCompat, color: Int){
        val states = arrayOf(intArrayOf(-android.R.attr.state_checked), intArrayOf(android.R.attr.state_checked))

        val thumbColors = intArrayOf(
                Color.LTGRAY,
                color)

        val trackColors = intArrayOf(
                Color.GRAY,
                DeviceUtils.darker(color, 0.6f))


        DrawableCompat.setTintList(DrawableCompat.wrap(switchCompat.thumbDrawable), ColorStateList(states, thumbColors))
        DrawableCompat.setTintList(DrawableCompat.wrap(switchCompat.trackDrawable), ColorStateList(states, trackColors))
    }

}