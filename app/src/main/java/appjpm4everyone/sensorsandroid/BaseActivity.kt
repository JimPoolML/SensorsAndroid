package appjpm4everyone.sensorsandroid

import android.app.Activity
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import de.mateware.snacky.Snacky


abstract class BaseActivity : AppCompatActivity() {

    //internal val mProgressDialog = CustomProgressBar()

    /*override fun showProgress(msgRes: String) {
        mProgressDialog.show(this, msgRes)
    }

    override fun showProgress() {
        mProgressDialog.show(this)
    }
    override fun hideProgress() {
        mProgressDialog.hideProgress()
    }*/

    fun showShortSnackError(activity: Activity, message: String) {
        showSnack(activity, message, Snacky.LENGTH_SHORT)
    }

    fun showShortSnackError(activity: Activity, msgResource: Int) {
        showSnack(activity, msgResource, Snacky.LENGTH_SHORT)
    }

    fun showLongSnackError(activity: Activity, message: String) {
        showSnack(activity, message, Snacky.LENGTH_LONG)
    }

    fun showLongSnackError(activity: Activity, msgResource: Int) {
        showSnack(activity, msgResource, Snacky.LENGTH_LONG)
    }

    fun showSnack(activity: Activity, message: String, lenght: Int) {
        val typeface = ResourcesCompat.getFont(this, R.font.opensans_regular)
        Snacky.builder()
            .setActivity(activity)
            .setTextSize(16f)
            .setTextTypeface(typeface)
            .setIcon(R.drawable.corona_v)
            .setText(message)
            .setBackgroundColor(ContextCompat.getColor(activity, R.color.purple_toolbar))
            .setDuration(lenght)
            .error()
            .show()
    }

    fun showSnack(activity: Activity, msgResource: Int, lenght: Int) {
        val typeface = ResourcesCompat.getFont(this, R.font.opensans_regular)
        Snacky.builder()
            .setActivity(activity)
            .setTextSize(16f)
            .setTextTypeface(typeface)
            .setText(msgResource)
            .setIcon(R.drawable.corona_v)
            .setDuration(lenght)
            .setBackgroundColor(ContextCompat.getColor(activity, R.color.purple_toolbar))
            .error()
            .show()
    }

    fun hideKeyboardFrom(activity: Activity) {
        val imm =
            activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        if (activity.currentFocus != null) {
            imm.hideSoftInputFromWindow(
                activity.currentFocus!!.windowToken, 0
            )
        }
    }

}