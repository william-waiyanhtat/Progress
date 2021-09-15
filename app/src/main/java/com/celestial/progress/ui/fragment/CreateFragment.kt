package com.celestial.progress.ui.fragment

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.transition.ChangeBounds
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

import com.celestial.progress.MainActivity
import com.celestial.progress.R
import com.celestial.progress.data.model.Counter
import com.celestial.progress.data.model.DisplayFormat
import com.celestial.progress.databinding.FragmentCreateBinding
import com.celestial.progress.others.Status
import com.celestial.progress.others.Validator.verifyCounterName
import com.celestial.progress.others.Validator.verifyInputDateString
import com.celestial.progress.ui.CounterViewModel
import petrov.kristiyan.colorpicker.ColorPicker
import petrov.kristiyan.colorpicker.ColorPicker.OnChooseColorListener
import java.util.*


class CreateFragment : Fragment() {

    lateinit var binding: FragmentCreateBinding
    lateinit var toolbar: Toolbar
    lateinit var viewModel: CounterViewModel
    lateinit var arrayColorPicker: Array<View>

    var startDate = ""
    var endDate = ""
    var displayFormat = ""
    var note = ""

    var colorValue: Int? = null


    val TAG = CreateFragment::class.simpleName


    fun goBack() {
        NavHostFragment.findNavController(this@CreateFragment)
            .popBackStack(R.id.dashboardFragment, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                //   findNavController(this@CreateFragment).popBackStack()
                goBack()
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

        sharedElementEnterTransition = ChangeBounds()

        setUpListener()

        setUpViewModel()

        return view
    }


    private fun setUpViewModel() {
        viewModel = ViewModelProvider(requireActivity())[CounterViewModel::class.java]
    }

    private fun setUpListener() {

        binding.parentLayout.setOnClickListener {
            unfocus(binding.textInputLayout)
        }

        //backbutton
        binding.toolbarCreate.setNavigationOnClickListener {
            goBack()
        }

        binding.inputName.setOnClickListener {


        }

        arrayColorPicker = arrayOf(
            binding.presetColor1,
            binding.presetColor2,
            binding.presetColor3,
            binding.presetColor4
        )

        binding.inputName.doOnTextChanged { text, start, before, count ->
            binding.textInputLayout.error = null
        }

        binding.startDateInput.setOnClickListener {
            showDatePickerDialog(it as TextView)
        }

        binding.endDateInput.setOnClickListener {
            showDatePickerDialog(it as TextView)
        }


        //setSpinnerAdapter
        val adapter: ArrayAdapter<*> = ArrayAdapter.createFromResource(
            requireActivity(),
            R.array.show_mode, android.R.layout.simple_spinner_item
        )

        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        binding.spinnerDisplayformat.adapter = adapter


        binding.spinnerDisplayformat.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    unfocus(binding.textInputLayout)
                    displayFormat = parent?.getItemAtPosition(position).toString()
                    Log.d(TAG, parent?.getItemAtPosition(position).toString())
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    unfocus(binding.textInputLayout)
                }
            }

        binding.btnCustomColor.setOnClickListener {
            val colorPicker = ColorPicker(activity)
            colorPicker.show()
            colorPicker.setOnChooseColorListener(object : OnChooseColorListener {
                override fun onChooseColor(position: Int, color: Int) {
                    binding.btnCustomColor.setBackgroundColor(color)
                    colorValue = color
                    clearColorPickUI()
                    resetColorErrorText()
                }

                override fun onCancel() {
                    // put code
                }
            })
        }

        binding.btnCreate.setOnClickListener {
            if (!validateCounterNameInput()) {
                binding.textInputLayout.requestFocus()
                return@setOnClickListener
            }

            if (!validateDateInput()){
                binding.parentLayout.requestChildFocus(binding.startDateInput,binding.startDateInput)
                return@setOnClickListener
            }

            if(!validateColorChoice()){
                binding.tvColorError.text = getString(R.string.color_error_label)
                return@setOnClickListener
            }
            resetColorErrorText()
            createCounter()

        }
        chooseColorCompoundListener()



    }

    private fun resetColorErrorText() {
      binding.tvColorError.text = ""
    }

    private fun createCounter() {
        val counter = Counter(
            binding.inputName.text.toString(),
            startDate,
            endDate,
            !binding.countdownChkBox.isChecked,
            colorValue,
            note,
            binding.requiredNotiChkBox.isChecked,
            DisplayFormat.DAY
        )

        (requireActivity() as MainActivity).insertCounter(counter,{goBack()})

//        lifecycleScope.launch {
//            viewModel.createCounter(counter).observe(viewLifecycleOwner, androidx.lifecycle.Observer {
//                when(it.status){
//                   Status.SUCCESS ->{
//                       Snackbar.make(requireActivity().view,"Success",Snackbar.LENGTH_SHORT)
//                       goBack()
//                   }
//                    Status.ERROR ->{}
//                }
//
//            })
//        }
    }

    private fun validateDateInput(): Boolean {
        val res = verifyInputDateString(
            startDate,
            endDate
        )
        return if (res.status == Status.ERROR) {
            binding.tvDateError.text = res.message
            false
        } else {
            true
        }

    }

    private fun validateCounterNameInput(): Boolean {
        val inputRes = verifyCounterName(binding.inputName.text.toString())
        return if (inputRes.status == Status.ERROR) {
            binding.textInputLayout.error = inputRes.message
            false
        } else {
            true
        }
    }

    private fun validateColorChoice(): Boolean{
        return colorValue != null
    }

    companion object {
        @JvmStatic
        fun newInstance() = CreateFragment()
    }


    fun showDatePickerDialog(v: TextView) {
        binding.inputName.clearFocus()

        v.isSelected = true
        val calendar = Calendar.getInstance()

        val datePickerDialog: DatePickerDialog = DatePickerDialog(
            requireActivity(),
            DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                val mONTH = month + 1

                if (v.id == R.id.startDateInput){
                    startDate = "$year-$mONTH-$dayOfMonth"
                    v.text = "Start Date: $year-$mONTH-$dayOfMonth"
                }

                else{
                    endDate = "$year-$mONTH-$dayOfMonth"
                    v.text = "End Date: $year-$mONTH-$dayOfMonth"
                }

                v.isSelected = false
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            setButton(
                Dialog.BUTTON_NEGATIVE,
                getString(R.string.cancel_label),
                DialogInterface.OnClickListener { dialog, which ->
                    when (which) {
                        DialogInterface.BUTTON_NEGATIVE -> {
                            Log.d(TAG, "Cancel Press")
                            v.isSelected = false
                        }
                    }

                })
        }

        datePickerDialog.show()
    }

    private fun chooseColorCompoundListener() {
        for (a in arrayColorPicker) {
            a.setOnClickListener(colorClickListener)
        }

    }

    private val colorClickListener = View.OnClickListener {
        clearColorPickUI()
        clearColorPickUI()
        colorValue = (it.background as ColorDrawable).color
        Log.d(TAG,"Color : ${colorValue}")
        it.foreground = requireActivity().getDrawable(R.drawable.ic_check)
    }

    private fun clearColorPickUI(){
        for (a in arrayColorPicker) {
            a.foreground = null
        }
    }
}



fun Fragment.unfocus(v: View) {
    v.clearFocus()
}
