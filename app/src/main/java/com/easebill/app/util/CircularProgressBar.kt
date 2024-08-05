package com.easebill.app.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class CircularProgressBar : View {
    private var progress = 0
    private var maxProgress = 100

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height / 2f
        val radius = (width.coerceAtMost(height) / 2f) * 0.8f

        val paint = Paint().apply {
            style = Paint.Style.STROKE
            color = Color.LTGRAY
            strokeWidth = 20f
            strokeCap = Paint.Cap.ROUND
        }

        val oval = RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius)

        // Draw background arc
        canvas.drawArc(oval, 0f, 360f, false, paint)

        paint.color = Color.GREEN
        // Draw progress arc
        canvas.drawArc(oval, -90f, 360 * (progress / maxProgress.toFloat()), false, paint)

        // Draw percentage text
        paint.style = Paint.Style.FILL
        paint.textSize = 100f
        paint.color = Color.BLACK
        paint.textAlign = Paint.Align.CENTER
        val text = "$progress%"
        val textHeight = paint.descent() - paint.ascent()
        val textOffset = textHeight / 2f - paint.descent()
        canvas.drawText(text, centerX, centerY + textOffset, paint)
    }

    fun setProgress(progress: Int) {
        this.progress = progress
        invalidate()
    }
}