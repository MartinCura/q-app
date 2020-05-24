package ar.uba.fi.remy.ui.perfil

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import ar.uba.fi.remy.LoginActivity
import ar.uba.fi.remy.R
import kotlinx.android.synthetic.main.fragment_perfil.*

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

        val btnSalir: TextView = root.findViewById(R.id.perfil_salir)
        btnSalir.setOnClickListener(View.OnClickListener {
            cerrarSesion()
        })

        return root
    }

    private fun cerrarSesion() {
        //Access sharedPreferences
        val sharedPref = activity?.getSharedPreferences(
            getString(R.string.preference_file), Context.MODE_PRIVATE)
        val editor = sharedPref?.edit()
        editor?.clear()
        editor?.apply()

        goLogin()
    }

    private fun goInventario() {
        Toast.makeText(activity, "Click Inventario", Toast.LENGTH_LONG).show()
    }

    private fun goLogin() {
        val intent = Intent(activity, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        getActivity()?.finish()
    }
}