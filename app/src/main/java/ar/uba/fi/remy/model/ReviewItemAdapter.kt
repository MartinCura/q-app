package ar.uba.fi.remy.model

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ar.uba.fi.remy.R
import ar.uba.fi.remy.RecipesCookedActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import org.json.JSONObject
import android.content.Context


private var contextGlobal: Context? = null
private var tokenGlobal: String? = null
class ReviewItemAdapter(private val context: Activity?, var listaRecetas:ArrayList<ReviewItem>, private var token: String):RecyclerView.Adapter<ReviewItemAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.review_item, parent, false)
        contextGlobal = context
        tokenGlobal = token
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

            val ratingbar:RatingBar = itemView.findViewById(R.id.ratingbar)
            ratingbar.onRatingBarChangeListener =
                RatingBar.OnRatingBarChangeListener { p0, rating, p2 ->
                    /*Log.i("API", "Rating " + data.nombre + ": " + rating)*/
                    addReview(data.id, rating)
            }

            /*ratingbar.setStepSize(0.5f);*/
            ratingbar.rating = data.puntaje

        }

        private fun addReview(id: Int, rating: Float) {
            val queue = Volley.newRequestQueue(contextGlobal)
            val url = "https://tpp-remy.herokuapp.com/api/v1/recipes/rate"

            val body = JSONObject()
            body.put("recipe_id", id)
            body.put("rating", rating)
            Log.i("API", "ID: " + id + " Rating: " + rating)

            val jsonObjectRequest = object: JsonObjectRequest(
                Request.Method.PUT, url, body,
                Response.Listener { response ->
                    Log.i("API", "Response: %s".format(response.toString()))
                },
                Response.ErrorListener { error ->
                    Log.e("API", "Error en GET")
                    Log.e("API", "Response: %s".format(error.toString()))
                }
            )
            {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Authorization"] = "Token " + tokenGlobal
                    return headers
                }
            }

            queue.add(jsonObjectRequest)
        }

    }

}