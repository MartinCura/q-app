package ar.uba.fi.remy.ui.chango

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import ar.uba.fi.remy.R
import ar.uba.fi.remy.model.InventoryAdapter
import kotlinx.android.synthetic.main.fragment_chango.view.*
import kotlinx.android.synthetic.main.fragment_inventory.*
import kotlinx.android.synthetic.main.fragment_inventory.view.*
import org.json.JSONArray
import org.json.JSONObject

class ChangoFragment : Fragment() {

    var dataList = ArrayList<HashMap<String, String>>()
    var changuito = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_chango, container, false)

        //Cambiar por llamada a la API
        changuito = "{\"changuito\": [{\"ingrediente\": \"Azucar\", \"cantidad\": \"1kg\"}, {\"ingrediente\": \"Nesquick\", \"cantidad\": \"300g\"}]}"

        cargarChango(changuito, root.chango_list)

        root.chango_search.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val jsonObj = JSONObject(changuito)
                val ingredientes = jsonObj.getJSONArray("changuito")
                val ingredientesFiltrados = JSONArray()

                for (i in 0 until ingredientes.length()) {
                    val ingrediente = ingredientes.getJSONObject(i)
                    if(ingrediente.getString("ingrediente").toUpperCase().startsWith(newText.toString().toUpperCase())){
                        ingredientesFiltrados.put(ingrediente)
                    }
                }

                cargarChango("{\"changuito\": " + ingredientesFiltrados.toString() + "}", root.chango_list)
                return true
            }

        })

        return root
    }

    private fun cargarChango(inventario: String, changoList: ListView) {
        val jsonObj = JSONObject(inventario)
        val ingredientes = jsonObj.getJSONArray("changuito")

        dataList.clear()
        for (i in 0 until ingredientes.length()) {
            val ingrediente = ingredientes.getJSONObject(i)

            val map = HashMap<String, String>()
            map["ingrediente"] = ingrediente.getString("ingrediente")
            map["cantidad"] = ingrediente.getString("cantidad")
            dataList.add(map)
        }

        changoList.adapter = InventoryAdapter(activity, dataList)
    }
}