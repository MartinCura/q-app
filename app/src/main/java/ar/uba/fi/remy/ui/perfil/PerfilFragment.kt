package ar.uba.fi.remy.ui.perfil

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import ar.uba.fi.remy.EventsActivity
import ar.uba.fi.remy.LoginActivity
import ar.uba.fi.remy.R
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup
import kotlinx.android.synthetic.main.fragment_perfil.*

class PerfilFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_perfil, container, false)

        val btnSalir: TextView = root.findViewById(R.id.perfil_salir)
        btnSalir.setOnClickListener(View.OnClickListener {
            cerrarSesion()
        })

        val btnEventos: Button = root.findViewById(R.id.perfil_btn_eventos)

        btnEventos.setOnClickListener(View.OnClickListener {
            goEvents()
        })

        cargarIngredientesProhibidos(root)

        return root
    }

    private fun cargarIngredientesProhibidos(root: View) {
        // TO-DO: hacer llamada a la api

        // Respuesta simulada:
        val ingredientes = arrayOf("Dulce de leche", "Azucar", "Harina")

        for (ingrediente in ingredientes) {
            val chipGroup: ChipGroup = root.findViewById(R.id.perfil_chipgroup)
            val chip = Chip(context)
            chip.text = ingrediente
            val drawable = context?.let { ChipDrawable.createFromAttributes(it, null, 0, R.style.Widget_MaterialComponents_Chip_Entry) }
            drawable?.let { chip.setChipDrawable(it) }
            chip.chipEndPadding = 20F
            chipGroup.addView(chip)
            chip.setOnCloseIconClickListener { chipGroup.removeView(chip as View) }
        }
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

    private fun goLogin() {
        val intent = Intent(activity, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        getActivity()?.finish()
    }

    private fun goEvents() {
        val intent = Intent(activity, EventsActivity::class.java)
        startActivity(intent)
    }
}