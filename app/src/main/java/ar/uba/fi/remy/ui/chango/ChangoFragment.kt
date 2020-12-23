package ar.uba.fi.remy.ui.chango

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import ar.uba.fi.remy.R
import ar.uba.fi.remy.model.InventoryAdapter
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.fragment_chango.view.*
import org.json.JSONArray
import org.json.JSONObject

class ChangoFragment : Fragment() {

    var dataList = ArrayList<HashMap<String, String>>()
    lateinit var token: String
    lateinit var adapter: InventoryAdapter
    lateinit var inventoryList: ListView
    var changuito = ""

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

        adapter = InventoryAdapter(activity, dataList)
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

        return root
    }

    private fun obtenerChango() {
        val queue = Volley.newRequestQueue(activity)
        val url = "https://tpp-remy.herokuapp.com/api/v1/cart/"

        val jsonObjectRequest = object: JsonObjectRequest(Request.Method.GET, url, null,
            Response.Listener { response ->
                Log.i("API", "Response: %s".format(response.toString()))
                var chango = response.toString()
                cargarChango(chango)
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

    private fun cargarChango(chango: String) {
        val jsonObj = JSONObject(chango)
        val ingredientes = jsonObj.getJSONArray("results")

        dataList.clear()
        for (i in 0 until ingredientes.length()) {
            val ingrediente = ingredientes.getJSONObject(i)
            agregarIngrediente(ingrediente.getString("product"), ingrediente.getString("quantity"), ingrediente.getString("unit"))
        }
    }

    private fun agregarIngrediente(ingrediente: String, cantidad: String, unidad: String) {
        inventoryList.adapter = adapter

        val map = HashMap<String, String>()
        map["ingrediente"] = ingrediente
        map["cantidad"] = cantidad + unidad
        adapter.addData(map)
    }
}