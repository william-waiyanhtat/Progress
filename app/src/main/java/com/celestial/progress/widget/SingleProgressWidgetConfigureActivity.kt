package com.celestial.progress.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.celestial.progress.R
import com.celestial.progress.databinding.ActivityMainBinding
import com.celestial.progress.databinding.SingleProgressWidgetConfigureBinding
import com.celestial.progress.ui.adapter.ItemAdapter
import dagger.hilt.android.AndroidEntryPoint

/**
 * The configuration screen for the [SingleProgressWidget] AppWidget.
 */
@AndroidEntryPoint
class SingleProgressWidgetConfigureActivity : AppCompatActivity() {
    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    private lateinit var appWidgetText: EditText

    private val widgetConfigViewModel: WidgetConfigViewModel by viewModels()

    private lateinit var rcyView: RecyclerView

    private lateinit var adapter: ItemAdapter<ItemAdapter<*>.WidgetSelectionViewHolder>

    private var onClickListener = View.OnClickListener {
        val context = this@SingleProgressWidgetConfigureActivity

        // When the button is clicked, store the string locally
        val widgetText = appWidgetText.text.toString()
        saveTitlePref(context, appWidgetId, widgetText)

        // It is the responsibility of the configuration activity to update the app widget
        val appWidgetManager = AppWidgetManager.getInstance(context)
        updateAppWidget(context, appWidgetManager, appWidgetId)

        // Make sure we pass back the original appWidgetId
        val resultValue = Intent()
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        setResult(RESULT_OK, resultValue)
        finish()
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

        appWidgetText = findViewById<View>(R.id.appwidget_text) as EditText
        findViewById<View>(R.id.add_button).setOnClickListener(onClickListener)

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
                )
        )
    }

    private fun initUI() {
        rcyView = binding.widgetConfigRcy
        adapter = ItemAdapter(t = ItemAdapter.WidgetSelectionViewHolder::class)
        rcyView.adapter = adapter
    }

    private fun observeData(){
        widgetConfigViewModel.observeCounterLiveData().observe(this@SingleProgressWidgetConfigureActivity, Observer {
            adapter.submitList(it)
        })
    }

}

private const val PREFS_NAME = "com.celestial.progress.widget.SingleProgressWidget"
private const val PREF_PREFIX_KEY = "appwidget_"

// Write the prefix to the SharedPreferences object for this widget
internal fun saveTitlePref(context: Context, appWidgetId: Int, text: String) {
    val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
    prefs.putString(PREF_PREFIX_KEY + appWidgetId, text)
    prefs.apply()
}

// Read the prefix from the SharedPreferences object for this widget.
// If there is no preference saved, get the default from a resource
internal fun loadTitlePref(context: Context, appWidgetId: Int): String {
    val prefs = context.getSharedPreferences(PREFS_NAME, 0)
    val titleValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null)
    return titleValue ?: context.getString(R.string.appwidget_text)
}

internal fun deleteTitlePref(context: Context, appWidgetId: Int) {
    val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
    prefs.remove(PREF_PREFIX_KEY + appWidgetId)
    prefs.apply()
}