package ar.uba.fi.remy.model

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ar.uba.fi.remy.R

class ViewPagerAdapter(private var title: List<String>, private var steps: List<String>) : RecyclerView.Adapter<ViewPagerAdapter.Pager2ViewHolder>() {

    inner class Pager2ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemTitle: TextView = itemView.findViewById(R.id.step_title)
        val itemInstructions: TextView = itemView.findViewById(R.id.step_instructions)
        val itemBtn: Button = itemView.findViewById(R.id.step_btn_end)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewPagerAdapter.Pager2ViewHolder {
        return Pager2ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.step_item, parent, false))
    }

    override fun getItemCount(): Int {
        return title.size
    }

    override fun onBindViewHolder(holder: ViewPagerAdapter.Pager2ViewHolder, position: Int) {
        holder.itemTitle.text = title[position]
        holder.itemInstructions.text = steps[position]
        Log.i("API", "Item Count: " + itemCount)
        Log.i("API", "Position: " + position)
        Log.i("API", "Paso: " + title[position])
        if(itemCount - 1 == position) {
            holder.itemBtn.visibility = View.VISIBLE
        } else {
            holder.itemBtn.visibility = View.GONE
        }
    }

}