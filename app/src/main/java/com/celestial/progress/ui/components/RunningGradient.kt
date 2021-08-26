package com.celestial.progress.ui.components

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator

class RunningGradientView: View, ValueAnimator.AnimatorUpdateListener
{
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    val color1 = Color.argb(255,255,0,0)

    val color2 = Color.argb(155,255,100,100)

    var start = 0f

    val shaderColor = intArrayOf(color1, color2)

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    override fun onDraw(canvas: Canvas?) {
    //    paint.shader = LinearGradient(0f,0f,100f,100f, shaderColor, floatArrayOf(0f,.5f,1f), Shader.TileMode.CLAMP)
        paint.color = color2
        canvas?.drawRect(0f,0f,400f,100f,paint)
        paint.color = color1
        canvas?.drawRect(start,0f,start+100f,100f,paint)

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        when(visibility){
            VISIBLE -> animator.start()
            INVISIBLE, GONE -> animator.cancel()
        }
    }




    companion object{
        val ANIMATION_DURATION = 1000L
    }



    private val animator: ValueAnimator = ValueAnimator.ofFloat(0f, 400f).apply {
        duration = ANIMATION_DURATION
        interpolator = LinearInterpolator()
        repeatCount = ValueAnimator.INFINITE
        addUpdateListener(this@RunningGradientView)
    }

    override fun onAnimationUpdate(animation: ValueAnimator?) {
        if(isAttachedToWindow) {
            val f = animation?.animatedValue as Float
            start = f
            invalidate()
        }else{

        }
    }
}