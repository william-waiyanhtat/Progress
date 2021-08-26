package com.celestial.progress.ui.components

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.graphics.Shader
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.view.animation.LinearInterpolator

class ListShimmerView : View, ValueAnimator.AnimatorUpdateListener {

    private val centerColor = Color.argb(CENTER_ALPHA, SHADER_COLOR_R, SHADER_COLOR_G, SHADER_COLOR_B)
    private val edgeColor = Color.argb(EDGE_ALPHA, SHADER_COLOR_R, SHADER_COLOR_G, SHADER_COLOR_B)

    private val vSpacing: Float = dpToPixels(context.resources.displayMetrics, V_SPACING_DP)
    private val hSpacing: Float = dpToPixels(context.resources.displayMetrics, H_SPACING_DP)
    private val lineHeight: Float = spToPixels(context.resources.displayMetrics, LINE_HEIGHT_SP)
    private val imageSize: Float = dpToPixels(context.resources.displayMetrics, IMAGE_SIZE_DP)
    private val cornerRadius: Float = dpToPixels(context.resources.displayMetrics, CORNER_RADIUS_DP)

    private var listItemPattern: Bitmap? = null

    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val shaderPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val shaderColors: IntArray = intArrayOf(edgeColor, centerColor, edgeColor)

    private val animator: ValueAnimator = ValueAnimator.ofFloat(-1f, 2f).apply {
        duration = ANIMATION_DURATION.toLong()
        interpolator = LinearInterpolator()
        repeatCount = ValueAnimator.INFINITE
        addUpdateListener(this@ListShimmerView)
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onAnimationUpdate(valueAnimator: ValueAnimator) {
        if (isAttachedToWindow) {
            val f = valueAnimator.animatedValue as Float
            updateShader(width.toFloat(), f)
            invalidate()
        } else {
            animator.cancel()
        }
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        when (visibility) {
            View.VISIBLE -> animator.start()
            View.INVISIBLE, View.GONE -> animator.cancel()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updateShader(w.toFloat(), -1f)
        if (h > 0 && w > 0) {
            preDrawItemPattern(w, h)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        listItemPattern?.recycle()
        animator.cancel()
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawColor(edgeColor)
        // draw gradient background
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), shaderPaint)
        listItemPattern?.let {
            // draw list item pattern
            canvas.drawBitmap(it, 0f, 0f, paint)
        }
    }

    private fun updateShader(w: Float, f: Float) {
        val left = w * f
        val shader = LinearGradient(left, 0f, left + w, 0f,
            shaderColors, floatArrayOf(0f, .5f, 1f), Shader.TileMode.CLAMP)
        shaderPaint.shader = shader
    }

    private fun preDrawItemPattern(w: Int, h: Int) {
        listItemPattern = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888).apply {
            // draw list items into the bitmap
            val canvas = Canvas(this)
            val item = getItemBitmap(w)
            var top = 0
            do {
                canvas.drawBitmap(item, 0f, top.toFloat(), paint)
                top += item.height
            } while (top < canvas.height)

            // only fill the rectangles with the background color
            canvas.drawColor(ITEM_PATTERN_BG_COLOR, PorterDuff.Mode.SRC_IN)
        }
    }

    private fun getItemBitmap(w: Int): Bitmap {
        val h = calculatePatternHeight(LIST_ITEM_LINES)
        // we only need Alpha value in this bitmap
        val item = Bitmap.createBitmap(w, h, Bitmap.Config.ALPHA_8)

        val canvas = Canvas(item)
        canvas.drawColor(Color.argb(255, 0, 0, 0))

        val itemPaint = Paint()
        itemPaint.isAntiAlias = true
        itemPaint.color = Color.argb(0, 0, 0, 0)
        itemPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)

        // avatar
        val rectF = RectF(vSpacing, hSpacing, vSpacing + imageSize, hSpacing + imageSize)
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, itemPaint)

        val textLeft = rectF.right + hSpacing
        val textRight = canvas.width - vSpacing

        // title line
        val titleWidth = ((textRight - textLeft) * 0.5).toFloat()
        rectF.set(textLeft, hSpacing, textLeft + titleWidth, hSpacing + lineHeight)
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, itemPaint)

        // timestamp
        val timeWidth = ((textRight - textLeft) * 0.2).toFloat()
        rectF.set(textRight - timeWidth, hSpacing, textRight, hSpacing + lineHeight)
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, itemPaint)

        // text lines
        var line = LIST_ITEM_LINES - 1
        while (line > 0) {
            val lineTop = rectF.bottom + hSpacing
            rectF.set(textLeft, lineTop, textRight, lineTop + lineHeight)
            canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, itemPaint)
            line--
        }
        return item
    }

    private fun calculatePatternHeight(lines: Int): Int {
        return (lines * lineHeight + hSpacing * (lines + 1)).toInt()
    }

    companion object {
        private const val TAG = "ListShimmerView"

        private const val V_SPACING_DP = 16
        private const val H_SPACING_DP = 16
        private const val IMAGE_SIZE_DP = 40
        private const val LINE_HEIGHT_SP = 15
        private const val CORNER_RADIUS_DP = 2
        private const val ITEM_PATTERN_BG_COLOR = Color.WHITE

        private const val CENTER_ALPHA = 50
        private const val EDGE_ALPHA = 12
        private const val SHADER_COLOR_R = 170
        private const val SHADER_COLOR_G = 170
        private const val SHADER_COLOR_B = 170

        private const val ANIMATION_DURATION = 1500

        private const val LIST_ITEM_LINES = 3

        private fun dpToPixels(metrics: DisplayMetrics, dp: Int): Float {
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), metrics)
        }

        private fun spToPixels(metrics: DisplayMetrics, sp: Int): Float {
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp.toFloat(), metrics)
        }
    }
}