package org.endhungerdurham.pantries.ui.utils

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.FontMetricsInt
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.text.style.ImageSpan

// Source: https://stackoverflow.com/a/38788432
class VerticalImageSpan(drawable: Drawable?) : ImageSpan(drawable!!) {
    /**
     * update the text line height
     */
    override fun getSize(paint: Paint, text: CharSequence?, start: Int, end: Int,
                fontMetricsInt: FontMetricsInt?): Int {
        val drawable = drawable
        val rect: Rect = drawable.bounds
        if (fontMetricsInt != null) {
            val fmPaint: FontMetricsInt = paint.getFontMetricsInt()
            val fontHeight = fmPaint.descent - fmPaint.ascent
            val drHeight: Int = rect.bottom - rect.top
            val centerY = fmPaint.ascent + fontHeight / 2
            fontMetricsInt.ascent = centerY - drHeight / 2
            fontMetricsInt.top = fontMetricsInt.ascent
            fontMetricsInt.bottom = centerY + drHeight / 2
            fontMetricsInt.descent = fontMetricsInt.bottom
        }
        return rect.right
    }

    /**
     * see detail message in android.text.TextLine
     *
     * @param canvas the canvas, can be null if not rendering
     * @param text the text to be draw
     * @param start the text start position
     * @param end the text end position
     * @param x the edge of the replacement closest to the leading margin
     * @param top the top of the line
     * @param y the baseline
     * @param bottom the bottom of the line
     * @param paint the work paint
     */
    override fun draw(canvas: Canvas, text: CharSequence?, start: Int, end: Int,
             x: Float, top: Int, y: Int, bottom: Int, paint: Paint) {
        val drawable = drawable
        canvas.save()
        val fmPaint: FontMetricsInt = paint.getFontMetricsInt()
        val fontHeight = fmPaint.descent - fmPaint.ascent
        val centerY = y + fmPaint.descent - fontHeight / 2
        val transY = centerY - (drawable.bounds.bottom - drawable.bounds.top) / 2
        canvas.translate(x, transY.toFloat())
        drawable.draw(canvas)
        canvas.restore()
    }
}