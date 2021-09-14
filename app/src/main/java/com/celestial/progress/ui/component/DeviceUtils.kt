package com.celestial.progress.ui.component

import android.content.Context
import android.graphics.Color
import android.util.DisplayMetrics


object DeviceUtils {
    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    fun convertDpToPixel(dp: Float, context: Context): Float {
        return dp * (context.getResources().getDisplayMetrics().densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    fun convertPixelsToDp(px: Float, context: Context): Float {
        return px / (context.getResources().getDisplayMetrics().densityDpi.toFloat()/ DisplayMetrics.DENSITY_DEFAULT)
    }

    /**
     * Returns darker version of specified `color`.
     */
    fun darker(color: Int, factor: Float): Int {
        val a: Int = Color.alpha(color)
        val r: Int = Color.red(color)
        val g: Int = Color.green(color)
        val b: Int = Color.blue(color)
        return Color.argb(a,
                Math.max((r * factor).toInt(), 0),
                Math.max((g * factor).toInt(), 0),
                Math.max((b * factor).toInt(), 0))
    }
}