package com.celestial.progress.ui.adapter


import android.content.Context
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
import com.celestial.progress.others.NotificationHelper
import com.celestial.progress.others.SharePrefHelper
import com.celestial.progress.ui.component.DeviceUtils
import kotlin.properties.Delegates
import kotlin.reflect.KClass


class ItemAdapter<T : Any>(val expandCollapse: ((Counter) -> Unit)? = null,
                           val itemMenuShow: ((Counter, View) -> Unit?)? = null,
                           val notiIssueCb: ((Counter) -> Unit?)? = null,
                           val t: KClass<T>,
                           val selectCounter: ((Counter) -> Unit?)? = null) :
        ListAdapter<Counter, RecyclerView.ViewHolder>(DIFF_UTIL){


    val TAG = ItemAdapter::class.java.name

    private lateinit var recyclerView: RecyclerView

    private lateinit var mContext: Context

    private var isAnimationOn by Delegates.notNull<Boolean>()

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
        mContext = recyclerView.context
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
            super.bind()
            var model = getItem(adapterPosition)
            val expandGroup = binding.expandGroup
            Log.d(TAG, "OnBind: ${model.title}")
            binding.itemTitleId.text = model.title
         //   binding.tvCounting.text = model.getDetail()

            switchColor(binding.swNoti,model.color!!)


            if(model.isElapsed() && !model.isArchived && isAnimationOn){
                binding.itemProgressBarId.indeterminate = true
                binding.itemProgressBarId.startIndeterminateAnimation()

                Log.d(TAG,"OnBind: animationon")
            }else{
                binding.itemProgressBarId.indeterminate = false
                binding.itemProgressBarId.stopAnimation()
                Log.d(TAG,"OnBind: stop animation")
            }

            binding.itemProgressBarId.color1 = model.color!!

            binding.tvActSdate.text = model.startDate

            if(model.endDate!!.isNotEmpty()){
                binding.tvActEdate.text = model.endDate
            }

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

            if(model.requiredNotification){
                binding.swNoti.isChecked = true
                if(!NotificationHelper.checkNotification(mContext,model)){
                    NotificationHelper.createNotification(mContext,model)
                }
            }else{
                binding.swNoti.isChecked = false
                if(NotificationHelper.checkNotification(mContext,model)){
                    NotificationHelper.cancelNotification(mContext,model)
                }
            }

        //    binding.swNoti.isChecked = NotificationHelper.checkNotification(mContext,model)


            binding.itemMenuBtn.setOnClickListener {
                itemMenuShow?.invoke(model, binding.itemMenuBtn)

            }
            itemView.setOnClickListener {
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
                }
            }

            //switch
            binding.swNoti.setOnClickListener {
                if(binding.swNoti.isChecked){
                    createNotification(model)
                    model.requiredNotification = true
                    notiIssueCb?.invoke(model)
                }else{
                    NotificationHelper.cancelNotification(mContext,model)
                    model.requiredNotification = false
                    notiIssueCb?.invoke(model)
                }

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

            if(model!!.isElapsed() && !model!!.isArchived && isAnimationOn){
                binding.itemProgressBarId.indeterminate = true
                binding.itemProgressBarId.startIndeterminateAnimation()

                Log.d(TAG,"OnBind: animationon")
            }else{
                binding.itemProgressBarId.indeterminate = false
                binding.itemProgressBarId.stopAnimation()
                Log.d(TAG,"OnBind: stop animation")
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
            if(model!!.isElapsed()){
                binding.tvCounting.text = model?.getDetail()
            }else{
                binding.tvCounting.text = model?.getDetail(true)
            }


            binding.itemProgressBarId.indeterminate = model?.isElapsed()!! && !model?.isArchived!!
            binding.itemProgressBarId.color1 = model?.color!!

            Log.d(TAG, "${model?.title} - " + model?.startDate + " : ${model?.getDetail()} -")

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ProgressItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        isAnimationOn = SharePrefHelper.isGlimmerAnimationOff(mContext)
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

    private fun createNotification(counter: Counter){
        NotificationHelper.createNotification(mContext,counter)
    }


}