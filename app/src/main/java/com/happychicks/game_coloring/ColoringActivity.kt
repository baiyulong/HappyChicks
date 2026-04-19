package com.happychicks.game_coloring

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.happychicks.R
import com.happychicks.core.BaseTvActivity
import com.happychicks.core.FocusUtils
import java.io.File
import java.io.FileOutputStream

class ColoringActivity : BaseTvActivity() {

    private lateinit var canvas: ColoringCanvasView
    private lateinit var palette: LinearLayout
    private lateinit var saveBtn: Button

    private val colors = intArrayOf(
        Color.parseColor("#E53935"), Color.parseColor("#FDD835"),
        Color.parseColor("#1E88E5"), Color.parseColor("#43A047"),
        Color.parseColor("#FB8C00"), Color.parseColor("#8E24AA"),
        Color.parseColor("#EC407A"), Color.WHITE,
    )
    private val colorNames = intArrayOf(
        R.string.color_red, R.string.color_yellow, R.string.color_blue, R.string.color_green,
        R.string.color_orange, R.string.color_purple, R.string.color_pink, R.string.color_white
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coloring)
        setupOverscan(findViewById(R.id.root))

        canvas = findViewById(R.id.canvas)
        palette = findViewById(R.id.palette)
        saveBtn = findViewById(R.id.btn_save)

        colors.forEachIndexed { idx, c ->
            val sw = View(this).apply {
                layoutParams = LinearLayout.LayoutParams(96, 96).apply { setMargins(12, 0, 12, 0) }
                background = GradientDrawable().apply {
                    shape = GradientDrawable.OVAL; setColor(c)
                    setStroke(4, Color.DKGRAY)
                }
                isFocusable = true
                setOnClickListener {
                    canvas.setColor(c)
                    tts.speak(getString(colorNames[idx]))
                }
                contentDescription = getString(colorNames[idx])
            }
            palette.addView(sw)
        }

        saveBtn.setOnClickListener { saveImage() }

        FocusUtils.requestInitialFocus(canvas)
    }

    private fun saveImage() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            val perm = Manifest.permission.WRITE_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(perm), REQ_WRITE)
                return
            }
        }
        val bmp = canvas.exportBitmap()
        try {
            @Suppress("DEPRECATION")
            val dir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "HappyChicks")
            if (!dir.exists()) dir.mkdirs()
            val file = File(dir, "chick_${System.currentTimeMillis()}.png")
            FileOutputStream(file).use { out ->
                bmp.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            toast(getString(R.string.image_saved))
        } catch (e: Exception) {
            toast(getString(R.string.image_save_failed))
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQ_WRITE && grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) {
            saveImage()
        } else {
            toast(getString(R.string.image_save_failed))
        }
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).apply { setGravity(Gravity.CENTER, 0, 0) }.show()
    }

    companion object { private const val REQ_WRITE = 1001 }
}
