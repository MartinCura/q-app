package ar.uba.fi.remy.model

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ar.uba.fi.remy.R

class CategoryItemAdapter(var listaCategorias:Array<Category>): RecyclerView.Adapter<CategoryItemAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.category_item, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return listaCategorias.size
    }

    override fun onBindViewHolder(holder:CategoryItemAdapter.ViewHolder, position: Int) {
        holder.bindItems(listaCategorias[position])
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view){

        fun bindItems(data: Category){
            val categoria: TextView = itemView.findViewById(R.id.category_item_nombre)
            categoria.text = data.nombre

            itemView.setOnClickListener {
                Log.i("API", data.id.toString() + " - " + data.nombre)
            }
        }

    }

}