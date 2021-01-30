package ar.uba.fi.remy.model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ar.uba.fi.remy.R
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation


class ReviewItemAdapter(var listaRecetas:ArrayList<ReviewItem>):RecyclerView.Adapter<ReviewItemAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.review_item, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return listaRecetas.size
    }

    override fun onBindViewHolder(holder:ReviewItemAdapter.ViewHolder, position: Int) {
        holder.bindItems(listaRecetas[position])
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view){

        fun bindItems(data:ReviewItem){
            val nombre:TextView = itemView.findViewById(R.id.title)
            val ivFoto:ImageView = itemView.findViewById(R.id.foto)

            nombre.text = data.nombre

            if(data.foto.isNotEmpty()) {
                Picasso.get()
                    .load(data.foto.replace("http", "https"))
                    .transform(RoundedCornersTransformation(60,0))
                    .resize(200, 200).into(ivFoto)
            }

            /*itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailRecipeActivity::class.java)
                intent.putExtra("id_receta", data.id)
                itemView.context.startActivity(intent)
            }

            // Ocultar estrellas de difucultad de forma dinamica
            val starTwo:ImageView = itemView.findViewById(R.id.starTwo)
            val starThree:ImageView = itemView.findViewById(R.id.starThree)
            val starFour:ImageView = itemView.findViewById(R.id.starFour)
            val starFive:ImageView = itemView.findViewById(R.id.starFive)
            if(data.dificultad < 5) starFive.visibility = View.GONE
            if(data.dificultad < 4) starFour.visibility = View.GONE
            if(data.dificultad < 3) starThree.visibility = View.GONE
            if(data.dificultad < 2) starTwo.visibility = View.GONE*/

        }

    }

}