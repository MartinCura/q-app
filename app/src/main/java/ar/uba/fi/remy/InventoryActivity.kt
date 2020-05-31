package ar.uba.fi.remy

import android.database.MatrixCursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import androidx.appcompat.widget.SearchView
import ar.uba.fi.remy.model.InventoryAdapter
import org.json.JSONArray
import org.json.JSONObject

class InventoryActivity : AppCompatActivity() {

    var dataList = ArrayList<HashMap<String, String>>()
    var inventario = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory)

        //Cambiar por llamada a la API
        inventario = "{\"inventario\": [{\"ingrediente\": \"Pan\", \"cantidad\": \"1kg\"}, {\"ingrediente\": \"Queso\", \"cantidad\": \"300g\"}]}"

        cargarInventario(inventario)
    }

    private fun cargarInventario(inventario: String) {
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

        findViewById<ListView>(R.id.inventory_list).adapter = InventoryAdapter(this@InventoryActivity, dataList)
    }



    override fun onCreateOptionsMenu(menu: Menu): Boolean {
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
    }
}
