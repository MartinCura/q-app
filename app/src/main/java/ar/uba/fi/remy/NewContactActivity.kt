package ar.uba.fi.remy

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.SearchView
import ar.uba.fi.remy.model.ContactAdapter
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_new_contact.*
import org.json.JSONObject

class NewContactActivity : AppCompatActivity() {

    lateinit var token: String
    var users = ArrayList<HashMap<String, String>>()
    lateinit var adapter: ContactAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_contact)

        //Obtener token
        val sharedPref = this.getSharedPreferences(
            getString(R.string.preference_file), Context.MODE_PRIVATE)
        token = sharedPref?.getString("TOKEN", "")!!

        //Configuro adapter
        adapter = ContactAdapter(this, users)
        new_contact_list.adapter = adapter

        configSearch()
    }

    private fun configSearch() {
        new_contact_btn_search.setOnClickListener {
            var txtContact = new_contact_edit_text.text.toString()
            Log.i("API", txtContact)
            if(txtContact.length > 2) {
                loadContacts(txtContact)
            }
        }
    }

    private fun loadContacts(query: String) {
        val queue = Volley.newRequestQueue(this)
        val url = "https://tpp-remy.herokuapp.com/api/v1/profiles/?search=" + query

        users.clear()
        adapter.notifyDataSetChanged()
        val jsonObjectRequest = object: JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                Log.i("API", "Response: %s".format(response.toString()))
                var results = response.getJSONArray("results")
                for (i in 0 until results.length()) {
                    val user = results.getJSONObject(i)
                    agregarUsuario(user)
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

        queue.add(jsonObjectRequest)
    }

    private fun agregarUsuario(user: JSONObject) {
        val map = HashMap<String, String>()

        map["idRequest"] = user.getInt("id").toString()
        map["name"] = user.getString("first_name") + " " + user.getString("last_name")
        map["username"] = user.getString("username")
        map["email"] = user.getString("email")

        users.add(map)
        adapter.notifyDataSetChanged()
    }
}
