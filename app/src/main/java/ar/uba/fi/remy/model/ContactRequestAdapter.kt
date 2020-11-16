package ar.uba.fi.remy.model


import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import ar.uba.fi.remy.ContactsActivity
import ar.uba.fi.remy.R
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject


class ContactRequestAdapter(private val context: Activity?, private var dataList: ArrayList<HashMap<String, String>>, private var token: String) : BaseAdapter() {

    private val inflater: LayoutInflater = this.context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    override fun getCount(): Int { return dataList.size }
    override fun getItem(position: Int): Int { return position }
    override fun getItemId(position: Int): Long { return position.toLong() }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var dataitem = dataList[position]

        val rowView = inflater.inflate(R.layout.contact_request_item, parent, false)
        rowView.findViewById<TextView>(R.id.contact_name).text = dataitem.get("name")
        rowView.findViewById<TextView>(R.id.contact_username).text = dataitem.get("username")
        rowView.findViewById<TextView>(R.id.contact_email).text = dataitem.get("email")

        val btnAccept = rowView.findViewById<ImageButton>(R.id.contact_accept)
        val btnDecline = rowView.findViewById<ImageButton>(R.id.contact_decline)

        btnAccept.setOnClickListener {
            Log.i("API", "Acepta solicitud " + dataitem.get("idRequest"))
            handleRequest(dataitem.get("idRequest"), true)
        }

        btnDecline.setOnClickListener {
            Log.i("API","Rechaza solicitud "  + dataitem.get("idRequest"))
            handleRequest(dataitem.get("idRequest"), false)
        }

        rowView.tag = position
        return rowView
    }

    private fun handleRequest(idRequest: String?, accept:  Boolean) {
        Log.i("API", "TOKEN adapter: " + token)

        val type = if(accept) "/accept/" else "/reject/"
        val queue = Volley.newRequestQueue(context)
        val url = "https://tpp-remy.herokuapp.com/api/v1/friendship/" + idRequest + type

        val jsonObjectRequest = object: JsonObjectRequest(
            Request.Method.POST, url, null,
            Response.Listener { response ->
                Log.i("API", "Response: %s".format(response.toString()))
                val activity = context as ContactsActivity
                activity.cargarInvites()
                activity.cargarContactos()
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

    fun addData(data: HashMap<String, String>) {
        dataList.add(data)
        notifyDataSetChanged()
    }
}