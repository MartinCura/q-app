package ar.uba.fi.remy

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_detail_event.*
import org.json.JSONArray
import org.json.JSONObject

class DetailEventActivity : AppCompatActivity() {
    lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_event)

        //Obtener token
        val sharedPref = this?.getSharedPreferences(
            getString(R.string.preference_file), Context.MODE_PRIVATE)
        token = sharedPref?.getString("TOKEN", "")!!

        val idEvento= intent.getIntExtra("id_evento", 0)
        Log.i("API", "ID Evento: " + idEvento)

        event_add_person_btn.setOnClickListener(View.OnClickListener {
          addPerson(idEvento)
        })
    }

    private fun addPerson(idEvento: Int) {
        val queue = Volley.newRequestQueue(this)
        //TO-DO: Agregar Dialog o algo para seleccionar al invitado a agregar
        val url = "https://tpp-remy.herokuapp.com/api/v1/events/" + idEvento + "/add_attendee/?attendee_id=" + "10"

        val jsonObjectRequest = object: JsonObjectRequest(
            Request.Method.POST, url, null,
            Response.Listener { response ->
                Log.i("API", "Response: %s".format(response.toString()))
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
}
