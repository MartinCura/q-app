package ar.uba.fi.remy

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import ar.uba.fi.remy.model.ContactAdapter
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_contacts.*
import org.json.JSONObject

class ContactsActivity : AppCompatActivity() {

    var dataList = ArrayList<HashMap<String, String>>()
    lateinit var token: String
    lateinit var adapter: ContactAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)

        //Obtener token
        val sharedPref = this.getSharedPreferences(
            getString(R.string.preference_file), Context.MODE_PRIVATE)
        token = sharedPref?.getString("TOKEN", "")!!

        //Configuro adapter
        adapter = ContactAdapter(this, dataList)
        contact_list.adapter = adapter

        cargarContactos()
    }

    private fun cargarContactos() {
        val queue = Volley.newRequestQueue(this)
        val url = "https://tpp-remy.herokuapp.com/api/v1/profiles/friends/"

        dataList.clear()
        val jsonArrayRequest = object: JsonArrayRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                Log.i("API", "Response: %s".format(response.toString()))
                /*contactos = response.toString()*/
                for (i in 0 until response.length()) {
                    val contacto = response.getJSONObject(i)
                    agregarContacto(contacto)
                }
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

        queue.add(jsonArrayRequest)
    }

    private fun agregarContacto(contacto: JSONObject?) {
        val map = HashMap<String, String>()
        if (contacto != null) {
            map["name"] = contacto.getString("first_name") + " " + contacto.getString("last_name")
            map["username"] = contacto.getString("username")
            map["email"] = contacto.getString("email")
        }

        adapter.addData(map)
    }
}
