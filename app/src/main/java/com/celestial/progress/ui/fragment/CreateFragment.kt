package com.celestial.progress.ui.fragment

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.transition.ChangeBounds
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
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
import com.celestial.progress.data.model.DisplayFormatTC
import com.celestial.progress.databinding.FragmentCreateBinding
import com.celestial.progress.others.Status
import com.celestial.progress.others.Validator.verifyCounterName
import com.celestial.progress.others.Validator.verifyInputDateString
import com.celestial.progress.ui.CounterViewModel
import petrov.kristiyan.colorpicker.ColorPicker
import petrov.kristiyan.colorpicker.ColorPicker.OnChooseColorListener
import java.util.*


class CreateFragment : Fragment() {

    private var _binding: FragmentCreateBinding? = null
    private lateinit var toolbar: Toolbar
    private lateinit var viewModel: CounterViewModel
    private lateinit var arrayColorPicker: Array<View>

    private val binding get() = _binding!!

    private var isCreate: Boolean = false


    private var startDate = ""
    private var endDate = ""
    private var displayFormat: DisplayFormat = DisplayFormat.DAY
    private var note = ""

    private var colorValue: Int? = null


    private val TAG = CreateFragment::class.simpleName


    fun goBack() {
        NavHostFragment.findNavController(this@CreateFragment)
                .popBackStack(R.id.dashboardFragment, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            isCreate = it.getBoolean("isCreate", true)
        }

        Log.d(TAG, "isCreate : $isCreate")

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

        _binding = FragmentCreateBinding.inflate(inflater, container, false)
        val view = binding.root
        setUpViewModel()
        toolbar = binding.toolbarCreate

        toolbar.inflateMenu(R.menu.create_menu)

        if (isCreate) {
            toolbar.menu.getItem(1).isVisible = false
        } else {
            toolbar.menu.getItem(0).isVisible = false
        }

        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.toolbar_menu_create -> createButtonClickEvent()
                R.id.toolbar_menu_update -> updateCounterClickEvent()
            }
            return@setOnMenuItemClickListener true
        }

        setUpListener()
        if (!isCreate) {
            toolbar.title = "Edit Counter"
            populateCounterData(binding)
        }

        sharedElementEnterTransition = ChangeBounds()

        return view
    }

    private fun populateCounterData(binding: FragmentCreateBinding) {
        viewModel.editCounter?.let {
            binding.inputName.setText(it.title)
            binding.startDateInput.text = "Start Date: ${it.startDate}"
            startDate = it.startDate
            if (it.endDate!!.isNotEmpty()) {
                binding.countdownChkBox.isChecked = true
                binding.endDateInput.visibility = View.VISIBLE
                binding.endDateInput.text = "End Date: ${it.endDate}"
                endDate = it.endDate
            }

            binding.spinnerDisplayformat.setSelection(
                    DisplayFormatTC().getIndex(it.displayFormat),
                    true
            )
            displayFormat = it.displayFormat

            setCounterColorForEdit(it)

            binding.requiredNotiChkBox.isChecked = it.requiredNotification

            it.note?.let { it ->
                binding.noteInput.setText(it)
                note = it
            }


        }

    }


    private fun setUpViewModel() {
        viewModel = ViewModelProvider(requireActivity())[CounterViewModel::class.java]
    }

    private fun setUpListener() {

        binding.parentLayout.setOnClickListener {
            unfocus(binding.textInputLayout, binding.root)
        }

        //backbutton
        binding.toolbarCreate.setNavigationOnClickListener {
            goBack()
        }

        binding.inputName.setOnClickListener {

        }

        binding.countdownChkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            hideInputMethod()
            if (isChecked) {
                binding.endDateInput.visibility = View.VISIBLE
                binding.tvEnddateErr.visibility = View.VISIBLE
            } else {
                endDate = ""
                binding.endDateInput.visibility = View.GONE
                binding.tvEnddateErr.visibility = View.GONE
            }
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
            unfocus(binding.textInputLayout, binding.root)
            hideInputMethod()
            showDatePickerDialog(it as TextView)
        }

        binding.endDateInput.setOnClickListener {
            unfocus(binding.textInputLayout, binding.root)
            hideInputMethod()
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

                        hideInputMethod()
                        unfocus(binding.textInputLayout, binding.root)
                        displayFormat = DisplayFormatTC().getValue(position)
                        Log.d(TAG, parent?.getItemAtPosition(position).toString())
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        hideInputMethod()
                        unfocus(binding.textInputLayout, binding.root)
                    }
                }

        binding.btnCustomColor.setOnClickListener {
            val colorPicker = ColorPicker(activity)
            colorPicker.show()
            unfocus(binding.textInputLayout, binding.root)
            colorPicker.setOnChooseColorListener(object : OnChooseColorListener {
                override fun onChooseColor(position: Int, color: Int) {
                    hideInputMethod()
                    binding.btnCustomColor.setBackgroundColor(color)
                    colorValue = color
                    clearColorPickUI()
                    resetColorErrorText()
                }

                override fun onCancel() {
                    hideInputMethod()
                    // put code
                }
            })
        }

