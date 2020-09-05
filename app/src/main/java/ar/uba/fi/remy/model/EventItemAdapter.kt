package ar.uba.fi.remy.model

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ar.uba.fi.remy.DetailRecipeActivity
import ar.uba.fi.remy.R

class EventItemAdapter(var listaEventos:ArrayList<EventItem>):RecyclerView.Adapter<EventItemAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.event_item, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return listaEventos.size
    }

    override fun onBindViewHolder(holder:EventItemAdapter.ViewHolder, position: Int) {
        holder.bindItems(listaEventos[position])
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view){

        fun bindItems(data:EventItem){
            val nombre:TextView = itemView.findViewById(R.id.title)
            val asistentes:TextView = itemView.findViewById(R.id.asistentes)
            /*val invitados:TextView = itemView.findViewById(R.id.invitados)*/
            val fecha:TextView = itemView.findViewById(R.id.date)

            nombre.text = data.nombre
            /*invitados.text = data.invitados.toString()*/
            asistentes.text = data.asistentes.toString()
            fecha.text = data.fecha

/*            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailEventActivity::class.java)
                // TO-DO: Cambiarlo por el ID de la receta para hacer un request a la API
                intent.putExtra("id_receta", data.nombre)
                itemView.context.startActivity(intent)
            }*/

        }

    }

}