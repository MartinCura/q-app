package ar.uba.fi.remy

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.text.HtmlCompat
import ar.uba.fi.remy.ui.loadingIndicator.LoadingIndicatorFragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
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

        // Hide the status bar.
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        actionBar?.hide()

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
        LoadingIndicatorFragment.show(this)
        val jsonObjectRequest = object: JsonObjectRequest(
            Request.Method.POST, url, null,
            Response.Listener { response ->
                Log.i("API", "Response: $response")
                Toast.makeText(this, response.getString("message"), Toast.LENGTH_SHORT).show()
                LoadingIndicatorFragment.hide()
                finish()
            },
            Response.ErrorListener { error ->
                Log.e("API", "Error en POST")
                Log.e("API", "Response: %s".format(error.toString()))
                LoadingIndicatorFragment.hide()
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

        LoadingIndicatorFragment.show(this)
        val jsonObjectRequest = object: JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                Log.i("API", "Response: $response")
                fillData(response)
                LoadingIndicatorFragment.hide()
            },
            Response.ErrorListener { error ->
                Log.e("API", "Error en GET")
                Log.e("API", "Response: %s".format(error.toString()))
                LoadingIndicatorFragment.hide()
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
            var itemQuantity = item.getString("quantity")
            /*txtIngredients.add(itemName + " " + itemQantity + " " + itemUnit)*/
            var txtAmount:String;
            if (itemQuantity == null || itemQuantity.isEmpty() || itemQuantity == "null") {
                txtAmount = "";
            } else {
                txtAmount = "$itemQuantity ";
                if (!(itemUnit == null || itemUnit.isEmpty() || itemUnit == "unit")) {
                    txtAmount += "$itemUnit ";
                }
            }
            txtIngredients += "- $txtAmount<b>$itemName</b><br />"
        }
        detail_recipe_ingredients.text = HtmlCompat.fromHtml(txtIngredients, HtmlCompat.FROM_HTML_MODE_LEGACY)

        steps = recipe?.getJSONArray("instructions")
        Log.i("API", steps.toString())

        var img = recipe?.getString("description")?.split('\'')?.get(3)
        if(img?.isNotEmpty()!!) {
            Picasso.get()
                .load(img.replace("http", "https"))
                .transform(RoundedCornersTransformation(60,0))
                .resize(200, 200).into(detail_recipe_image)
        }

        detail_recipe_duration.text = recipe?.getString("duration") + " minutos"

        val rating = recipe?.getJSONObject("rating")
        if (rating != null && rating.getBoolean("real")) {
            detail_recipe_rating.text = rating.getString("score") + " ★"
        }
    }
}
