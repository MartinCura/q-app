package ar.uba.fi.remy

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ar.uba.fi.remy.model.EventItem
import ar.uba.fi.remy.model.EventItemAdapter
import ar.uba.fi.remy.ui.loadingIndicator.LoadingIndicatorFragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_events.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class EventsActivity : AppCompatActivity() {

    lateinit var token: String

    override fun onResume() {
        super.onResume()
        cargarEventos()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events)

        // Hide the status bar.
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        actionBar?.hide()

        //Obtener token
        val sharedPref = this.getSharedPreferences(
            getString(R.string.preference_file), Context.MODE_PRIVATE)
        token = sharedPref?.getString("TOKEN", "")!!

        configAddBtn()
    }

    private fun configAddBtn() {
        inventory_floating_add.setOnClickListener {
            val intent = Intent(this, NewEventActivity::class.java)
            startActivity(intent)
        }
    }

    private fun cargarEventos() {
        val recyclerView: RecyclerView = rv_eventos
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        val eventos = ArrayList<EventItem>()

        val queue = Volley.newRequestQueue(this)
        val url = "https://tpp-remy.herokuapp.com/api/v1/events/"

        LoadingIndicatorFragment.show(this)
        val jsonObjectRequest = object: JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                Log.i("API", "Response: $response")
                val eventosArray = response.getJSONArray("results")
                for (i in 0 until eventosArray.length()) {
                    var evento = eventosArray.getJSONObject(i)
                    var fecha = evento.getString("starting_datetime")
                    eventos.add(EventItem(evento.getInt("id"), evento.getString("name"), evento.getJSONArray("attendees").length(), 10, formatDate(fecha)))
                }
                LoadingIndicatorFragment.hide()
                val adapter = EventItemAdapter(eventos)
                recyclerView.adapter = adapter
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

    private fun formatDate(fecha: String?): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale("es", "ES"))
        val outputFormat = SimpleDateFormat("EEEE dd-MM-yyyy", Locale("es", "ES"))
        val date = inputFormat.parse(fecha)
        val formattedDate = outputFormat.format(date)
        return formattedDate.capitalize()
    }
}
