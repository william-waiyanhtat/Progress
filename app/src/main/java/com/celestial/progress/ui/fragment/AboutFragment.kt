package com.celestial.progress.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.celestial.progress.BuildConfig
import com.celestial.progress.databinding.FragmentAboutBinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class AboutFragment : Fragment() {

    private var _binding: FragmentAboutBinding? = null

    private val binding get()= _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentAboutBinding.inflate(inflater, container, false)
        val  view = binding.root

        setValue(binding)

        return view
    }

    private fun setValue(binding: FragmentAboutBinding) {
        binding.tvAppName.text = getApplicationName(requireContext())
        binding.tvAppVer.text = BuildConfig.VERSION_NAME
    }
    fun getApplicationName(context: Context): String? {
        return context.applicationInfo.loadLabel(context.packageManager).toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() =
                AboutFragment()
    }
}