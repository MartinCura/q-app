package ar.uba.fi.remy.ui.loadingIndicator

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.FragmentActivity
import ar.uba.fi.remy.R

/**
 * A simple [Fragment] subclass.
 * Use the [LoadingIndicatorFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
public class LoadingIndicatorFragment : Fragment() {
    companion object {
        private lateinit var dialogLoading: Dialog

        fun show(context: Context)
        {
            dialogLoading = Dialog(context)
            dialogLoading.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialogLoading.setCancelable(false)
            dialogLoading.setContentView(R.layout.fragment_loading_indicator)
            dialogLoading.show()
        }

        fun hide() {
            dialogLoading.dismiss()
        }
    }
}