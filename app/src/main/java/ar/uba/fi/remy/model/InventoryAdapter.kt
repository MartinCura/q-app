package ar.uba.fi.remy.model


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import ar.uba.fi.remy.R


class InventoryAdapter(private val context: Context, private val dataList: ArrayList<HashMap<String, String>>) : BaseAdapter() {

    private val inflater: LayoutInflater = this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    override fun getCount(): Int { return dataList.size }
    override fun getItem(position: Int): Int { return position }
    override fun getItemId(position: Int): Long { return position.toLong() }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var dataitem = dataList[position]

        val rowView = inflater.inflate(R.layout.inventory_row, parent, false)
        rowView.findViewById<TextView>(R.id.row_name).text = dataitem.get("ingrediente")
        rowView.findViewById<TextView>(R.id.row_age).text = dataitem.get("cantidad")

        rowView.tag = position
        return rowView
    }
}