package com.happychicks.game_puzzle

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.happychicks.R
import com.happychicks.core.BaseTvActivity
import com.happychicks.core.FocusUtils

/**
 * Sliding puzzle: generate a colorful image, split into tiles, shuffle with one empty slot.
 * Focus a tile and press OK to slide it into the empty slot (if adjacent).
 */
class PuzzleActivity : BaseTvActivity() {

    private var size = 3
    private lateinit var grid: GridLayout
    private lateinit var sizePicker: LinearLayout

    private val tileOrder = mutableListOf<Int>() // current positions; value = tile original index; size*size-1 = empty
    private lateinit var sourceBitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_puzzle)
        setupOverscan(findViewById(R.id.root))

        sizePicker = findViewById(R.id.size_picker)
        grid = findViewById(R.id.grid)

        val b2 = findViewById<Button>(R.id.btn_2x2)
        val b3 = findViewById<Button>(R.id.btn_3x3)
        b2.setOnClickListener { startPuzzle(2) }
        b3.setOnClickListener { startPuzzle(3) }
        FocusUtils.requestInitialFocus(b2)
    }

    private fun startPuzzle(n: Int) {
        size = n
        sizePicker.visibility = View.GONE
        grid.visibility = View.VISIBLE
        grid.removeAllViews()
        grid.rowCount = n; grid.columnCount = n

        sourceBitmap = generateSource()
        val tileSize = 200
        val tiles = mutableListOf<Int>()
        for (i in 0 until n * n) tiles.add(i)
        // Empty is last index. Shuffle solvable by random valid moves.
        tileOrder.clear(); tileOrder.addAll(tiles)
        shuffleByMoves(80)

        val firstFocus = arrayOfNulls<View>(1)
        for (pos in 0 until n * n) {
            val index = pos
            val cell = FrameLayout(this).apply {
                layoutParams = GridLayout.LayoutParams().apply {
                    width = tileSize; height = tileSize
                    setMargins(4, 4, 4, 4)
                }
                isFocusable = true; isClickable = true
                background = resources.getDrawable(R.drawable.bg_menu_card, null)
                setOnClickListener { tryMove(index) }
            }
            val tileVal = tileOrder[pos]
            if (tileVal != n * n - 1) {
                val iv = ImageView(this).apply {
                    layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                    scaleType = ImageView.ScaleType.CENTER_CROP
                    setImageBitmap(tileBitmap(sourceBitmap, tileVal, n))
                }
                cell.addView(iv)
            }
            grid.addView(cell)
            if (pos == 0) firstFocus[0] = cell
        }
        FocusUtils.requestInitialFocus(firstFocus[0])
    }

    private fun generateSource(): Bitmap {
        val s = 600
        val bmp = Bitmap.createBitmap(s, s, Bitmap.Config.ARGB_8888)
        val c = Canvas(bmp)
        val p = Paint(Paint.ANTI_ALIAS_FLAG)
        val colors = intArrayOf(0xFFB74D.toInt(), 0xFF8A65.toInt(), 0xAED581.toInt(), 0x81D4FA.toInt())
        for (r in 0 until 4) for (col in 0 until 4) {
            p.color = colors[(r + col) % colors.size]
            c.drawRect((col * s / 4).toFloat(), (r * s / 4).toFloat(),
                ((col + 1) * s / 4).toFloat(), ((r + 1) * s / 4).toFloat(), p)
        }
        // chick in center
        p.color = 0xFFF176.toInt()
        c.drawCircle(s / 2f, s / 2f, (s / 3f), p)
        p.color = Color.BLACK
        c.drawCircle(s / 2f - 40, s / 2f - 20, 8f, p)
        c.drawCircle(s / 2f + 40, s / 2f - 20, 8f, p)
        return bmp
    }

    private fun tileBitmap(src: Bitmap, originalIndex: Int, n: Int): Bitmap {
        val r = originalIndex / n; val col = originalIndex % n
        val w = src.width / n; val h = src.height / n
        return Bitmap.createBitmap(src, col * w, r * h, w, h)
    }

    private fun shuffleByMoves(moves: Int) {
        var empty = size * size - 1
        repeat(moves) {
            val neighbors = mutableListOf<Int>()
            val r = empty / size; val c = empty % size
            if (r > 0) neighbors.add(empty - size)
            if (r < size - 1) neighbors.add(empty + size)
            if (c > 0) neighbors.add(empty - 1)
            if (c < size - 1) neighbors.add(empty + 1)
            val target = neighbors.random()
            val tmp = tileOrder[empty]; tileOrder[empty] = tileOrder[target]; tileOrder[target] = tmp
            empty = target
        }
    }

    private fun tryMove(pos: Int) {
        val empty = tileOrder.indexOf(size * size - 1)
        val r1 = pos / size; val c1 = pos % size
        val r2 = empty / size; val c2 = empty % size
        if ((r1 == r2 && kotlin.math.abs(c1 - c2) == 1) ||
            (c1 == c2 && kotlin.math.abs(r1 - r2) == 1)) {
            val tmp = tileOrder[empty]; tileOrder[empty] = tileOrder[pos]; tileOrder[pos] = tmp
            // Swap views (re-render by removing all and rebuilding for simplicity)
            rebuildGrid()
            if (isSolved()) {
                tts.speak(getString(R.string.puzzle_complete))
                repo.addCoins(COINS_PER_PUZZLE)
                android.widget.Toast.makeText(this, R.string.puzzle_complete, android.widget.Toast.LENGTH_LONG)
                    .apply { setGravity(Gravity.CENTER, 0, 0) }.show()
            }
        }
    }

    private fun rebuildGrid() {
        val focusedIdx = (0 until grid.childCount).firstOrNull { grid.getChildAt(it).hasFocus() } ?: 0
        grid.removeAllViews()
        val tileSize = 200
        val firstFocus = arrayOfNulls<View>(1)
        for (pos in 0 until size * size) {
            val index = pos
            val cell = FrameLayout(this).apply {
                layoutParams = GridLayout.LayoutParams().apply {
                    width = tileSize; height = tileSize
                    setMargins(4, 4, 4, 4)
                }
                isFocusable = true; isClickable = true
                background = resources.getDrawable(R.drawable.bg_menu_card, null)
                setOnClickListener { tryMove(index) }
            }
            val tileVal = tileOrder[pos]
            if (tileVal != size * size - 1) {
                val iv = ImageView(this).apply {
                    layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                    scaleType = ImageView.ScaleType.CENTER_CROP
                    setImageBitmap(tileBitmap(sourceBitmap, tileVal, size))
                }
                cell.addView(iv)
            }
            grid.addView(cell)
            if (pos == focusedIdx) firstFocus[0] = cell
        }
        FocusUtils.requestInitialFocus(firstFocus[0])
    }

    private fun isSolved(): Boolean =
        tileOrder.withIndex().all { (i, v) -> v == i }

    companion object { private const val COINS_PER_PUZZLE = 10 }
}
