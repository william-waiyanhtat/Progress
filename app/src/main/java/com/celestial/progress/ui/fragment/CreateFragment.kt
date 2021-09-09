package com.celestial.progress.ui.fragment

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.celestial.progress.data.model.Counter
import com.celestial.progress.databinding.FragmentCreateBinding
import com.celestial.progress.ui.CounterViewModel
import java.util.*


class CreateFragment : Fragment() {

    lateinit var binding: FragmentCreateBinding
    lateinit var toolbar: Toolbar
    lateinit var viewModel: CounterViewModel


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

        toolbar = binding.toolbarCreate

        setUpListener()

        setUpViewModel()

        return view
    }

    private fun setUpViewModel() {
        viewModel  = ViewModelProvider(requireActivity())[CounterViewModel::class.java]
        viewModel.createCounter(Counter(
                "abc",
                "ss",
                "ss",
        false,
        Color.RED,
        "some")
        )
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