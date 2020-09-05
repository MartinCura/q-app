package ar.uba.fi.remy.model


import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import ar.uba.fi.remy.R


class ContactAdapter(private val context: Activity?, private val dataList: ArrayList<HashMap<String, String>>) : BaseAdapter() {

    private val inflater: LayoutInflater = this.context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    override fun getCount(): Int { return dataList.size }
    override fun getItem(position: Int): Int { return position }
    override fun getItemId(position: Int): Long { return position.toLong() }

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

/*    fun filter(text: String) {
        val result = dataList.filter { it["ingrediente"]!!.startsWith(text.toString()) }
        Log.i("API", result.toString())
    }*/
}