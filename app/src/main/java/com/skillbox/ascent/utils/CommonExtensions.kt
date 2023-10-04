package com.skillbox.ascent.utils

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.skillbox.ascent.R
import java.util.*

fun <T : ViewBinding> ViewGroup.inflate(
    inflateBinding: (
        inflater: LayoutInflater,
        root: ViewGroup?,
        attachToRoot: Boolean
    ) -> T, attachToRoot: Boolean = false
): T {
    val inflater = LayoutInflater.from(context)
    return inflateBinding(inflater, this, attachToRoot)
}

fun Activity.hideKeyboardAndClearFocus() {
    val view = currentFocus ?: View(this)
    hideKeyboardFrom(view)
    view.clearFocus()
}

fun Context.hideKeyboardFrom(view: View) {
    getSystemService(Activity.INPUT_METHOD_SERVICE)
        .let { it as InputMethodManager }
        .hideSoftInputFromWindow(view.windowToken, 0)
}

fun <T : Fragment> T.toast(@StringRes message: Int) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}

fun <T : Fragment> T.toast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}

fun haveQ(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
}

fun twoDigit(digit: Int): String {
    return if (digit in 0..9) "0$digit" else "$digit"
}

fun snackBar(view: View, message: String) {
    Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
}

fun statusBottomBar(activity: Activity, status: Boolean) {
    val bottomNavigation = activity.findViewById<BottomNavigationView>(R.id.bottomNavigation)
    bottomNavigation.isVisible = status
}

fun calculateDeferredTime(alertHour: Int, alertMin: Int): Long {
    val currentDate = Calendar.getInstance()
    val alertDate = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, alertHour)
        set(Calendar.MINUTE, alertMin)
        set(Calendar.SECOND, 0)
    }
    if (alertDate.before(currentDate)) {
        Log.d("extensions", "${alertDate.before(currentDate)}")
        alertDate.add(Calendar.HOUR_OF_DAY, 24)
    }
    Log.d("extensions", " currentDate.time   ${currentDate.time}")
    Log.d("extensions", " alertDate.time   ${alertDate.time}")
    Log.d("extensions", " alertDate.before(currentDate)  ${alertDate.before(currentDate)}")
    Log.d(
        "extensions",
        "alertDate--${alertDate.timeInMillis}  currenrt--${currentDate.timeInMillis}"
    )
    return alertDate.timeInMillis - currentDate.timeInMillis
}


fun <T : Fragment> T.listenerMenuToolBar(toolBar: Toolbar) {
    toolBar.setOnMenuItemClickListener { menuItem ->
        when (menuItem.itemId) {
            R.id.set_alert -> {
                findNavController().navigate(R.id.alertFragment)
                // findNavController().navigate(R.id.alertFragment)
                true
            }
            else -> false
        }
    }
}

fun snackBarSuccess(view: View, message: String, bottomBar: BottomNavigationView) {
    val context = view.context
    Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
        .setBackgroundTint(setColor(context, R.color.snack_success_back))
        .setTextColor(setColor(context, R.color.snack_success_text))
        .setAnchorView(bottomBar)
        .show()
}

fun snackBarFailed(view: View, message: String, bottomBar: BottomNavigationView) {
    val context = view.context
    Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
        .setBackgroundTint(setColor(context, R.color.snack_failed_back))
        .setTextColor(setColor(context, R.color.snack_failed_text))
        .setAnchorView(bottomBar)
        .show()
}

fun searchBottomView(activity: Activity): BottomNavigationView {
    return activity.findViewById(R.id.bottomNavigation)
}


fun setColor(context: Context, color: Int) = ContextCompat.getColor(context, color)


//--------------------------------------------------------------------------------------------------

