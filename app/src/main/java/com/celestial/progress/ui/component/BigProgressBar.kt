package com.celestial.progress.ui.component

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View


const val TAG = "BigProgressBar"

class BigProgressBar: View, ValueAnimator.AnimatorUpdateListener {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    var color1: Int
    var color2: Int
    var paint = Paint()



    var WIDTH= 0
    var HEIGHT = 0

    var MIN: Int? = null
    var MAX: Int? = null

    init {
        this.color1 = Color.RED
        this.color2 = Color.BLUE
        MIN = 25
        MAX = 100
    }

    override fun onDraw(canvas: Canvas?) {
        paint.color = color1
        canvas?.drawRect(0f, 0f, WIDTH.toFloat(), 20f, paint)
        paint.color = color2


        paint.shader = createGradient()
        canvas?.drawRect(0f, 0f, WIDTH / 3.toFloat(), 20f, paint)

        Log.i("ProgressBar:", "WIDTH:${WIDTH}, WIDTH/2:${WIDTH / 2}")
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        WIDTH = w
        HEIGHT = h
        Log.i(TAG, "Width: $w, Height $h")
    }

    override fun onAnimationUpdate(animation: ValueAnimator?) {

    }


     fun getPercent(): Int{


        return WIDTH/2
    }


    fun createGradient():Shader{

        val linearGradientShader: Shader = LinearGradient(0f, 0f, 300f, 20f, intArrayOf(Color.RED, Color.BLUE), floatArrayOf(0f, 1f), Shader.TileMode.CLAMP)
        return linearGradientShader
    }
}