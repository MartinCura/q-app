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
import ar.uba.fi.remy.ui.loadingIndicator.LoadingIndicatorFragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject


class ContactAdapter(private val context: Activity?, private var dataList: ArrayList<HashMap<String, String>>, private var token: String) : BaseAdapter(), Filterable {

    private val inflater: LayoutInflater = this.context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    override fun getCount(): Int { return dataList.size }
    override fun getItem(position: Int): Int { return position }
    override fun getItemId(position: Int): Long { return position.toLong() }
    private val mFilter = ItemFilter()
    private var originalData : List<HashMap<String, String>> = dataList

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var dataitem = dataList[position]

        var rowView: View
        rowView = if(context.toString().contains("ar.uba.fi.remy.NewContactActivity")) {
            inflater.inflate(R.layout.contact_add_item, parent, false)
        } else {
            inflater.inflate(R.layout.contact_item, parent, false)
        }

        rowView.findViewById<TextView>(R.id.contact_name).text = dataitem.get("name")
        rowView.findViewById<TextView>(R.id.contact_username).text = dataitem.get("username")
        rowView.findViewById<TextView>(R.id.contact_email).text = dataitem.get("email")

        if(context.toString().contains("ar.uba.fi.remy.NewContactActivity")) {
            val contactadd = rowView.findViewById<ImageButton>(R.id.contact_add)
            if(contactadd != null) {
                contactadd.setOnClickListener {
                    requestFriendship(dataitem.get("id"))
                }
            }
        }

        rowView.tag = position
        return rowView
    }

    private fun requestFriendship(id: String?) {
        val queue = Volley.newRequestQueue(context)
        val url = "https://tpp-remy.herokuapp.com/api/v1/friendship/"

        val body = JSONObject()
        body.put("profile_requested_id", id?.toInt())

        val jsonObjectRequest = object: JsonObjectRequest(
            Request.Method.POST, url, body,
            Response.Listener { response ->
                Log.i("API", "Response: %s".format(response.toString()))
                Toast.makeText(this.context, "Solicitud de amistad enviada", Toast.LENGTH_LONG).show()
            },
            Response.ErrorListener { error ->
                Log.e("API", "Error en GET")
                Log.e("API", "Response: %s".format(error.toString()))
                Toast.makeText(this.context, "Error al enviar solicitud de amistad", Toast.LENGTH_LONG).show()
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

    override fun getFilter(): Filter {
        return mFilter
    }

    private inner class ItemFilter : Filter() {
        override fun performFiltering(constraint: CharSequence): Filter.FilterResults {

            val filterString = constraint.toString().toLowerCase()

            val results = Filter.FilterResults()

            val list = originalData

            val count = list.size
            val nlist = ArrayList<HashMap<String, String>>(count)

            var filterableString: String
            for (i in 0 until count) {
                filterableString = list[i]["name"].toString()
                if (filterableString.toLowerCase().contains(filterString)) {
                    nlist.add(list[i])
                }
            }

            results.values = nlist
            results.count = nlist.size

            return results
        }

        override fun publishResults(constraint: CharSequence, results: Filter.FilterResults) {
            dataList = results.values as ArrayList<HashMap<String, String>>
            notifyDataSetChanged()
        }

    }
}
