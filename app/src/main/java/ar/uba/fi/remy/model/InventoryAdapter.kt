package ar.uba.fi.remy.model


import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.FragmentActivity
import ar.uba.fi.remy.R
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.text.Normalizer


class InventoryAdapter(private val context: FragmentActivity?, private var dataList: ArrayList<HashMap<String, String>>, private val flag:Int, private var token: String) : BaseAdapter(), Filterable {

    private val inflater: LayoutInflater = this.context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    override fun getCount(): Int { return dataList.size }
    override fun getItem(position: Int): Int { return position }
    override fun getItemId(position: Int): Long { return position.toLong() }
    private val mFilter = ItemFilter()
    private var originalData : List<HashMap<String, String>> = dataList

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var dataitem = dataList[position]

        var rowView: View
        rowView = if(flag == 1) {
            inflater.inflate(R.layout.inventory_row, parent, false)
        } else {
            inflater.inflate(R.layout.chango_row, parent, false)
        }

        rowView.findViewById<TextView>(R.id.row_name).text = dataitem.get("ingrediente")
        rowView.findViewById<TextView>(R.id.row_age).text = dataitem.get("cantidad")

        if(flag == 2) {
            val ingredient_remove = rowView.findViewById<ImageButton>(R.id.chango_remove)
            if(ingredient_remove != null) {
                ingredient_remove.setOnClickListener {
                    removeItem(dataitem.get("id"))
                }
            }
        }

        rowView.tag = position
        return rowView
    }

    private fun removeItem(id: String?) {
        val queue = Volley.newRequestQueue(context)
        val url = "https://tpp-remy.herokuapp.com/api/v1/cart/" + id + "/"
        Log.i("API", url)

        val jsonObjectRequest = object: JsonObjectRequest(
            Request.Method.DELETE, url, null,
            Response.Listener { response ->
                Log.i("API", "Ingrediente borrado")
            },
            Response.ErrorListener { error ->
                Log.e("API", "Error en DELETE")
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

    override fun getFilter(): Filter {
        return mFilter
    }

    private val REGEX_UNACCENT = "\\p{InCombiningDiacriticalMarks}+".toRegex()

    fun CharSequence.unaccent(): String {
        val temp = Normalizer.normalize(this, Normalizer.Form.NFD)
        return REGEX_UNACCENT.replace(temp, "")
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
                filterableString = list[i]["ingrediente"].toString()
                if (filterableString.toLowerCase().unaccent().contains(filterString)) {
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