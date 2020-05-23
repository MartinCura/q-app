package ar.uba.fi.remy.model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import ar.uba.fi.remy.R

class  RecommendedItemAdapter(var listaRecetas:ArrayList<RecommendedItem>):RecyclerView.Adapter<RecommendedItemAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.recommended_item, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return listaRecetas.size
    }

    override fun onBindViewHolder(holder:RecommendedItemAdapter.ViewHolder, position: Int) {
        holder.bindItems(listaRecetas[position])
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view){

        fun bindItems(data:RecommendedItem){
            val nombre:TextView = itemView.findViewById(R.id.title)
            val duracion:TextView = itemView.findViewById(R.id.minutos)
            val calorias:TextView = itemView.findViewById(R.id.calorias)
/*            val foto:ImageView = itemView.findViewById(R.id.foto)*/

            nombre.text = data.nombre
            duracion.text = data.tiempo.toString()
            calorias.text = data.calorias.toString()

            itemView.setOnClickListener {
                Toast.makeText(itemView.context, "Click ${data.nombre}", Toast.LENGTH_LONG).show()
            }

        }

    }

}