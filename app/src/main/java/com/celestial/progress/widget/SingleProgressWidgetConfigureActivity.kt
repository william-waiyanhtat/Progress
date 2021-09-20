package com.celestial.progress.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.celestial.progress.R
import com.celestial.progress.data.model.Counter
import com.celestial.progress.databinding.SingleProgressWidgetConfigureBinding
import com.celestial.progress.ui.adapter.ItemAdapter
import com.celestial.progress.widget.SingleProgressWidget.Companion.updateAppWidget
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SingleProgressWidgetConfigureActivity : AppCompatActivity() {
    val TAG = SingleProgressWidgetConfigureActivity::class.java.name

    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    private lateinit var appWidgetText: EditText

    private val widgetConfigViewModel: WidgetConfigViewModel by viewModels()

    private lateinit var rcyView: RecyclerView

    private lateinit var adapter: ItemAdapter<ItemAdapter<*>.WidgetSelectionViewHolder>

    private var selectCounter : Counter? = null

    private var onClickListener = View.OnClickListener {

        val context = this@SingleProgressWidgetConfigureActivity

        // When the button is clicked, store the string locally
     //   val widgetText = appWidgetText.text.toString()

        val widgetText = selectCounter?.id
        saveTitlePref(context, appWidgetId, widgetText!!)

        // It is the responsibility of the configuration activity to update the app widget
        val appWidgetManager = AppWidgetManager.getInstance(context)
       // updateAppWidget(context, appWidgetManager, appWidgetId, null)

        // Make sure we pass back the original appWidgetId
        val resultValue = Intent()
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        setResult(RESULT_OK, resultValue)

        finish()

        val pendIntent = SingleProgressWidget.getRefreshIntent(applicationContext)

        pendIntent.send()
    }



    private lateinit var binding: SingleProgressWidgetConfigureBinding

    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED)

        binding = SingleProgressWidgetConfigureBinding.inflate(layoutInflater)
        val view = binding?.root

        setContentView(view)

        initUI()
        observeData()

        appWidgetText = binding.appwidgetText
        binding.addButton.setOnClickListener(onClickListener)

        // Find the widget id from the intent.
        val intent = intent
        val extras = intent.extras

        if (extras != null) {
            appWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID
            )
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        appWidgetText.setText(
                loadTitlePref(
                        this@SingleProgressWidgetConfigureActivity,
                        appWidgetId
                ).toString()
        )

    }

    private fun initUI() {
        rcyView = binding.widgetConfigRcy
        adapter = ItemAdapter(t = ItemAdapter.WidgetSelectionViewHolder::class, selectCounter = selectedCounter)
        rcyView.adapter = adapter
    }

    private fun observeData(){
        widgetConfigViewModel.observeCounterLiveData().observe(this@SingleProgressWidgetConfigureActivity, Observer {
            adapter.submitList(it)
        })
    }

    val selectedCounter: (Counter) -> Unit? = {
        selectCounter = it
        Log.d(TAG, "Select Counter Name: ${it.title} , ID: ${it.id}")
        null
    }

}

private const val PREFS_NAME = "com.celestial.progress.widget.SingleProgressWidget"
private const val PREF_PREFIX_KEY = "appwidget_"

// Write the prefix to the SharedPreferences object for this widget
internal fun saveTitlePref(context: Context, appWidgetId: Int, text: Int) {
    val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
    prefs.putInt(PREF_PREFIX_KEY + appWidgetId, text)
    prefs.apply()
}

// Read the prefix from the SharedPreferences object for this widget.
// If there is no preference saved, get the default from a resource
internal fun loadTitlePref(context: Context, appWidgetId: Int): Int {
    val prefs = context.getSharedPreferences(PREFS_NAME, 0)
    val titleValue = prefs.getInt(PREF_PREFIX_KEY + appWidgetId, 0)
    return titleValue ?: 0
}

internal fun deleteTitlePref(context: Context, appWidgetId: Int) {
    val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
    prefs.remove(PREF_PREFIX_KEY + appWidgetId)
    prefs.apply()
}