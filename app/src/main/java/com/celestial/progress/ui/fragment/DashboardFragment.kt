package com.celestial.progress.ui.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.celestial.progress.MainActivity
import com.celestial.progress.R
import com.celestial.progress.data.adapter.ItemAdapter
import com.celestial.progress.databinding.FragmentDashboardBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

private val TAG = DashboardFragment::class.java.name

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? =null

    private val binding get() = _binding!!

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.counterRcy.adapter = ItemAdapter()
        binding.counterRcy.layoutManager = LinearLayoutManager(context)

        setListener()

        return view
    }

    fun setListener(){
        binding.textView.setOnClickListener{
            (activity as MainActivity).callback()
            Log.d(TAG, "Click on Listener")
        }

        binding.fab.setOnClickListener {
            (activity as MainActivity).showHideAppBar(false)
            findNavController().navigate(R.id.createFragment)

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                DashboardFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}