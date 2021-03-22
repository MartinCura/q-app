package ar.uba.fi.remy.model

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ar.uba.fi.remy.CategoryDishes
import ar.uba.fi.remy.R
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation

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
            val ivFoto: ImageView = itemView.findViewById(R.id.category_item_foto)

            if(data.foto.isNotEmpty()) {
                Picasso.get()
                    .load(data.foto)
                    .transform(RoundedCornersTransformation(600,50))
                    .resize(300, 300)
                    .into(ivFoto)
            }

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, CategoryDishes::class.java)
                Log.i("API", "ID: " + data.id)
                intent.putExtra("idCategory", data.id)
                itemView.context.startActivity(intent)
            }
        }

    }

}