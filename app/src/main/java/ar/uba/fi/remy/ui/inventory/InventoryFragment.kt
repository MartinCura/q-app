package ar.uba.fi.remy.ui.inventory

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
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
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.fragment_inventory.view.*
import org.json.JSONArray
import org.json.JSONObject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class InventoryFragment : Fragment() {

    var dataList = ArrayList<HashMap<String, String>>()

    lateinit var inventoryList: ListView
    lateinit var token: String
    lateinit var adapter: InventoryAdapter
    /*lateinit var adapterIngredientes: ArrayAdapter<String>*/
    lateinit var dropdownIngredientes: AutoCompleteTextView
    var listProducts: MutableList<Product> = mutableListOf<Product>()
    var items: MutableList<String> = mutableListOf<String>()
    lateinit var adapterUnits: ArrayAdapter<String>
    var idManual: Int = -1

    override fun onResume() {
        super.onResume()
        obtenerInventario()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_inventory, container, false)

        //Obtener token
        val sharedPref = activity?.getSharedPreferences(
            getString(R.string.preference_file), Context.MODE_PRIVATE)
        token = sharedPref?.getString("TOKEN", "")!!

        adapter = InventoryAdapter(activity, dataList, 1, token)

        /*showLoading()*/
        inventoryList = root.inventory_list
        /*obtenerInventario()*/

        root.inventory_search.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
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

        root.inventory_floating_scan.setOnClickListener(View.OnClickListener {
            goScanner()
        })

        root.inventory_floating_add.setOnClickListener(View.OnClickListener {
            goAddManually()
        })

        return root
    }

    private fun obtenerInventario() {
        val queue = Volley.newRequestQueue(activity)
        val url = "https://tpp-remy.herokuapp.com/api/v1/inventoryitems/?page_size=200"

        if(LoadingIndicatorFragment.isShowing()) {
            LoadingIndicatorFragment.hide()
        }

        LoadingIndicatorFragment.show(requireContext())
        val jsonObjectRequest = object: JsonObjectRequest(Request.Method.GET, url, null,
            Response.Listener { response ->
                Log.i("API", "Response: %s".format(response.toString()))
                var inventario = response.toString()
                cargarInventario(inventario)
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
    private fun loadNewProducts(ingredientes: JSONArray) {
        val body = JSONObject()
        body.put("items",ingredientes)

        Log.i("API", "Nuevos productos: %s".format(body.toString()))

        val queue = Volley.newRequestQueue(activity)
        val url = "https://tpp-remy.herokuapp.com/api/v1/inventoryitems/add_items/"

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

    private fun getQR(url: String) {
        val queue = Volley.newRequestQueue(activity)

        LoadingIndicatorFragment.show(requireContext())
        val jsonObjectRequest = object: JsonObjectRequest(Request.Method.GET, url, null,
            Response.Listener { response ->
                Log.i("API", "Response: %s".format(response.toString()))

                val intent = Intent(context, InventoryConfirmActivity::class.java)
                intent.putExtra("Ingredients", response.toString())
                LoadingIndicatorFragment.hide()
                startActivity(intent)

                /*val jsonObj = JSONObject(response.toString())
                val ingredientes = jsonObj.getJSONArray("items")
                val url = jsonObj.getString("url")

                loadNewProducts(ingredientes)

                for (i in 0 until ingredientes.length()) {
                    val ingrediente = ingredientes.getJSONObject(i)

                    agregarIngrediente(ingrediente.getString("product"), ingrediente.getString("quantity"), ingrediente.getString("unit"))
                }*/
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

    private fun getBarCode(codebar: String) {
        val queue = Volley.newRequestQueue(activity)
        val url = "https://tpp-remy.herokuapp.com/api/v1/barcode/" + codebar + "/add_item/"

        LoadingIndicatorFragment.show(requireContext())
        val jsonObjectRequest = object: JsonObjectRequest(Request.Method.POST, url, null,
            Response.Listener { response ->
                Log.i("API", "Response: %s".format(response.toString()))
                LoadingIndicatorFragment.hide()
            },
            Response.ErrorListener { error ->
                Log.e("API", "Error en POST")
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
                agregarIngrediente(txtIngrediente, txtCantidad, txtUnidad, idManual.toString(), true)
                dialog.dismiss()
            }
        }
        cancelBtn.setOnClickListener { dialog.dismiss() }
        //Sete unidades
        items = mutableListOf<String>("g", "kg", "u", "l")
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
            idManual = listProducts[position].id
            loadUnits(listProducts[position].units)
        }

        dropdownIngredientes.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                /*Log.i("API", count.toString())
                Log.i("API", textIngrediente.editText?.text.toString())*/
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

    private fun goScanner() {
        val scanner = IntentIntegrator.forSupportFragment(this)
        scanner.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(LoadingIndicatorFragment.isShowing()) {
            LoadingIndicatorFragment.hide()
        }
        if(resultCode == Activity.RESULT_OK){
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if (result != null) {
                if (result.contents == null) {
                    Toast.makeText(activity, "Cancelled", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(activity, "Scanned: " + result.contents, Toast.LENGTH_LONG).show()
                    Log.e("API", "Scanned: " + result.contents)
                    if(result.contents.startsWith("http")) {
                        Log.i("API", "QR")
                        val urlQR = result.contents.toString().replace("http", "https")
                        getQR(urlQR)
                    } else {
                        Log.e("API", "BARRAS")
                        getBarCode(result.contents.toString())
                    }
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    private fun cargarInventario(inventario: String) {
        var jsonObj = JSONObject(inventario)
        val ingredientes = jsonObj.getJSONArray("results")

        dataList.clear()
        for (i in 0 until ingredientes.length()) {
            val ingrediente = ingredientes.getJSONObject(i)

            agregarIngrediente(ingrediente.getString("product"), ingrediente.getString("quantity"), ingrediente.getString("unit"), ingrediente.getInt("id").toString(),false)
        }
    }

    private fun agregarIngrediente(
        ingrediente: String,
        cantidad: String,
        unidad: String,
        id: String,
        persist: Boolean
    ) {
        inventoryList.adapter = adapter

        val map = HashMap<String, String>()
        map["id"] = id
        map["ingrediente"] = ingrediente
        map["cantidad"] = "$cantidad $unidad"
        adapter.addData(map)

        if(persist) {
            addProductToInventory(ingrediente, cantidad, unidad)
        }
    }

    private fun addProductToInventory(product: String, cantidad: String, unidad: String) {
        val queue = Volley.newRequestQueue(activity)
        val url = "https://tpp-remy.herokuapp.com/api/v1/inventoryitems/"

        val body = JSONObject()
        body.put("unit", unidad)
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
