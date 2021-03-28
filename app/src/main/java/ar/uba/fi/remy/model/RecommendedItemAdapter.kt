package ar.uba.fi.remy.model

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ar.uba.fi.remy.DetailRecipeActivity
import ar.uba.fi.remy.R
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation


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
            /*val calorias:TextView = itemView.findViewById(R.id.calorias)*/
            val ivFoto:ImageView = itemView.findViewById(R.id.foto)

            nombre.text = data.nombre
            duracion.text = data.tiempo.toString()
            /*calorias.text = data.calorias.toString()*/

            if(data.foto.isNotEmpty()) {
                Picasso.get()
                    .load(data.foto.replace("http", "https"))
                    .transform(RoundedCornersTransformation(60,0))
                    .resize(200, 200).into(ivFoto)
            }

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailRecipeActivity::class.java)
                intent.putExtra("id_receta", data.id)
                itemView.context.startActivity(intent)
            }

            // Ocultar estrellas de difucultad de forma dinamica
            val starTwo:ImageView = itemView.findViewById(R.id.starTwo)
            val starThree:ImageView = itemView.findViewById(R.id.starThree)
            val starFour:ImageView = itemView.findViewById(R.id.starFour)
            val starFive:ImageView = itemView.findViewById(R.id.starFive)
            val starTwoEmpty:ImageView = itemView.findViewById((R.id.starTwoEmpty))
            val starThreeEmpty:ImageView = itemView.findViewById((R.id.starThreeEmpty))
            val starFourEmpty:ImageView = itemView.findViewById((R.id.starFourEmpty))
            val starFiveEmpty:ImageView = itemView.findViewById((R.id.starFiveEmpty))
            if(data.dificultad < 5) {
                starFive.visibility = View.GONE
                starFiveEmpty.visibility = View.VISIBLE
            }
            if(data.dificultad < 4) {
                starFour.visibility = View.GONE
                starFourEmpty.visibility = View.VISIBLE
            }
            if(data.dificultad < 3) {
                starThree.visibility = View.GONE
                starThreeEmpty.visibility = View.VISIBLE
            }
            if(data.dificultad < 2) {
                starTwo.visibility = View.GONE
                starTwoEmpty.visibility = View.VISIBLE
            }

        }

    }

}
