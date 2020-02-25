package ar.uba.fi.remy.ui.perfil

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import ar.uba.fi.remy.R

class PerfilFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_perfil, container, false)

        val cardInventario: CardView = root.findViewById(R.id.card_inventario)
        cardInventario.setOnClickListener{
            goInventario()
        }

        return root
    }

    private fun goInventario() {
        Toast.makeText(activity, "Click Inventario", Toast.LENGTH_LONG).show()
    }
}