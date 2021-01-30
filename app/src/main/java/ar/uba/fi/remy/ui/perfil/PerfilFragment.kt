package ar.uba.fi.remy.ui.perfil

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import ar.uba.fi.remy.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup
import kotlinx.android.synthetic.main.fragment_perfil.*
import org.json.JSONArray
import org.json.JSONObject

class PerfilFragment : Fragment() {
    lateinit var chipGroup:ChipGroup
    var forbiddenProducts:MutableList<String> = ArrayList()
    lateinit var token: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_perfil, container, false)

        //Obtener token
        val sharedPref = activity?.getSharedPreferences(
            getString(R.string.preference_file), Context.MODE_PRIVATE)
        token = sharedPref?.getString("TOKEN", "")!!

        val btnSalir: TextView = root.findViewById(R.id.perfil_salir)
        btnSalir.setOnClickListener(View.OnClickListener {
            cerrarSesion()
        })

        val btnEventos: Button = root.findViewById(R.id.perfil_btn_eventos)
        btnEventos.setOnClickListener(View.OnClickListener {
            goEvents()
        })

        val btnContacts: Button = root.findViewById(R.id.perfil_btn_contactos)
        btnContacts.setOnClickListener(View.OnClickListener {
            goContacts()
        })

        val btnRecipesCooked: Button = root.findViewById(R.id.perfil_btn_recetas_cocinadas)
        btnRecipesCooked.setOnClickListener {
            goRecipesCooked()
        }

        cargarPerfil()

        chipGroup = root.findViewById(R.id.perfil_chipgroup)

        configAddForbidden(root)

        return root
    }

    private fun cargarPerfil() {
        val queue = Volley.newRequestQueue(activity)
        val url = "https://tpp-remy.herokuapp.com/api/v1/profiles/3/"

        val jsonObjectRequest = object: JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                Log.i("API", "Response: %s".format(response.toString()))
                var response_forbidden = response.getJSONArray("forbidden_products")
                for(i in 0 until response_forbidden.length()) {
                    val ingrediente = response_forbidden.getString(i)
                    forbiddenProducts.add(ingrediente)
                    addChip(ingrediente)
                }
                var response_profile_types = response.getJSONArray("profiletypes")
                configProfileTypes(response_profile_types)
            },
            Response.ErrorListener { error ->
                Log.e("API", "Error en GET")
                Log.e("API", "Response: %s".format(error.toString()))
            }
        )
        {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Token " + token
                return headers
            }
        }

        queue.add(jsonObjectRequest)
    }

    private fun configProfileTypes(responseProfileTypes: JSONArray) {
        for(i in 0 until responseProfileTypes.length()) {
            val type = responseProfileTypes.getString(i)
            /*Log.i("API", "Type: " + type)*/

            if(type == "Vegetariano") {
                Vegetariano.isChecked = true
            }

            if(type == "Vegano") {
                Vegano.isChecked = true
            }

            if(type == "Celiaco") {
                Celiaco.isChecked = true
            }

            if(type == "Diabetico") {
                Diabetico.isChecked = true
            }
        }
    }

    private fun configAddForbidden(root: View) {
        var btnAddForbidden = root.findViewById<ImageButton>(R.id.perfil_add_forbidden)
        btnAddForbidden.setOnClickListener {
            var txtIngredient = perfil_forbidden.text
            if(!txtIngredient.isNullOrEmpty()) {
                requestForbidden(txtIngredient.toString())
            }
        }
    }

    private fun addChip(ingredient: String) {
        val chip = Chip(context)
        chip.text = ingredient
        val drawable = context?.let { ChipDrawable.createFromAttributes(it, null, 0, R.style.Widget_MaterialComponents_Chip_Entry) }
        drawable?.let { chip.setChipDrawable(it) }
        chip.chipEndPadding = 20F
        chipGroup.addView(chip)
        chip.setOnCloseIconClickListener { removeChip(chip, ingredient) }
    }

    private fun removeChip(chip: Chip, ingredient: String) {
        chipGroup.removeView(chip as View)
        forbiddenProducts.remove(ingredient)
        requestForbidden(null)
    }

    private fun requestForbidden(ingrediente: String?) {
        val body = JSONObject()
        if(!ingrediente.isNullOrEmpty()) {
            forbiddenProducts.add(ingrediente)
        }
        val jsonForbidden = JSONArray(forbiddenProducts)
        body.put("forbidden_products", jsonForbidden)

        Log.i("API", "Nuevo prohibido: %s".format(body.toString()))

        val queue = Volley.newRequestQueue(activity)
        val url = "https://tpp-remy.herokuapp.com/api/v1/profiles/3/"

        val jsonObjectRequest = object: JsonObjectRequest(
            Request.Method.PATCH, url, body,
            Response.Listener { response ->
                Log.i("API", "Response: %s".format(response.toString()))
                if(!ingrediente.isNullOrEmpty()) {
                    addChip(ingrediente)
                }
                perfil_forbidden.setText("")
            },
            Response.ErrorListener { error ->
                Log.e("API", "Error en GET")
                Log.e("API", "Response: %s".format(error.toString()))
            }
        )
        {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Token " + token
                return headers
            }
        }

        queue.add(jsonObjectRequest)
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

    private fun goContacts() {
        val intent = Intent(activity, ContactsActivity::class.java)
        startActivity(intent)
    }

    private fun goRecipesCooked() {
        val intent = Intent(activity, RecipesCookedActivity::class.java)
        startActivity(intent)
    }
}