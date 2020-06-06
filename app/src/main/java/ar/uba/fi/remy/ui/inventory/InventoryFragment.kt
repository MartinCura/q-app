package ar.uba.fi.remy.ui.inventory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import ar.uba.fi.remy.R
import ar.uba.fi.remy.model.InventoryAdapter
import kotlinx.android.synthetic.main.fragment_inventory.*
import kotlinx.android.synthetic.main.fragment_inventory.view.*
import org.json.JSONArray
import org.json.JSONObject

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

        return root
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

        inventoryList.adapter = InventoryAdapter(activity, dataList)
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