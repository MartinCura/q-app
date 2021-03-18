package ar.uba.fi.remy.ui.inventory

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ListView
import ar.uba.fi.remy.R
import ar.uba.fi.remy.model.ConfirmItemAdapter
import ar.uba.fi.remy.ui.loadingIndicator.LoadingIndicatorFragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_inventory_confirm.*
import org.json.JSONArray
import org.json.JSONObject

class InventoryConfirmActivity : AppCompatActivity() {
    var dataList = ArrayList<HashMap<String, String>>()
    lateinit var token: String
    lateinit var adapter: ConfirmItemAdapter
    lateinit var inventoryList: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory_confirm)

        //Obtener token
        val sharedPref = this.getSharedPreferences(
            getString(R.string.preference_file), Context.MODE_PRIVATE)
        token = sharedPref?.getString("TOKEN", "")!!

        adapter = ConfirmItemAdapter(this, dataList)

        inventoryList = inventory_list_confirm

        Log.i("API", "Nueva screen: " + intent.getStringExtra("Ingredients"))

        var jsonObj = JSONObject(intent.getStringExtra("Ingredients"))
        val ingredientes = jsonObj.getJSONArray("items")
        Log.i("API", "Nueva screen: " + ingredientes)

        dataList.clear()
        for (i in 0 until ingredientes.length()) {
            val ingrediente = ingredientes.getJSONObject(i)
            agregarIngrediente(ingrediente.getString("product"), ingrediente.getString("quantity"), ingrediente.getString("unit"))
        }

        btn_confirm_add_inventory.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                loadNewProducts(dataList)
            }})
    }

    private fun loadNewProducts(ingredientes: ArrayList<HashMap<String, String>>) {
        val body = JSONObject()
        val jsonArrayIngredientes = JSONArray(ingredientes)

        body.put("items",jsonArrayIngredientes)

        Log.i("API", "Nuevos productos: %s".format(body.toString()))

        val queue = Volley.newRequestQueue(this)
        val url = "https://tpp-remy.herokuapp.com/api/v1/inventoryitems/add_items/"

        LoadingIndicatorFragment.show(this)
        val jsonObjectRequest = object: JsonObjectRequest(
            Request.Method.POST, url, body,
            Response.Listener { response ->
                Log.i("API", "Response: %s".format(response.toString()))
                finish()
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

    private fun agregarIngrediente(ingrediente: String, cantidad: String, unidad: String) {
        inventoryList.adapter = adapter

        val map = HashMap<String, String>()
        map["product"] = ingrediente
        map["quantity"] = cantidad
        map["unit"] = unidad
        adapter.addData(map)
    }
}
