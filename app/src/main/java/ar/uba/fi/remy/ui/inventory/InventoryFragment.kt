package ar.uba.fi.remy.ui.inventory

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
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
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.fragment_inventory.view.*
import org.json.JSONArray
import org.json.JSONObject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class InventoryFragment : Fragment() {

    var dataList = ArrayList<HashMap<String, String>>()
    var inventario = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_inventory, container, false)

        //Cambiar por llamada a la API
        inventario = "{\"inventario\": [{\"ingrediente\": \"Pan\", \"cantidad\": \"1kg\"}, {\"ingrediente\": \"Queso\", \"cantidad\": \"300g\"}]}"

        cargarInventario(inventario, root.inventory_list)

        root.inventory_search.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val jsonObj = JSONObject(inventario)
                val ingredientes = jsonObj.getJSONArray("inventario")
                val ingredientesFiltrados = JSONArray()

                for (i in 0 until ingredientes.length()) {
                    val ingrediente = ingredientes.getJSONObject(i)
                    if(ingrediente.getString("ingrediente").toUpperCase().startsWith(newText.toString().toUpperCase())){
                        ingredientesFiltrados.put(ingrediente)
                    }
                }

                cargarInventario("{\"inventario\": " + ingredientesFiltrados.toString() + "}", root.inventory_list)
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

    private fun goAddManually() {
        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_add_ingredient)
        /*val body = dialog.findViewById(R.id.body) as TextView
        body.text = title*/
        val confirmBtn = dialog.findViewById(R.id.dialog_add_ingredient_agregar) as Button
        val cancelBtn = dialog.findViewById(R.id.dialog_add_ingredient_cancelar) as Button
        cancelBtn.setBackgroundColor(Color.GRAY)
        confirmBtn.setOnClickListener {
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
                    Log.i("API", result.contents)
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    private fun cargarInventario(inventario: String, inventoryList: ListView) {
        val jsonObj = JSONObject(inventario)
        val ingredientes = jsonObj.getJSONArray("inventario")

        dataList.clear()
        for (i in 0 until ingredientes.length()) {
            val ingrediente = ingredientes.getJSONObject(i)

            val map = HashMap<String, String>()
            map["ingrediente"] = ingrediente.getString("ingrediente")
            map["cantidad"] = ingrediente.getString("cantidad")
            dataList.add(map)
        }

        val adapter = InventoryAdapter(activity, dataList);
        inventoryList.adapter = adapter

        val map = HashMap<String, String>()
        map["ingrediente"] = "ingrediente"
        map["cantidad"] = "cantidad"
        adapter.addData(map)
    }



/*    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.inventory_menu, menu)
        val searchView = menu.findItem(R.id.inventory_menu_search).actionView as SearchView
        searchView.queryHint = "Ingrese el ingrediente a buscar..."

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val jsonObj = JSONObject(inventario)
                val ingredientes = jsonObj.getJSONArray("inventario")
                val ingredientesFiltrados = JSONArray()

                for (i in 0 until ingredientes.length()) {
                    val ingrediente = ingredientes.getJSONObject(i)
                    if(ingrediente.getString("ingrediente").toUpperCase().startsWith(newText.toString().toUpperCase())){
                        ingredientesFiltrados.put(ingrediente)
                    }
                }

                cargarInventario("{\"inventario\": " + ingredientesFiltrados.toString() + "}")
                return true
            }

        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.inventory_menu_search -> {
                Log.i("API", "Click Search")
            }
        }
        return super.onOptionsItemSelected(item)
    }*/
}