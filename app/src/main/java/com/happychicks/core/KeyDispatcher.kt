package com.happychicks.core

import android.view.KeyEvent

object KeyDispatcher {
    fun isConfirm(event: KeyEvent): Boolean =
        event.action == KeyEvent.ACTION_DOWN && (
            event.keyCode == KeyEvent.KEYCODE_DPAD_CENTER ||
            event.keyCode == KeyEvent.KEYCODE_ENTER ||
            event.keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER ||
            event.keyCode == KeyEvent.KEYCODE_BUTTON_A
        )

    fun isBack(event: KeyEvent): Boolean =
        event.action == KeyEvent.ACTION_DOWN && (
            event.keyCode == KeyEvent.KEYCODE_BACK ||
            event.keyCode == KeyEvent.KEYCODE_BUTTON_B ||
            event.keyCode == KeyEvent.KEYCODE_ESCAPE
        )

    fun isDirection(event: KeyEvent): Boolean = event.action == KeyEvent.ACTION_DOWN && (
        event.keyCode == KeyEvent.KEYCODE_DPAD_UP ||
        event.keyCode == KeyEvent.KEYCODE_DPAD_DOWN ||
        event.keyCode == KeyEvent.KEYCODE_DPAD_LEFT ||
        event.keyCode == KeyEvent.KEYCODE_DPAD_RIGHT
    )
}
