package ar.uba.fi.remy.ui.explorar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import ar.uba.fi.remy.R

class ExplorarFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_explorar, container, false)

        //Título de la pantalla
        val textView: TextView = root.findViewById(R.id.dashboard_title)
        textView.text = getString(R.string.dashboard_title)

        return root
    }
}