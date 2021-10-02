package com.celestial.progress

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.celestial.progress.data.model.Counter
import com.celestial.progress.databinding.ActivityMainBinding
import com.celestial.progress.others.Status
import com.celestial.progress.ui.CounterViewModel
import com.celestial.progress.ui.adapter.ItemAdapter
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


val TAG = MainActivity::class.java.simpleName

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var adapter: ItemAdapter<ItemAdapter<*>.ItemViewHolder>

    private var binding: ActivityMainBinding? = null

    lateinit var callback: () -> Unit

    lateinit var appBar: ConstraintLayout

    lateinit var toolbar: Toolbar

    lateinit var viewModel: CounterViewModel

    private var mFirebaseAnalytics: FirebaseAnalytics? = null

    var insertCounter: (Counter, () -> Unit) -> Unit = { counter, func ->
        lifecycleScope.launch {
            viewModel.createCounter(counter).observe(
                    this@MainActivity,
                    androidx.lifecycle.Observer {
                        when (it.status) {
                            Status.SUCCESS -> {
                                func.invoke()
                                binding?.mainLayout?.let { it1 ->
                                    Snackbar.make(
                                            it1,
                                            it.message!!,
                                            Snackbar.LENGTH_LONG
                                    ).show()
                                }
                            }
                            Status.ERROR -> {
                                binding?.mainLayout?.let { it1 ->
                                    Snackbar.make(
                                            it1,
                                            it.message!!,
                                            Snackbar.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }

                    })
        }

    }

    var updateCounter: (Counter, () -> Unit) -> Unit = { counter, func ->
        lifecycleScope.launch {
            viewModel.updateCounter(counter).observe(this@MainActivity,
                    Observer {
                        when (it.status) {
                            Status.SUCCESS -> {
                                func.invoke()
                                binding?.mainLayout?.let { it1 ->
                                    Snackbar.make(
                                            it1,
                                            it.message!!,
                                            Snackbar.LENGTH_LONG
                                    ).show()
                                }
                            }
                            Status.ERROR -> {
                                binding?.mainLayout?.let { it1 ->
                                    Snackbar.make(
                                            it1,
                                            it.message!!,
                                            Snackbar.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }

                    })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding?.root
        toolbar = binding!!.toolbarMainactivity
        toolbar.navigationIcon = null

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        viewModel = ViewModelProvider(this)[CounterViewModel::class.java]

        // appBar = binding?.appBar!!

        setContentView(view)
        loadAd(binding!!)

        callback = {
            makeVisible()
            Log.d(TAG, "Callback method call")
        }

//        showNotification(this,"ABC","DEF")
//
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//        Utils.showNotificationOreo(100,"com.celcesital.progress.channel1","Hello there", this@MainActivity)
//        }

    }

    fun makeVisible() {
        binding.apply {
            Log.d(TAG, "MAKE VISIBLE")
            //    this?.appBar?.visibility = View.VISIBLE
//            this?.mainBtmNav?.visibility = View.VISIBLE
        }

    }


    fun showHideAppBar(isVisible: Boolean) {
    }


    fun showHideToolbar(isShown: Boolean) {
        if (isShown)
            toolbar.visibility = View.VISIBLE
        else
            toolbar.visibility = View.GONE
    }

    fun setTitle(title: String) {
        toolbar.title = title
    }


    private fun loadAd(binding: ActivityMainBinding){
      //  binding.adView.adSize = AdSize.BANNER
     //   binding.adView.adUnitId = "ca-app-pub-3940256099942544/6300978111"
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)
    }

}

