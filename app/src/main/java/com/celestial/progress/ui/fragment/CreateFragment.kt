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
import co.mobiwise.materialintro.MaterialIntroConfiguration
import co.mobiwise.materialintro.shape.Focus
import co.mobiwise.materialintro.shape.FocusGravity
import co.mobiwise.materialintro.shape.ShapeType
import co.mobiwise.materialintro.view.MaterialIntroView

import com.celestial.progress.MainActivity
import com.celestial.progress.R
import com.celestial.progress.data.model.Counter
import com.celestial.progress.data.model.DisplayFormat
import com.celestial.progress.data.model.DisplayFormatTC
import com.celestial.progress.databinding.FragmentCreateBinding
import com.celestial.progress.others.SharePrefHelper
import com.celestial.progress.others.Status
import com.celestial.progress.others.Utils
import com.celestial.progress.others.Validator.verifyCounterName
import com.celestial.progress.others.Validator.verifyInputDateString
import com.celestial.progress.ui.CounterViewModel
import com.elconfidencial.bubbleshowcase.BubbleShowCaseBuilder
import petrov.kristiyan.colorpicker.ColorPicker
import petrov.kristiyan.colorpicker.ColorPicker.OnChooseColorListener
import smartdevelop.ir.eram.showcaseviewlib.GuideView
import smartdevelop.ir.eram.showcaseviewlib.config.Gravity
import smartdevelop.ir.eram.showcaseviewlib.config.PointerType
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

    private var datePickerDialog: DatePickerDialog? = null

    private var dialogOkBtn: View? = null

    private val TAG = CreateFragment::class.simpleName


    private lateinit var startDateChooseGuide: MaterialIntroView.Builder
    private lateinit var config: MaterialIntroConfiguration

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

        if(!SharePrefHelper.isGuideShown(requireContext())){
            initializeGuide()
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
                    Log.d(TAG,"Color: $color")
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
                if(!SharePrefHelper.isGuideShown(requireContext())){
                    SharePrefHelper.setGuideShown(requireContext())
                }

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
        return colorValue != null && colorValue != 0
    }

    companion object {
        @JvmStatic
        fun newInstance() = CreateFragment()
    }


    fun showDatePickerDialog(v: TextView) {
        binding.inputName.clearFocus()

        v.isSelected = true
        val calendar = Calendar.getInstance()

         datePickerDialog = DatePickerDialog(
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

        datePickerDialog!!.show()

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


    private fun createAndShowGuide(){

        val displayFormatGuide = GuideView.Builder(requireContext())
            .setTitle("Tap Here")
            .setContentText("To set the end date")
            .setTargetView(binding.endDateInput)
            .setGravity(Gravity.center)
            .setPointerType(PointerType.none)
            .setGuideListener {


            }
            .build()

        val endDateInputGuide = GuideView.Builder(requireContext())
            .setTitle("Tap Here")
            .setContentText("To set the end date")
            .setTargetView(binding.endDateInput)
            .setGravity(Gravity.center)
            .setPointerType(PointerType.none)
            .setGuideListener {
                displayFormatGuide.show()

            }
            .build()

        val endDateChkBoxGuide =  GuideView.Builder(requireContext())
            .setTitle("Tap Here")
            .setContentText("To make the period tracking progress")
            .setTargetView(binding.countdownChkBox)
            .setGravity(Gravity.center)
            .setPointerType(PointerType.none)
            .setGuideListener {
                endDateInputGuide.show()

            }
            .build()
        val startDateGuide = GuideView.Builder(requireContext())
            .setTitle("Tap Here")
            .setContentText("To set the start date")
            .setTargetView(binding.startDateInput)
            .setGravity(Gravity.center)
            .setPointerType(PointerType.none)
            .setGuideListener {
                    endDateChkBoxGuide.show()

            }
            .build()



        GuideView.Builder(requireContext())
            .setTitle("Tap Here")
            .setContentText("To enter progress name")
            .setTargetView(binding.inputName)
            .setPointerType(PointerType.none)
            .setGravity(Gravity.center)
            .setGuideListener {
                startDateGuide.show()

            }
            .build()
            .show()


    }

    private fun showGuide() {
        val createButtonTarget = binding.toolbarCreate.findViewById<View>(R.id.toolbar_menu_create)

        startDateChooseGuide = MaterialIntroView.Builder(requireActivity())
            .performClick(true)
            .setInfoText("Tap this date input to set the Today Date")
            .setShape(ShapeType.RECTANGLE)
            .setTarget(binding.root)
            .setUsageId(System.currentTimeMillis().toString())
            .setTargetPadding(-100)
            .setConfiguration(config)
            .enableIcon(false)
            .setListener {
            }


        val createGuide = MaterialIntroView.Builder(requireActivity())
            .performClick(true)
            .setInfoText("Press this button to create progress")
            .setShape(ShapeType.RECTANGLE)
            .setTarget(createButtonTarget)
            .setUsageId(System.currentTimeMillis().toString())
            .setConfiguration(config)
            .enableIcon(false)
            .setListener {
                //startDate = "$year-$mONTH-$dayOfMonth"
                //  binding.endDateInput.text = "ABC"

            }



        val noteGuide = MaterialIntroView.Builder(requireActivity())
            .performClick(true)
            .setInfoText("Tap this input to enter the note for your progress, this time we will enter for you.")
            .setShape(ShapeType.RECTANGLE)
            .setTarget(binding.noteInput)
            .setUsageId(System.currentTimeMillis().toString())
            .setConfiguration(config)
            .enableIcon(false)
            .setListener {
                binding.noteInput.setText("Make new year party with friends")
                createGuide.show()

            }




        val notificationGuide = MaterialIntroView.Builder(requireActivity())
            .performClick(true)
            .setInfoText("Check this box to create the notification")
            .setShape(ShapeType.RECTANGLE)
            .setTarget(binding.requiredNotiChkBox)
            .setUsageId(System.currentTimeMillis().toString())
            .setConfiguration(config)
            .enableIcon(false)
            .setListener {
                noteGuide.show()
            }


        val colorChooseGuide = MaterialIntroView.Builder(requireActivity())
            .performClick(true)
            .setInfoText("Tap this color to set the Progress bar color")
            .setShape(ShapeType.RECTANGLE)
            .setTarget(binding.presetColor1)
            .setUsageId(System.currentTimeMillis().toString())
            .setConfiguration(config)
            .enableIcon(false)
            .setListener {
                notificationGuide.show()

            }

        val dayFormatGuide = MaterialIntroView.Builder(requireActivity())
            .performClick(false)
            .setInfoText("Tap this dropdown to choose the display format, this time we will choose Month-Week-Day format for you")
            .setShape(ShapeType.RECTANGLE)
            .setTarget(binding.spinnerDisplayformat)
            .setUsageId(System.currentTimeMillis().toString())
            .setConfiguration(config)
            .enableIcon(false)
            .setListener {
                //startDate = "$year-$mONTH-$dayOfMonth"
                //  binding.endDateInput.text = "ABC"
                binding.spinnerDisplayformat.setSelection(2)
                displayFormat = DisplayFormatTC().getValue(2)
                colorChooseGuide.show()
            }




        val endDateInputGuide =  MaterialIntroView.Builder(requireActivity())
            .performClick(false)
            .setInfoText("Tap this checkbox to make the Countdown Progress")
            .setShape(ShapeType.RECTANGLE)
            .setTarget(binding.endDateInput)
            .setUsageId(System.currentTimeMillis().toString())
            .setConfiguration(config)
            .enableIcon(false)
            .setListener {
                setEndDateText(binding.endDateInput,Utils.getEndDateStringForTutorial())
                dayFormatGuide.show()
            }



        val checkBoxGuide = MaterialIntroView.Builder(requireActivity())
            .performClick(true)
            .setInfoText("Tap this checkbox to make the Countdown Progress")
            .setShape(ShapeType.RECTANGLE)
            .setTarget(binding.countdownChkBox)
            .setUsageId(System.currentTimeMillis().toString())
            .setConfiguration(config)
            .enableIcon(false)
            .setListener {
                endDateInputGuide.show()

            }


        val startDateInput = MaterialIntroView.Builder(requireActivity())
            .performClick(false)
            .setInfoText("Tap this date input to set the start date, this time we will set today date as Start Date")
            .setShape(ShapeType.RECTANGLE)
            .setTarget(binding.startDateInput)
            .setUsageId(System.currentTimeMillis().toString())
            .setConfiguration(config)
            .enableIcon(false)
            .setListener {
                //startDate = "$year-$mONTH-$dayOfMonth"
                setStartDateText(binding.startDateInput, Utils.getStartDateStringForTutorial())
                checkBoxGuide.show()

            }


        MaterialIntroView.Builder(requireActivity())
            .performClick(true)
            .setInfoText("Hi There! \nTap this input to enter the name for new year countdown, this time we will enter the name for you.")
            .setShape(ShapeType.RECTANGLE)
            .setTarget(binding.inputName)
            .setUsageId(System.currentTimeMillis().toString())
            .setConfiguration(config)
            .enableIcon(false)
            .setListener {
                binding.inputName.setText("New Year Countdown")
                startDateInput.show()
            }
            .show()
    }

    private fun initializeGuide(){
         config = MaterialIntroConfiguration()
        config.delayMillis = 0
        config.isFadeAnimationEnabled = true
        config.apply {
            focusGravity = FocusGravity.CENTER
            focusType = Focus.MINIMUM
        }


        showGuide()
    }

    private fun setStartDateText(v:TextView,dateString: String){
        v.text = "Start Date: $dateString"
        startDate = dateString
    }

    private fun setEndDateText(v:TextView,dateString: String){
        v.text = "End Date: $dateString"
        endDate = dateString
    }

}

