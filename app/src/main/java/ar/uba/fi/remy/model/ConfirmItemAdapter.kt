package ar.uba.fi.remy.model


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import ar.uba.fi.remy.R
import kotlinx.android.synthetic.main.list_item_confirm.view.*


class ConfirmItemAdapter(private val context: FragmentActivity?, private var dataList: ArrayList<HashMap<String, String>>) : BaseAdapter() {

    private val inflater: LayoutInflater = this.context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    override fun getCount(): Int { return dataList.size }
    override fun getItem(position: Int): Int { return position }
    override fun getItemId(position: Int): Long { return position.toLong() }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var dataitem = dataList[position]

        val rowView = inflater.inflate(R.layout.list_item_confirm, parent, false)
        rowView.findViewById<TextView>(R.id.row_name).text = dataitem.get("product")
        rowView.findViewById<TextView>(R.id.row_age).text = dataitem.get("quantity") + " " + dataitem.get("unit")

        rowView.delete_btn.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                dataList.removeAt(position)
                notifyDataSetChanged()
            }})

        rowView.tag = position
        return rowView
    }

    fun addData(data: HashMap<String, String>) {
        dataList.add(data)
        notifyDataSetChanged()
    }

}