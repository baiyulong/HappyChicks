package com.happychicks.core

import android.app.Activity
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/** Apply overscan-safe padding (5% of screen) to the root view. */
object OverscanHelper {
    fun applyToRoot(activity: Activity, root: View) {
        val dm = activity.resources.displayMetrics
        val horiz = (dm.widthPixels * 0.05f).toInt()
        val vert = (dm.heightPixels * 0.05f).toInt()
        root.setPadding(horiz, vert, horiz, vert)
        ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
            val sys = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                maxOf(horiz, sys.left),
                maxOf(vert, sys.top),
                maxOf(horiz, sys.right),
                maxOf(vert, sys.bottom)
            )
            insets
        }
    }
}
