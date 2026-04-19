package com.happychicks.game_coloring

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View

/**
 * Simple grid-based coloring canvas.
 * A fixed line-art of a chick is represented by 8x6 cells. DPAD moves selector, OK fills with current color.
 */
class ColoringCanvasView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val cols = 8
    private val rows = 6
    private val cells = IntArray(cols * rows) { Color.WHITE }

    // Which cells are part of the chick outline (pre-filled black lines kept)
    private val outline = booleanArrayOf(
        false,false,true,true,true,true,false,false,
        false,true,true,true,true,true,true,false,
        false,true,true,true,true,true,true,false,
        true,true,true,true,true,true,true,true,
        true,true,true,true,true,true,true,true,
        false,true,true,true,true,true,true,false,
    )

    private var selCol = 3
    private var selRow = 3
    private var currentColor = Color.YELLOW

    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val stroke = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE; strokeWidth = 3f; color = Color.DKGRAY
    }
    private val selPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE; strokeWidth = 8f; color = Color.RED
    }

    init { isFocusable = true; isFocusableInTouchMode = false }

    fun setColor(c: Int) { currentColor = c }

    override fun onDraw(canvas: Canvas) {
        val cw = width.toFloat() / cols
        val ch = height.toFloat() / rows
        for (r in 0 until rows) for (c in 0 until cols) {
            val idx = r * cols + c
            if (!outline[idx]) continue
            val rect = RectF(c * cw, r * ch, (c + 1) * cw, (r + 1) * ch)
            fillPaint.color = cells[idx]
            canvas.drawRect(rect, fillPaint)
            canvas.drawRect(rect, stroke)
        }
        // selector highlight
        val sr = RectF(selCol * cw, selRow * ch, (selCol + 1) * cw, (selRow + 1) * ch)
        canvas.drawRect(sr, selPaint)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        var handled = true
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_LEFT -> moveSel(-1, 0)
            KeyEvent.KEYCODE_DPAD_RIGHT -> moveSel(1, 0)
            KeyEvent.KEYCODE_DPAD_UP -> moveSel(0, -1)
            KeyEvent.KEYCODE_DPAD_DOWN -> moveSel(0, 1)
            KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> fillSelected()
            else -> handled = false
        }
        if (handled) invalidate()
        return handled || super.onKeyDown(keyCode, event)
    }

    private fun moveSel(dx: Int, dy: Int) {
        // jump over cells not in outline
        var nc = selCol; var nr = selRow
        for (i in 1..(cols + rows)) {
            nc = (nc + dx).coerceIn(0, cols - 1)
            nr = (nr + dy).coerceIn(0, rows - 1)
            if (outline[nr * cols + nc]) { selCol = nc; selRow = nr; return }
            if (dx == 0 && dy == 0) return
        }
    }

    private fun fillSelected() {
        val idx = selRow * cols + selCol
        if (outline[idx]) cells[idx] = currentColor
    }

    fun exportBitmap(): Bitmap {
        val bmp = Bitmap.createBitmap(width.coerceAtLeast(1), height.coerceAtLeast(1), Bitmap.Config.ARGB_8888)
        val c = Canvas(bmp)
        c.drawColor(Color.WHITE)
        // draw without selector
        val cw = width.toFloat() / cols; val ch = height.toFloat() / rows
        for (r in 0 until rows) for (col in 0 until cols) {
            val idx = r * cols + col
            if (!outline[idx]) continue
            val rect = RectF(col * cw, r * ch, (col + 1) * cw, (r + 1) * ch)
            fillPaint.color = cells[idx]
            c.drawRect(rect, fillPaint); c.drawRect(rect, stroke)
        }
        return bmp
    }
}
