package ar.uba.fi.remy.ui.chango

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ar.uba.fi.remy.R
import kotlinx.android.synthetic.main.fragment_chango.*

class ChangoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_chango, container, false)

        return root
    }
}