//        binding.btnCreate.setOnClickListener {
//
//
//
//
//            if (!validateCounterNameInput()) {
//                binding.textInputLayout.requestFocus()
//                return@setOnClickListener
//            }
//
//            if (!validateDateInput()) {
//                binding.parentLayout.requestChildFocus(
//                        binding.startDateInput,
//                        binding.startDateInput
//                )
//                return@setOnClickListener
//            }
//
//            if (!validateColorChoice()) {
//                binding.tvColorError.text = getString(R.string.color_error_label)
//                return@setOnClickListener
//            } else if (validateColorChoice()) {
//                binding.tvColorError.text = ""
//                return@setOnClickListener
//            }
//            note = binding.noteInput.text.toString()
//
//            resetColorErrorText()
//            createCounter()
//
//        }
        chooseColorCompoundListener()


    }

    private fun resetColorErrorText() {
        binding.tvColorError.text = ""
    }

    private fun createCounter() {
        var counter = Counter(
                binding.inputName.text.toString(),
                startDate,
                endDate,
                colorValue,
                note,
                binding.requiredNotiChkBox.isChecked,
                displayFormat
        )
        if (isCreate) {
            (requireActivity() as MainActivity).insertCounter(counter, { goBack() })
        } else {

            var updateCounter = counter
            updateCounter.id = viewModel.editCounter?.id

            (requireActivity() as MainActivity).updateCounter(updateCounter, { goBack() })
        }
    }

    private fun validateDateInput(): Boolean {

        binding.tvDateError.text = ""
        binding.tvEnddateErr.text = ""
        val res = verifyInputDateString(
                startDate,
                endDate, binding.countdownChkBox.isChecked
        )
        return if (res.status == Status.ERROR && res.data == 0) {
            binding.tvDateError.text = res.message
            false
        } else if (res.status == Status.ERROR && res.data == -1) {
            binding.tvEnddateErr.text = res.message
            false
        } else {
            binding.tvDateError.text = ""
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

    private fun validateColorChoice(): Boolean {
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

                    if (v.id == R.id.startDateInput) {
                        startDate = "$year-$mONTH-$dayOfMonth"
                        v.text = "Start Date: $year-$mONTH-$dayOfMonth"
                    } else {
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
        Log.d(TAG, "Color : ${colorValue}")
        it.foreground = requireActivity().getDrawable(R.drawable.ic_check)
    }

    private fun clearColorPickUI() {
        for (a in arrayColorPicker) {
            a.foreground = null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setCounterColorForEdit(model: Counter) {
        colorValue = model.color

        for (i in arrayColorPicker) {
            val colVal = (i.background as ColorDrawable).color

            if (colVal == model.color) {
                i.foreground = requireActivity().getDrawable(R.drawable.ic_check)
                return
            }
        }

        binding.btnCustomColor.setBackgroundColor(model.color!!)
    }

    private fun createButtonClickEvent() {
        binding.tvColorError.text = ""
        hideInputMethod()
        if (!validateCounterNameInput()) {
            binding.textInputLayout.requestFocus()
            return
        }

        if (!validateDateInput()) {
            binding.parentLayout.requestChildFocus(
                    binding.startDateInput,
                    binding.startDateInput
            )
            return
        }

        if (!validateColorChoice()) {
            binding.tvColorError.text = getString(R.string.color_error_label)
            return
        }
        note = binding.noteInput.text.toString()

        resetColorErrorText()
        createCounter()
    }


    private fun hideInputMethod() {
        try {
            val imm: InputMethodManager? = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm?.hideSoftInputFromWindow(binding.root.windowToken, 0)
        } catch (e: Exception) {

        }
    }


    fun Fragment.unfocus(v: View, mainVew: View) {
        Log.d("CreateFragment", "Unfocus Called")
        v.clearFocus()
        binding.noteInput.clearFocus()
        mainVew.requestFocus()
    }

    fun updateCounterClickEvent() {
        createButtonClickEvent()
    }


}

