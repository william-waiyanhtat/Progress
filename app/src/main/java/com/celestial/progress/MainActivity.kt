package com.celestial.progress

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import com.celestial.progress.data.adapter.ItemAdapter
import com.celestial.progress.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint



val TAG = MainActivity::class.java.simpleName

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var adapter: ItemAdapter

    private var binding: ActivityMainBinding? = null

    lateinit var callback: ()-> Unit

    lateinit var appBar: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding?.root

        appBar = binding?.appBar!!

        setContentView(view)
        setListener()

        callback = {
            makeVisible()
            Log.d(TAG,"Callback method call")
        }
    }

    fun makeVisible(){
        binding.apply {
            Log.d(TAG,"MAKE VISIBLE")
            this?.appBar?.visibility = View.VISIBLE
//            this?.mainBtmNav?.visibility = View.VISIBLE
        }

    }

    fun setListener(){
        binding?.imgvProfile?.setOnClickListener {
            binding.apply {
                this?.appBar?.let {
                    if(it.visibility == View.VISIBLE){
                        this?.appBar?.visibility = View.GONE
                    }
                }
            }
        }
    }

    fun showHideAppBar(isVisible: Boolean){
        appBar?.let{
            it.visibility = if(isVisible) View.VISIBLE else View.GONE
        }
    }


}

