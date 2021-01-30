package ar.uba.fi.remy

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_detail_recipe.*
import org.json.JSONArray
import org.json.JSONObject

class DetailRecipeActivity : AppCompatActivity() {
    lateinit var token: String
    var idRecipe: Int = 0
    var steps: JSONArray? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_recipe)

        //Obtener token
        val sharedPref = this.getSharedPreferences(
            getString(R.string.preference_file), Context.MODE_PRIVATE)
        token = sharedPref?.getString("TOKEN", "")!!

        idRecipe= intent.getIntExtra("id_receta", 0)
        Log.i("API", idRecipe.toString())

        loadRecipe(idRecipe)

        configCookBtn()

        configAddCartBtn()
    }

    private fun configAddCartBtn() {
        detail_recipe_add_cart.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Ingredientes")
                .setMessage("Indique si desea agregar todos los ingredientes o solo los faltantes")
                .setNegativeButton("SOLO FALTANTES") { dialog, which ->
                    Log.i("API", "Solo faltantes")
                    addIngredients(true)
                }
                .setPositiveButton("TODOS") { dialog, which ->
                    Log.i("API", "Todos")
                    addIngredients(false)
                }
                .show()
        }
    }

    private fun addIngredients(todos: Boolean) {
        val queue = Volley.newRequestQueue(this)
        val url = "https://tpp-remy.herokuapp.com/api/v1/cart/add_recipe/?recipe=" + idRecipe + "&only_missing=" + todos
        Log.i("API", url)
        val jsonObjectRequest = object: JsonObjectRequest(
            Request.Method.POST, url, null,
            Response.Listener { response ->
                Log.i("API", "Response: $response")
                Toast.makeText(this, response.getString("message"), Toast.LENGTH_SHORT).show()
                finish()
            },
            Response.ErrorListener { error ->
                Log.e("API", "Error en POST")
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

    private fun configCookBtn() {
        detail_recipe_cook.setOnClickListener {
            val intent = Intent(this, StepsActivity::class.java)
            intent.putExtra("id_receta", idRecipe)
            intent.putExtra("steps", steps.toString())
            startActivity(intent)
        }
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

        steps = recipe?.getJSONArray("instructions")
        Log.i("API", steps.toString())
    }
}
