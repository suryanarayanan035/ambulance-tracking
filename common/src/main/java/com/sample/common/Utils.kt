package com.sample.common

import android.view.View
import com.google.android.material.snackbar.Snackbar

inline fun View.snackbar(msg: String?, action: String?, anchor: Boolean = false, crossinline func: () -> Unit = {}) {
    Snackbar.make(
        this,
        msg ?: "Nothing", Snackbar.LENGTH_LONG,
    ).apply {
        if (anchor) {
            anchorView = this@snackbar
        }
        action?.let {
            setAction(it) {
                func()
            }
        }
    }.show()
}