package com.celestial.progress.ui.fragment

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.celestial.progress.R
import com.celestial.progress.data.model.Counter
import com.celestial.progress.databinding.FragmentCreateBinding
import com.celestial.progress.others.Status
import com.celestial.progress.others.Validator.verifyCounterName
import com.celestial.progress.others.Validator.verifyInputDateString
import com.celestial.progress.ui.CounterViewModel
import petrov.kristiyan.colorpicker.ColorPicker
import petrov.kristiyan.colorpicker.ColorPicker.OnChooseColorListener
import java.util.*



class CreateFragment : Fragment(){

    lateinit var binding: FragmentCreateBinding
    lateinit var toolbar: Toolbar
    lateinit var viewModel: CounterViewModel
    lateinit var arrayColorPicker: Array<View>
    val TAG = CreateFragment::class.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
             //   findNavController(this@CreateFragment).popBackStack()
                NavHostFragment.findNavController(this@CreateFragment).popBackStack(R.id.dashboardFragment,false)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

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
        viewModel.createCounter(
            Counter(
                "abc",
                "ss",
                "ss",
                false,
                Color.RED,
                "some"
            )
        )
    }

    private fun setUpListener() {
        arrayColorPicker = arrayOf(binding.presetColor1,binding.presetColor2,binding.presetColor3,binding.presetColor4)

        binding.inputName.doOnTextChanged { text, start, before, count ->
            binding.textInputLayout.error = null
        }

        binding.startDateInput.setOnClickListener {
            showDatePickerDialog(it as TextView)
        }

        binding.endDateInput.setOnClickListener {
            showDatePickerDialog(it as TextView)
        }

        binding.dateInputEdt.setOnClickListener {
            showDatePickerDialog(it)

        }


        //setSpinnerAdapter
        val adapter: ArrayAdapter<*> = ArrayAdapter.createFromResource(
            requireActivity(),
            R.array.show_mode, android.R.layout.simple_spinner_item
        )

        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        binding.spinnerDisplayformat.adapter = adapter


        binding.spinnerDisplayformat.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Log.d(TAG, parent?.getItemAtPosition(position).toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        binding.btnCustomColor.setOnClickListener {
            val colorPicker = ColorPicker(activity)
            colorPicker.show()
            colorPicker.setOnChooseColorListener(object : OnChooseColorListener {
                override fun onChooseColor(position: Int, color: Int) {
                    binding.btnCustomColor.setBackgroundColor(color)
                }

                override fun onCancel() {
                    // put code
                }
            })
        }

        binding.btnCreate.setOnClickListener {
            if(!validateCounterNameInput()) return@setOnClickListener

            if(!validateDateInput()) return@setOnClickListener

        }

        chooseColorCompoundListener()

    }

    private fun validateDateInput(): Boolean {
        val res = verifyInputDateString(
            binding.startDateInput.text.toString(),
            binding.endDateInput.text.toString()
        )
        return if(res.status == Status.ERROR){
            binding.tvDateError.text = res.message
            false
        }else{
            true
        }

    }

    private fun validateCounterNameInput(): Boolean {
        val inputRes = verifyCounterName(binding.inputName.text.toString())
        return if(inputRes.status == Status.ERROR){
            binding.textInputLayout.error = inputRes.message
            false
        }else{
            true
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = CreateFragment()
    }


    fun showDatePickerDialog(v: View) {
        binding.textInputLayout.clearFocus()
        binding.inputName.clearFocus()

        v.isSelected = true
        val calendar = Calendar.getInstance()

        val datePickerDialog: DatePickerDialog = DatePickerDialog(
            requireActivity(),
            DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                //v.text = "$year-$month-$dayOfMonth"
                v.isSelected = false
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.show()
    }

    fun chooseColorCompoundListener(){

        for(a in arrayColorPicker){
            a.setOnClickListener(colorClickListener)
        }

    }

    private val colorClickListener = View.OnClickListener {
        for(a in arrayColorPicker){
            a.foreground = null
        }


        it.foreground = requireActivity().getDrawable(R.drawable.ic_check)
    }



}