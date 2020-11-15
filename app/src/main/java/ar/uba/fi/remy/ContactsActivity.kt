package ar.uba.fi.remy

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.SearchView
import ar.uba.fi.remy.model.ContactAdapter
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_contacts.*
import org.json.JSONObject

class ContactsActivity : AppCompatActivity() {

    var dataList = ArrayList<HashMap<String, String>>()
    var pendingInvites = ArrayList<HashMap<String, String>>()
    lateinit var token: String
    lateinit var adapter: ContactAdapter
    lateinit var adapterInvites: ContactAdapter

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

        //Configuro adapter invites
        adapterInvites = ContactAdapter(this, pendingInvites)
        contact_pending_invites.adapter = adapterInvites

        //Configuro filtro de contactos
        setFilter()

        cargarContactos()

        cargarInvites()
    }

    private fun cargarInvites() {
        val map = HashMap<String, String>()

        map["name"] = "Prueba"
        map["username"] = "username"
        map["email"] = "mail@a.com"

        adapterInvites.addData(map)
    }

    private fun setFilter() {
        contact_search.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
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

        val map2 = HashMap<String, String>()
        map2["name"] = "Martin Cura"
        map2["username"] = "Martin"
        map2["email"] = "martin@mail.com"
        adapter.addData(map2)

    }
}
