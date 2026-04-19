package com.happychicks.core

import android.view.View

object FocusUtils {
    fun requestInitialFocus(view: View?) {
        view?.post {
            view.isFocusable = true
            view.isFocusableInTouchMode = true
            view.requestFocus()
        }
    }

    fun linkHorizontal(vararg views: View) {
        for (i in views.indices) {
            views[i].nextFocusLeftId = views[(i - 1 + views.size) % views.size].id
            views[i].nextFocusRightId = views[(i + 1) % views.size].id
        }
    }

    fun linkVertical(vararg views: View) {
        for (i in views.indices) {
            views[i].nextFocusUpId = views[(i - 1 + views.size) % views.size].id
            views[i].nextFocusDownId = views[(i + 1) % views.size].id
        }
    }

    fun linkGrid(cols: Int, vararg views: View) {
        val rows = (views.size + cols - 1) / cols
        for (i in views.indices) {
            val r = i / cols; val c = i % cols
            views[i].nextFocusLeftId = if (c > 0) views[i - 1].id else views[i].id
            views[i].nextFocusRightId = if (c < cols - 1 && i + 1 < views.size) views[i + 1].id else views[i].id
            views[i].nextFocusUpId = if (r > 0) views[i - cols].id else views[i].id
            views[i].nextFocusDownId = if (r < rows - 1 && i + cols < views.size) views[i + cols].id else views[i].id
        }
    }
}
