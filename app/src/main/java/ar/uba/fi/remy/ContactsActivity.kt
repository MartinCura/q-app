package ar.uba.fi.remy

import android.content.Context
import android.content.Intent
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
import android.view.MotionEvent
import android.view.View
import ar.uba.fi.remy.model.ContactRequestAdapter
import ar.uba.fi.remy.ui.loadingIndicator.LoadingIndicatorFragment
import com.android.volley.toolbox.JsonObjectRequest


class ContactsActivity : AppCompatActivity() {

    var dataList = ArrayList<HashMap<String, String>>()
    var pendingInvites = ArrayList<HashMap<String, String>>()
    lateinit var token: String
    lateinit var adapter: ContactAdapter
    lateinit var adapterInvites: ContactRequestAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)

        // Hide the status bar.
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        actionBar?.hide()

        //Obtener token
        val sharedPref = this.getSharedPreferences(
            getString(R.string.preference_file), Context.MODE_PRIVATE)
        token = sharedPref?.getString("TOKEN", "")!!

        //Configuro adapter
        adapter = ContactAdapter(this, dataList, token)
        contact_list.adapter = adapter

        //Configuro adapter invites
        adapterInvites = ContactRequestAdapter(this, pendingInvites, token)
        contact_pending_invites.adapter = adapterInvites

        //Configuro filtro de contactos
        setFilter()

        LoadingIndicatorFragment.show(this)
        cargarContactos()

        cargarInvites()


        configHideInvites()

        configAddFriend()
    }

    private fun configAddFriend() {
        contact_floating_add.setOnClickListener {
            val intent = Intent(this, NewContactActivity::class.java)
            startActivity(intent)
        }
    }

    private fun configHideInvites() {
        contact_title_invites.setOnTouchListener { _, motionEvent ->
            if(motionEvent.action == MotionEvent.ACTION_DOWN) {
                if(contact_pending_invites.visibility == View.VISIBLE) {
                    contact_pending_invites.visibility = View.GONE
                } else {
                    contact_pending_invites.visibility = View.VISIBLE
                }
            }
            true
        }
    }

    public fun cargarInvites() {
        val queue = Volley.newRequestQueue(this)
        val url = "https://tpp-remy.herokuapp.com/api/v1/friendship/"

        pendingInvites.clear()
        adapterInvites.notifyDataSetChanged()

        val jsonObjectRequest = object: JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                Log.i("API", "Response: %s".format(response.toString()))
                var results = response.getJSONArray("results")
                for (i in 0 until results.length()) {
                    val solicitud = results.getJSONObject(i)
                    if(solicitud.getString("status") == "REQUESTED") {
                        agregarSolicitud(solicitud)
                    }

                }
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

    private fun agregarSolicitud(solicitud: JSONObject) {
        val map = HashMap<String, String>()

        map["idRequest"] = solicitud.getInt("id").toString()
        val contact = solicitud.getJSONObject("profile_requesting")
        map["name"] = contact.getString("first_name") + " " + contact.getString("last_name")
        map["username"] = contact.getString("username")
        map["email"] = contact.getString("email")

        pendingInvites.add(map)
        adapterInvites.notifyDataSetChanged()
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

    public fun cargarContactos() {
        val queue = Volley.newRequestQueue(this)
        val url = "https://tpp-remy.herokuapp.com/api/v1/profiles/friends/"

        dataList.clear()
        adapter.notifyDataSetChanged()

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

        dataList.add(map)
        adapter.notifyDataSetChanged()

    }
}
