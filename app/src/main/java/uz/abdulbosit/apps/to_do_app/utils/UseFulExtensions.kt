package uz.abdulbosit.apps.to_do_app.utils

import android.app.ActionBar.LayoutParams
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.Toast
import androidx.annotation.GravityInt

fun Dialog.setDialogConfigurations(cancelable:Boolean, @GravityInt gravity:Int) {
    this.show()
    this.setCancelable(cancelable)
    this.window?.setLayout(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT)
    this.window?.setGravity(gravity)
    this.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//    this.window?.setWindowAnimations() // Your animation
}
//fun String.myLog(tag: String = "TTT") = Timber.tag(tag).d(this)

fun Context.toToast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
fun String.toToast(context: Context) = Toast.makeText(context, this, Toast.LENGTH_SHORT).show()
fun View.gone() {
    this.visibility = View.GONE
}

fun View.visible() {
    this.visibility = View.VISIBLE
}
