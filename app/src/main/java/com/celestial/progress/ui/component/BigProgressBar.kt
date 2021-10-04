package com.celestial.progress.ui.component

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.celestial.progress.R


const val TAG = "BigProgressBar"

class BigProgressBar : View, ValueAnimator.AnimatorUpdateListener {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    var color1: Int
    set(value) {
        field = value
        color2 = DeviceUtils.darker(value, 0.6f)
    }

    var color2: Int

    var color3: Int

    var color4: Int

    var paint = Paint()

    var paint2 = Paint()

    var duration = 2000

    var progress = 100
    set(value) {
        field = value
        invalidate()
    }

    var paintStrokeWidth = 5f

    var indeterminate = false
    set(value){
        field = value
        if(value) {
            valueAnimator = null
            if (valueAnimator == null) {
                valueAnimator = ValueAnimator.ofFloat(0f, DeviceUtils.convertDpToPixel(WIDTH.toFloat() * 2, context))
                valueAnimator.apply {
                    this.repeatMode = ValueAnimator.RESTART
                    this.repeatCount = ValueAnimator.INFINITE
                    this.duration = duration
                    this.addUpdateListener(this@BigProgressBar)
                    this.start()
                }
            } else if (!valueAnimator.isRunning) {
                valueAnimator.start()
            } else {
                valueAnimator.cancel()
                valueAnimator.start()
            }
        }else{
            valueAnimator?.cancel()
            valueAnimator = null
        }
    }



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

    fun startIndeterminateAnimation(){
        if(valueAnimator == null){
            valueAnimator = ValueAnimator.ofFloat(0f, DeviceUtils.convertDpToPixel(WIDTH.toFloat() * 2, context))
            valueAnimator.apply {
                this.repeatMode = ValueAnimator.RESTART
                this.repeatCount = ValueAnimator.INFINITE
                this.duration = 2000
                this.addUpdateListener(this@BigProgressBar)
                this.start()
            }
        }else if(!valueAnimator.isRunning){
            valueAnimator.start()
        }

        else{
            valueAnimator.cancel()
            valueAnimator.start()
        }
    }

    fun stopAnimation(){
        if(valueAnimator!=null)
        if(valueAnimator.isRunning){
            valueAnimator.cancel()
        }
    }


    override fun getPaddingStart(): Int {
        return super.getPaddingStart()
    }

    override fun getPaddingTop(): Int {
        return super.getPaddingTop()
    }

    override fun onDraw(canvas: Canvas?) {
        paint.strokeWidth = paintStrokeWidth

        val r = RectF(paintStrokeWidth / 2, paintStrokeWidth / 2, WIDTH.toFloat() - paintStrokeWidth / 2, HEIGHT.toFloat() - paintStrokeWidth / 2)

        paint.style = Paint.Style.FILL
        paint.color = context.getColor(R.color.pb_gray_color)
        canvas?.drawRoundRect(r, 30f, 30f, paint)
        paint.shader = createGradient()

        paint.style = Paint.Style.FILL
        val clipPath = Path()
        clipPath.addRoundRect(r,30f,30f,Path.Direction.CW)
        canvas?.clipPath(clipPath)

        val p = (WIDTH-paintStrokeWidth) * (progress.toFloat() / MAX!!).toFloat()
            if(p>0){
                val q = RectF(paintStrokeWidth, paintStrokeWidth, p, HEIGHT.toFloat() - paintStrokeWidth)
                canvas?.drawRoundRect(q, 30f, 30f, paint)

            }

        val w = WIDTH.toFloat() - paintStrokeWidth

        if(indeterminate){
            paint2.shader = animated3Gradient()
            val q = RectF(paintStrokeWidth, paintStrokeWidth, w, HEIGHT.toFloat() - paintStrokeWidth)
            canvas?.drawRoundRect(q, 30f, 30f, paint2)


        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        WIDTH = w
        HEIGHT = h
        if(valueAnimator != null) {
            if (valueAnimator.isRunning) {
                valueAnimator.cancel()
                valueAnimator = null
            }
            if (indeterminate) {
                valueAnimator = ValueAnimator.ofFloat(0f, DeviceUtils.convertDpToPixel(WIDTH.toFloat() * 2, context))
                valueAnimator.apply {
                    this.repeatMode = ValueAnimator.RESTART
                    this.repeatCount = ValueAnimator.INFINITE
                    this.duration = 2000
                    this.addUpdateListener(this@BigProgressBar)
                    this.start()
                }
            }
        }
      //  Log.i(TAG, "Width: $w, Height $h")
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

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        Log.i(TAG, "DEtach from windows")
        if(valueAnimator!= null){
            valueAnimator.cancel()
            valueAnimator = null
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        Log.i(TAG, "on Attach to windows")

        if(indeterminate){
            startIndeterminateAnimation()
        }
    }



}