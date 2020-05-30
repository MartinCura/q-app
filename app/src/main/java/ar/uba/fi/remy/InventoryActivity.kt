package ar.uba.fi.remy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import ar.uba.fi.remy.model.InventoryAdapter
import org.json.JSONObject

class InventoryActivity : AppCompatActivity() {

    var dataList = ArrayList<HashMap<String, String>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory)

        cargarInventario()
    }

    private fun cargarInventario() {
        // TO-DO: request a la API

        val result = "{\"inventario\": [{\"ingrediente\": \"Pan\", \"cantidad\": \"1kg\"}, {\"ingrediente\": \"Queso\", \"cantidad\": \"300g\"}]}"
        val jsonObj = JSONObject(result)
        val ingredientes = jsonObj.getJSONArray("inventario")
        Log.i("API", "Lista: %s".format(ingredientes.toString()))
        for (i in 0 until ingredientes.length()) {
            val ingrediente = ingredientes.getJSONObject(i)

            val map = HashMap<String, String>()
            map["ingrediente"] = ingrediente.getString("ingrediente")
            map["cantidad"] = ingrediente.getString("cantidad")
            dataList.add(map)
        }
        Log.i("API", "Lista: %s".format(dataList.toString()))
        findViewById<ListView>(R.id.inventory_list).adapter = InventoryAdapter(this@InventoryActivity, dataList)
    }
}
