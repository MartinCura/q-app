package ar.uba.fi.remy.ui.perfil

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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
import android.widget.AdapterView

class PerfilFragment : Fragment() {
    lateinit var chipGroup:ChipGroup
    var forbiddenProducts:MutableList<String> = ArrayList()
    var responseProfileTypes:MutableList<String> = ArrayList()
    lateinit var token: String
    lateinit var spinner: Spinner
    var arrayIDPlaces:MutableList<Int> = ArrayList()
    var arrayNamePlaces:MutableList<String> = ArrayList()

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


        cargarPerfil(root)
        /*configCheckboxListener(root)*/

        chipGroup = root.findViewById(R.id.perfil_chipgroup)
        configAddForbidden(root)

        spinner = root.findViewById(R.id.places_spinner)
        loadPlaces()


        return root
    }

    private fun loadPlaces() {
        val queue = Volley.newRequestQueue(activity)
        val url = "https://tpp-remy.herokuapp.com/api/v1/places/"

        val jsonObjectRequest = object: JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                Log.i("API", "Response: %s".format(response.toString()))
                var places = response.getJSONArray("results")
                configSpinner(places)
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

    private fun configSpinner(places: JSONArray?) {
        // [{"id":1,"name":"string","members":[3]},{"id":2,"name":"string","members":[3]}]
        if (places != null) {
            for (i in 0 until places.length()) {
                val item = places?.getJSONObject(i)
                arrayNamePlaces.add(item.getString("name"))
                arrayIDPlaces.add(item.getInt("id"))
            }
        }

        var aa = ArrayAdapter(context, android.R.layout.simple_spinner_item, arrayNamePlaces)

        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        with(spinner)
        {
            adapter = aa
            setSelection(0, false)
            prompt = "Selecciona tu inventario"
            gravity = Gravity.CENTER

        }

        spinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                var CapturaSpinner = parent.getItemAtPosition(position) as String
                var index = position
                Log.i("API","Indice:$index")
                Log.i("API","ID:" + arrayIDPlaces[index])
                setDefaultPlace(arrayIDPlaces[index])
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        })
    }

    private fun setDefaultPlace(id: Int) {
        //https://tpp-remy.herokuapp.com/api/v1/default_place?place_id=5
        val queue = Volley.newRequestQueue(activity)
        val url = "https://tpp-remy.herokuapp.com/api/v1/default_place?place_id=" + id

        val jsonObjectRequest = object: JsonObjectRequest(
            Request.Method.POST, url, null,
            Response.Listener { response ->
                Log.i("API", "Response: %s".format(response.toString()))
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

    private fun configCheckboxListener(root: View) {
        val checkVegetariano: CheckBox = root.findViewById(R.id.Vegetariano)
        checkVegetariano.setOnCheckedChangeListener { compoundButton, isChecked ->
            Log.i("API", "Check Vegetariano: " + isChecked)
            if(isChecked && !responseProfileTypes.contains("Vegetariano")) {
                responseProfileTypes.add("Vegetariano")
            } else {
                responseProfileTypes.remove("Vegetariano")
            }
            requestProfileTypes()
        }

        val checkVegano: CheckBox = root.findViewById(R.id.Vegano)
        checkVegano.setOnCheckedChangeListener { compoundButton, isChecked ->
            Log.i("API", "Check Vegano: " + isChecked)
            if(isChecked && !responseProfileTypes.contains("Vegano")) {
                responseProfileTypes.add("Vegano")
            } else {
                responseProfileTypes.remove("Vegano")
            }
            requestProfileTypes()
        }

        val checkCeliaco: CheckBox = root.findViewById(R.id.Celiaco)
        checkCeliaco.setOnCheckedChangeListener { compoundButton, isChecked ->
            Log.i("API", "Check Celiaco: " + isChecked)
            if(isChecked && !responseProfileTypes.contains("Celiaco")) {
                responseProfileTypes.add("Celiaco")
            } else {
                responseProfileTypes.remove("Celiaco")
            }
            requestProfileTypes()
        }

        val checkDiabetico: CheckBox = root.findViewById(R.id.Diabetico)
        checkDiabetico.setOnCheckedChangeListener { compoundButton, isChecked ->
            Log.i("API", "Check Diabetico: " + isChecked)
            if(isChecked && !responseProfileTypes.contains("Diabetico")) {
                responseProfileTypes.add("Diabetico")
            } else {
                responseProfileTypes.remove("Diabetico")
            }
            requestProfileTypes()
        }
    }

    private fun cargarPerfil(root: View) {
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
                configCheckboxListener(root)
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

    private fun configProfileTypes(response_profile_types: JSONArray) {
        for(i in 0 until response_profile_types.length()) {
            val type = response_profile_types.getString(i)
            responseProfileTypes.add(type)

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

    private fun requestProfileTypes() {
        val jsonTypes = JSONArray(responseProfileTypes)
        val body = JSONObject()
        body.put("profiletypes", jsonTypes)
        Log.i("API", "Final types: " + body.toString())

        val queue = Volley.newRequestQueue(activity)
        val url = "https://tpp-remy.herokuapp.com/api/v1/profiles/3/"

        val jsonObjectRequest = object: JsonObjectRequest(
            Request.Method.PATCH, url, body,
            Response.Listener { response ->
                Log.i("API", "Response: %s".format(response.toString()))
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
}