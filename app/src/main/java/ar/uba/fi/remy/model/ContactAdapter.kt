package ar.uba.fi.remy.model


import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import ar.uba.fi.remy.R


class ContactAdapter(private val context: Activity?, private var dataList: ArrayList<HashMap<String, String>>) : BaseAdapter(), Filterable {

    private val inflater: LayoutInflater = this.context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    override fun getCount(): Int { return dataList.size }
    override fun getItem(position: Int): Int { return position }
    override fun getItemId(position: Int): Long { return position.toLong() }
    private val mFilter = ItemFilter()
    private var originalData : List<HashMap<String, String>> = dataList

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var dataitem = dataList[position]

        val rowView = inflater.inflate(R.layout.contact_item, parent, false)
        rowView.findViewById<TextView>(R.id.contact_name).text = dataitem.get("name")
        rowView.findViewById<TextView>(R.id.contact_username).text = dataitem.get("username")
        rowView.findViewById<TextView>(R.id.contact_email).text = dataitem.get("email")

        rowView.tag = position
        return rowView
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