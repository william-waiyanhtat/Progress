package com.celestial.progress.ui.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.celestial.progress.databinding.FragmentCreateBinding
import java.util.*


class CreateFragment : Fragment() {

    lateinit var binding: FragmentCreateBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentCreateBinding.inflate(inflater, container, false)
        val view = binding.root

        setUpListener()

        return view
    }

    private fun setUpListener() {
        binding.startDateLayout.setOnClickListener {
            showDatePickerDialog(it as TextView)
        }

        binding.endDateLayout.setOnClickListener {
            showDatePickerDialog(it as TextView)
        }

    }

    companion object {
        @JvmStatic
        fun newInstance() = CreateFragment()
    }


    fun showDatePickerDialog(v: TextView) {
        v.isSelected = true
        val calendar = Calendar.getInstance()

        val datePickerDialog: DatePickerDialog = DatePickerDialog(
                requireActivity(), DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            v.text = "$year-$month-$dayOfMonth"
            v.isSelected = false
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

        datePickerDialog.show()
    }


}