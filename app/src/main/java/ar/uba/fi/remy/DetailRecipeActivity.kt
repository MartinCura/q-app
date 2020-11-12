package ar.uba.fi.remy

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_detail_recipe.*
import org.json.JSONObject

class DetailRecipeActivity : AppCompatActivity() {
    lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_recipe)

        //Obtener token
        val sharedPref = this.getSharedPreferences(
            getString(R.string.preference_file), Context.MODE_PRIVATE)
        token = sharedPref?.getString("TOKEN", "")!!

        val idRecipe= intent.getIntExtra("id_receta", 0)
        Log.i("API", idRecipe.toString())

        loadRecipe(idRecipe)
    }

    private fun loadRecipe(idRecipe: Int) {
        val queue = Volley.newRequestQueue(this)
        val url = "https://tpp-remy.herokuapp.com/api/v1/recipes/" + idRecipe + "/"

        val jsonObjectRequest = object: JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                Log.i("API", "Response: $response")
                fillData(response)
            },
            Response.ErrorListener { error ->
                Log.e("API", "Error en GET")
                Log.e("API", "Response: %s".format(error.toString()))
            }
        )
        {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Token " + token
                return headers
            }
        }

        queue.add(jsonObjectRequest)
    }

    private fun fillData(recipe: JSONObject?) {
        var title = recipe?.getString("title")
        detail_recipe_title.text = title

        /*var txtIngredients: MutableList<String> = mutableListOf()*/
        var txtIngredients:String = ""
        var ingredients = recipe?.getJSONArray("ingredients")
        for(i in 0 until ingredients!!.length()) {
            var item = ingredients.getJSONObject(i)
            var itemName = item.getJSONObject("product").getString("name")
            var itemUnit = item.getString("unit")
            var itemQantity = item.getString("quantity")
            /*txtIngredients.add(itemName + " " + itemQantity + " " + itemUnit)*/
            txtIngredients += (i+1).toString() + ". " + itemName + " " + itemQantity + " " + itemUnit + "\n"
        }
        detail_recipe_ingredients.text = txtIngredients
    }
}
