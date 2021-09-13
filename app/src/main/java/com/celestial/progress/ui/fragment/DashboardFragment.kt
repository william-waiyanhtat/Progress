package com.celestial.progress.ui.fragment

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.celestial.progress.MainActivity
import com.celestial.progress.R
import com.celestial.progress.data.adapter.ItemAdapter
import com.celestial.progress.data.model.Counter
import com.celestial.progress.databinding.FragmentDashboardBinding
import com.celestial.progress.ui.CounterViewModel


private val TAG = DashboardFragment::class.java.name

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? =null

    private val binding get() = _binding!!

    lateinit var viewModel: CounterViewModel

    lateinit var adapter: ItemAdapter

    var rcyState: Parcelable? = null

    var position = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val view = binding.root


        viewModel = ViewModelProvider(requireActivity())[CounterViewModel::class.java]
        adapter = ItemAdapter()
        binding.counterRcy.adapter = adapter
        binding.counterRcy.layoutManager = LinearLayoutManager(context)

        setListener()
        observeData()
        Log.d(TAG, "On Create View")

        return view
    }

    private fun observeData() {
      viewModel?.let {
          it.readAllCounters().observe(viewLifecycleOwner, Observer {
              Log.d(TAG,"Data get ${it.size}")

              adapter.submitList(it)
              binding.counterRcy.layoutManager?.onRestoreInstanceState(rcyState)
          })

      }
    }

    fun setListener(){
        binding.textView.setOnClickListener{
            (activity as MainActivity).callback()
            Log.d(TAG, "Click on Listener")
        }

        binding.fab.setOnClickListener {
            (activity as MainActivity).showHideAppBar(false)

            if (findNavController().currentDestination?.id == R.id.dashboardFragment) {
                val extras = FragmentNavigator.Extras.Builder()
                    .addSharedElement(
                        binding.fab,"fabBtn"
                    ).build()

               findNavController().navigate(R.id.navigateToCreateFragment,null,null,extras)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
    companion object {
        @JvmStatic
        fun newInstance() =
                DashboardFragment()
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "On Pause")
        rcyState = binding.counterRcy.layoutManager?.onSaveInstanceState()
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "On Resume")
       // binding.counterRcy.layoutManager?.onRestoreInstanceState(rcyState)
    }
}