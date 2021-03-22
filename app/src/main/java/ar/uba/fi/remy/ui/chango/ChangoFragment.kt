package ar.uba.fi.remy.ui.chango

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import ar.uba.fi.remy.R
import ar.uba.fi.remy.model.InventoryAdapter
import ar.uba.fi.remy.model.Product
import ar.uba.fi.remy.ui.loadingIndicator.LoadingIndicatorFragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.fragment_chango.view.*
import org.json.JSONArray
import org.json.JSONObject

class ChangoFragment : Fragment() {

    var dataList = ArrayList<HashMap<String, String>>()
    lateinit var token: String
    lateinit var adapter: InventoryAdapter
    lateinit var inventoryList: ListView
    var changuito = ""
    var items: MutableList<String> = mutableListOf<String>()
    lateinit var adapterUnits: ArrayAdapter<String>
    lateinit var dropdownIngredientes: AutoCompleteTextView
    var listProducts: MutableList<Product> = mutableListOf<Product>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_chango, container, false)

        //Obtener token
        val sharedPref = activity?.getSharedPreferences(
            getString(R.string.preference_file), Context.MODE_PRIVATE)
        token = sharedPref?.getString("TOKEN", "")!!

        adapter = InventoryAdapter(activity, dataList, 2, token)
        inventoryList = root.chango_list

        //Cambiar por llamada a la API
        changuito = "{\"changuito\": [{\"ingrediente\": \"Azucar\", \"cantidad\": \"1kg\"}, {\"ingrediente\": \"Nesquick\", \"cantidad\": \"300g\"}]}"

        obtenerChango()

        root.chango_search.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    adapter.getFilter().filter(newText)
                }
                return true
            }

        })

        root.chango_floating_add.setOnClickListener(View.OnClickListener {
            goAddManually()
        })

        return root
    }

    private fun goAddManually() {
        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_add_ingredient)

        val confirmBtn = dialog.findViewById(R.id.dialog_add_ingredient_agregar) as Button
        val cancelBtn = dialog.findViewById(R.id.dialog_add_ingredient_cancelar) as Button
        val textIngrediente = dialog.findViewById(R.id.dialog_add_ingredient_ingrediente) as TextInputLayout
        val textCantidad = dialog.findViewById(R.id.dialog_add_ingredient_cantidad) as TextInputLayout
        val textUnidad = dialog.findViewById(R.id.dialog_add_ingredient_unidad) as TextInputLayout

        cancelBtn.setBackgroundColor(Color.GRAY)
        confirmBtn.setOnClickListener {

            //Valido que esten completos los campos
            val txtIngrediente = textIngrediente.editText?.text.toString()
            val txtCantidad = textCantidad.editText?.text.toString()
            val txtUnidad = textUnidad.editText?.text.toString()

            var error = false

            if(txtCantidad.isBlank()) {
                textCantidad.error = "Indicar cantidad"
                error = true
            } else {
                textCantidad.error = ""
            }
            if(txtUnidad.isBlank()) {
                textUnidad.error = "Indicar unidad"
                error = true
            } else {
                textUnidad.error = ""
            }

            if(txtIngrediente.isBlank()) {
                textIngrediente.error = "Indicar ingrediente"
                error = true
            } /*else if(adapterIngredientes.getPosition(txtIngrediente) < 0) {
                textIngrediente.error = "Ingrediente no existente"
                error = true
            }*/ else {
                textIngrediente.error = ""
            }

            if(!error) {
                agregarIngrediente(txtIngrediente, txtCantidad, txtUnidad, "1", true)
                dialog.dismiss()
            }
        }
        cancelBtn.setOnClickListener { dialog.dismiss() }

        //Sete unidades
        adapterUnits = ArrayAdapter(activity, R.layout.list_item, items)
        val dropdown = dialog.findViewById(R.id.dialog_add_ingredient_unit_dropdown) as AutoCompleteTextView
        dropdown.setAdapter(adapterUnits)
        dropdown.keyListener = null

        //Seteo ingredientes
        /*adapterIngredientes = ArrayAdapter(activity, R.layout.list_item)*/
        dropdownIngredientes = dialog.findViewById(R.id.dialog_add_ingredient_dropdown) as AutoCompleteTextView
        /*dropdownIngredientes.setAdapter(adapterIngredientes)*/

        dropdownIngredientes.onItemClickListener = AdapterView.OnItemClickListener{
                parent,view,position,id->
            val selectedItem = parent.getItemAtPosition(position).toString()
            Log.i("API", "Selected 1: " + selectedItem)
            Log.i("API", "Selected 2: " + listProducts[position].name)
            Log.i("API", "Selected 2: " + listProducts[position].id)
            loadUnits(listProducts[position].units)
        }

        dropdownIngredientes.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.i("API", textIngrediente.editText?.text.toString())
                var numChars = s.toString().length
                if(numChars > 2) {
                    getIngredientes(textIngrediente.editText?.text.toString())
                }
            }
        })

        dialog.show()
        dialog.window?.setLayout(1000,950)
    }

    private fun loadUnits(units: JSONArray) {
        Log.i("API", "Units: " + units)
        items.clear()
        for (i in 0 until units.length()) {
            val unit = units.getJSONObject(i)
            items.add(unit.getString("name"))
        }
        adapterUnits.notifyDataSetChanged()
    }

    private fun getIngredientes(query: String) {
        val queue = Volley.newRequestQueue(activity)
        val url = "https://tpp-remy.herokuapp.com/api/v1/products/?search=" + query

        //LoadingIndicatorFragment.show(requireContext())
        val jsonObjectRequest = object: JsonObjectRequest(Request.Method.GET, url, null,
            Response.Listener { response ->
                Log.i("API", "Response: %s".format(response.toString()))
                val results = response.getJSONArray("results")
                var ingredientes = mutableListOf<String>()
                listProducts.clear()
                for(i in 0 until results.length()) {
                    val ingrediente = results.getJSONObject(i)
                    ingredientes.add(ingrediente.getString("name"))
                    listProducts.add(Product(ingrediente.getInt("id"), ingrediente.getString("name"), ingrediente.getJSONArray("available_units")))
                }
                val adapter = ArrayAdapter(context, R.layout.list_item, ingredientes)
                //LoadingIndicatorFragment.hide()
                dropdownIngredientes.setAdapter<ArrayAdapter<String>>(adapter)
                dropdownIngredientes.showDropDown()
            },
            Response.ErrorListener { error ->
                Log.e("API", "Error en GET")
                Log.e("API", "Response: %s".format(error.toString()))
                //LoadingIndicatorFragment.hide()
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

    private fun obtenerChango() {
        val queue = Volley.newRequestQueue(activity)
        val url = "https://tpp-remy.herokuapp.com/api/v1/cart/?page_size=1000"

        LoadingIndicatorFragment.show(requireContext())
        val jsonObjectRequest = object: JsonObjectRequest(Request.Method.GET, url, null,
            Response.Listener { response ->
                Log.i("API", "Response: %s".format(response.toString()))
                var chango = response.toString()
                LoadingIndicatorFragment.hide()
                cargarChango(chango)
            },
            Response.ErrorListener { error ->
                Log.e("API", "Error en GET")
                Log.e("API", "Response: %s".format(error.toString()))
                LoadingIndicatorFragment.hide()
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

    private fun cargarChango(chango: String) {
        val jsonObj = JSONObject(chango)
        val ingredientes = jsonObj.getJSONArray("results")

        dataList.clear()
        for (i in 0 until ingredientes.length()) {
            val ingrediente = ingredientes.getJSONObject(i)
            agregarIngrediente(ingrediente.getString("product"), ingrediente.getString("quantity"), ingrediente.getString("unit"), ingrediente.getInt("id").toString(), false)
        }
    }

    private fun agregarIngrediente(ingrediente: String, cantidad: String, unidad: String, id:  String, persist: Boolean) {
        inventoryList.adapter = adapter

        val map = HashMap<String, String>()
        map["id"] = id
        map["ingrediente"] = ingrediente
        map["cantidad"] = cantidad + unidad
        adapter.addData(map)

        if(persist) {
            addProductToCart(ingrediente, cantidad, unidad)
        }
    }

    private fun addProductToCart(product: String, cantidad: String, unidad: String) {
        val queue = Volley.newRequestQueue(activity)
        val url = "https://tpp-remy.herokuapp.com/api/v1/cart/"

        val body = JSONObject()
        body.put("unit", "kilogram")
        body.put("quantity", cantidad)
        body.put("product", product)
        Log.i("API", unidad)
        Log.i("API", cantidad)
        Log.i("API", product)

        LoadingIndicatorFragment.show(requireContext())
        val jsonObjectRequest = object: JsonObjectRequest(Request.Method.POST, url, body,
            Response.Listener { response ->
                Log.i("API", "Response: %s".format(response.toString()))
                LoadingIndicatorFragment.hide()
            },
            Response.ErrorListener { error ->
                Log.e("API", "Error en GET")
                Log.e("API", "Response: %s".format(error.toString()))
                LoadingIndicatorFragment.hide()
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