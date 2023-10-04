package com.skillbox.ascent.ui.alert

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class OvalView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var canRes: Canvas? = null
    private var paint = Paint()
    private val ratioRadius = 0.5f
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canRes = canvas
        val h = height
        val w = width
        if (w == 0 || h == 0) return
        val x = w.toFloat() / 2.0f
        val y = h.toFloat() / 2.0f
        val radius = if (w > h) h * ratioRadius else w * ratioRadius
        paint.reset()
        paint.color = Color.WHITE
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f
        paint.isAntiAlias = true
        canvas?.drawCircle(x, y, radius, paint)
    }
}



