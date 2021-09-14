package com.celestial.progress.ui.component

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View


const val TAG = "BigProgressBar"

class BigProgressBar : View, ValueAnimator.AnimatorUpdateListener {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    var color1: Int

    var color2: Int

    var color3: Int

    var color4: Int

    var paint = Paint()

    var paint2 = Paint()

    var progress = 100
    set(value) {
        indeterminate = false
        field = value
    }

    var paintStrokeWidth = 5f

    var indeterminate = false


    var WIDTH = 0
    var HEIGHT = 0

    var MIN: Int? = 0
    var MAX: Int? = 100

    var animatedValue = 0f

    var valueAnimator = ValueAnimator.ofFloat(0f, 0f)

    init {

        this.color1 = Color.RED
        this.color2 = DeviceUtils.darker(this.color1, 0.6f)
        this.color3 = Color.WHITE
        this.color4 = Color.TRANSPARENT

        //this.color2 = Color.BLUE


        MIN = 0
        MAX = 100

        //setup paint
        paint.isAntiAlias = true
        paint.strokeJoin = Paint.Join.ROUND
        paint.strokeCap = Paint.Cap.ROUND


    }

    override fun getPaddingStart(): Int {
        return super.getPaddingStart()
    }

    override fun getPaddingTop(): Int {
        return super.getPaddingTop()
    }

    override fun onDraw(canvas: Canvas?) {
        paint.color = color1
        //   canvas?.drawRect(0f, 0f, WIDTH.toFloat(), 20f, paint)

        paint.shader = createGradient()
        //   canvas?.drawRect(0f, 0f, WIDTH / 2.toFloat(), 20f, paint)
        Log.i("ProgressBar:", "WIDTH:${WIDTH}, WIDTH/2:${WIDTH / 2}")
        paint.strokeWidth = paintStrokeWidth

        val r = RectF(paintStrokeWidth / 2, paintStrokeWidth / 2, WIDTH.toFloat() - paintStrokeWidth / 2, HEIGHT.toFloat() - paintStrokeWidth / 2)
        val p = WIDTH - (WIDTH * progress / MAX!!)
        paint.style = Paint.Style.STROKE
        canvas?.drawRoundRect(r, 30f, 30f, paint)

        paint.shader = createGradient()

        paint.style = Paint.Style.FILL

        if (WIDTH.toFloat() - paintStrokeWidth - p >= 30f / 2) {
            val q = RectF(paintStrokeWidth, paintStrokeWidth, WIDTH.toFloat() - paintStrokeWidth - p, HEIGHT.toFloat() - paintStrokeWidth)
            canvas?.drawRoundRect(q, 25f, 25f, paint)

        }

        val w = WIDTH.toFloat() - paintStrokeWidth - p

        paint2.shader = animated3Gradient()
        val q = RectF(paintStrokeWidth, paintStrokeWidth, w, HEIGHT.toFloat() - paintStrokeWidth)
        canvas?.drawRoundRect(q, 25f, 25f, paint2)
        //shaded animated glare rectangle


    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        WIDTH = w
        HEIGHT = h

        if(valueAnimator.isRunning){
            valueAnimator.cancel()
        }

        if(indeterminate) {

            valueAnimator = ValueAnimator.ofFloat(0f, DeviceUtils.convertDpToPixel(WIDTH.toFloat() * 2, context))
            valueAnimator.apply {
                this.repeatMode = ValueAnimator.RESTART
                this.repeatCount = ValueAnimator.INFINITE
                this.duration = 2000
                this.addUpdateListener(this@BigProgressBar)
                this.start()
            }
        }
        Log.i(TAG, "Width: $w, Height $h")
    }

    override fun onAnimationUpdate(animation: ValueAnimator?) {
         animatedValue = animation?.animatedValue as Float
          invalidate()
    }

    fun getPercent(): Int {
        return WIDTH / 2
    }

    private fun createGradient(): Shader {
       // return LinearGradient(0f, 0f, WIDTH.toFloat(), 0f, color3, color2, Shader.TileMode.CLAMP)
        return LinearGradient(0f, 0f, 0f, HEIGHT.toFloat(), color1, color2, Shader.TileMode.CLAMP)
    }

    private fun animatedGradient(): Shader {
        return LinearGradient(0f, 0f, animatedValue, 0f, color4, color3, Shader.TileMode.CLAMP)
    }

    private fun animated3Gradient(): Shader{
        val co = intArrayOf(Color.RED, Color.YELLOW, Color.GREEN, Color.YELLOW, Color.RED)
        val coP = floatArrayOf(0.1f, 0.1f, 0.6f, 0.1f, 0.1f)
        val intArray: IntArray = intArrayOf(color4, color3, color4)
        val floatArray: FloatArray = floatArrayOf(0.0f, 0.5f, 0.8f)
        return LinearGradient(0f, 0f, animatedValue*2, 0f, intArray, floatArray, Shader.TileMode.CLAMP)
    }

}