package ar.uba.fi.remy.ui.inventory

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import ar.uba.fi.remy.R
import ar.uba.fi.remy.model.InventoryAdapter
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

class InventoryFragment : Fragment() {

    var dataList = ArrayList<HashMap<String, String>>()
    var inventario = ""
    private lateinit var dialogLoading: Dialog


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_inventory, container, false)

        showLoading()
        obtenerInventario(root.inventory_list)

        root.inventory_search.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val jsonObj = JSONObject(inventario)
                val ingredientes = jsonObj.getJSONArray("results")
                val ingredientesFiltrados = JSONArray()

                for (i in 0 until ingredientes.length()) {
                    val ingrediente = ingredientes.getJSONObject(i)
                    if(ingrediente.getString("product").toUpperCase().startsWith(newText.toString().toUpperCase())){
                        ingredientesFiltrados.put(ingrediente)
                    }
                }

                cargarInventario(root.inventory_list, "{\"results\": " + ingredientesFiltrados.toString() + "}")
                return true
            }

        })

        root.inventory_floating_scan.setOnClickListener(View.OnClickListener {
            goScanner()
        })

        root.inventory_floating_add.setOnClickListener(View.OnClickListener {
            goAddManually(root.inventory_list)
        })

        return root
    }

    private fun showLoading() {
        dialogLoading = Dialog(activity)
        dialogLoading.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogLoading.setCancelable(false)
        dialogLoading.setContentView(R.layout.custom_loading)
        dialogLoading.show()
    }

    private fun obtenerInventario(inventoryList: ListView) {
        //Access sharedPreferences
        val sharedPref = activity?.getSharedPreferences(
            getString(R.string.preference_file), Context.MODE_PRIVATE)
        val token = sharedPref?.getString("TOKEN", "")

        val queue = Volley.newRequestQueue(activity)
        val url = "https://tpp-remy.herokuapp.com/api/v1/inventoryitems/"

        val jsonObjectRequest = object: JsonObjectRequest(Request.Method.GET, url, null,
            Response.Listener { response ->
                Log.i("API", "Response: %s".format(response.toString()))
                inventario = response.toString()
                cargarInventario(inventoryList, null)
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

    private fun goAddManually(inventoryList: ListView) {
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
            agregarIngrediente(textIngrediente.editText?.text.toString(), textCantidad.editText?.text.toString(), textUnidad.editText?.text.toString(), inventoryList)
            dialog.dismiss()
        }
        cancelBtn.setOnClickListener { dialog.dismiss() }
        val items = listOf("g", "kg", "u", "l")
        val adapter = ArrayAdapter(activity, R.layout.list_item, items)
        val dropdown = dialog.findViewById(R.id.dialog_add_ingredient_dropdown) as AutoCompleteTextView
        dropdown.setAdapter(adapter)
        dropdown.keyListener = null
        dialog.show()
        dialog.window.setLayout(1000,900)
    }

    private fun goScanner() {
        val scanner = IntentIntegrator.forSupportFragment(this)
        scanner.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == Activity.RESULT_OK){
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if (result != null) {
                if (result.contents == null) {
                    Toast.makeText(activity, "Cancelled", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(activity, "Scanned: " + result.contents, Toast.LENGTH_LONG).show()
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    private fun cargarInventario(inventoryList: ListView, inventarioFiltrado: String?) {
        var jsonObj = JSONObject()
        if(inventarioFiltrado !== null) {
            jsonObj = JSONObject(inventarioFiltrado)
        } else {
            jsonObj = JSONObject(inventario)
        }
        val ingredientes = jsonObj.getJSONArray("results")

        dataList.clear()
        for (i in 0 until ingredientes.length()) {
            val ingrediente = ingredientes.getJSONObject(i)

            agregarIngrediente(ingrediente.getString("product"), ingrediente.getJSONObject("amount").getString("quantity"), ingrediente.getJSONObject("amount").getString("unit"), inventoryList)
        }

        dialogLoading.dismiss()
    }

    private fun agregarIngrediente(ingrediente: String, cantidad: String, unidad: String, inventoryList: ListView) {
        val adapter = InventoryAdapter(activity, dataList)
        inventoryList.adapter = adapter

        val map = HashMap<String, String>()
        map["ingrediente"] = ingrediente
        map["cantidad"] = cantidad + unidad
        adapter.addData(map)
    }

}