package com.example.alex.start.extensions

import android.support.design.widget.Snackbar
import android.view.View

/**
 * Created by Alex on 06.09.2017.
 */

fun View.perpetualSnackbar(message: Int, actionText: Int, action: (View) -> Unit) = Snackbar
        .make(this, message, Snackbar.LENGTH_INDEFINITE)
        .setAction(actionText, action)
        .show()

fun View.perpetualSnackbar(message: String, actionText: Int, action: (View) -> Unit) = Snackbar
        .make(this, message, Snackbar.LENGTH_INDEFINITE)
        .setAction(actionText, action)
        .show()