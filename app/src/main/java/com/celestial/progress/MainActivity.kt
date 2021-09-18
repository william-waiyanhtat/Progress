package com.celestial.progress

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.celestial.progress.ui.adapter.ItemAdapter
import com.celestial.progress.data.model.Counter
import com.celestial.progress.databinding.ActivityMainBinding
import com.celestial.progress.others.Status
import com.celestial.progress.others.Utils.showNotification
import com.celestial.progress.ui.CounterViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


val TAG = MainActivity::class.java.simpleName

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var adapter: ItemAdapter<ItemAdapter<*>.ItemViewHolder>

    private var binding: ActivityMainBinding? = null

    lateinit var callback: ()-> Unit

    lateinit var appBar: ConstraintLayout

    lateinit var viewModel: CounterViewModel

    var insertCounter: (Counter,()->Unit) -> Unit = { counter ,func ->
        lifecycleScope.launch {
            viewModel.createCounter(counter).observe(this@MainActivity, androidx.lifecycle.Observer {
                when(it.status){
                    Status.SUCCESS ->{
                        func.invoke()
                        binding?.mainLayout?.let { it1 -> Snackbar.make(it1,it.message!!,Snackbar.LENGTH_LONG).show() }

                    }
                    Status.ERROR ->{
                        binding?.mainLayout?.let { it1 -> Snackbar.make(it1,it.message!!,Snackbar.LENGTH_LONG).show() }
                    }
                }

            })
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding?.root

        viewModel = ViewModelProvider(this)[CounterViewModel::class.java]

       // appBar = binding?.appBar!!

        setContentView(view)
 //       setListener()

        callback = {
            makeVisible()
            Log.d(TAG,"Callback method call")
        }

        showNotification(this,"ABC","DEF")


    }

    fun makeVisible(){
        binding.apply {
            Log.d(TAG,"MAKE VISIBLE")
        //    this?.appBar?.visibility = View.VISIBLE
//            this?.mainBtmNav?.visibility = View.VISIBLE
        }

    }

//    fun setListener(){
//        binding?.imgvProfile?.setOnClickListener {
//            binding.apply {
//                this?.appBar?.let {
//                    if(it.visibility == View.VISIBLE){
//                        this?.appBar?.visibility = View.GONE
//                    }
//                }
//            }
//        }
//    }

    fun showHideAppBar(isVisible: Boolean){
//        appBar?.let{
//            it.visibility = if(isVisible) View.VISIBLE else View.GONE
//        }
    }

//    override fun onBackPressed() {
//        super.onBackPressed()
//
//    }



}

