package ar.uba.fi.remy

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ar.uba.fi.remy.model.ReviewItem
import ar.uba.fi.remy.model.ReviewItemAdapter
import ar.uba.fi.remy.ui.loadingIndicator.LoadingIndicatorFragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_recipes_cooked.*

class RecipesCookedActivity : AppCompatActivity() {
    lateinit var token: String
    lateinit var rvAdapter:ReviewItemAdapter
    val recipesCooked = ArrayList<ReviewItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipes_cooked)

        // Hide the status bar.
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        actionBar?.hide()

        //Obtener token
        val sharedPref = this.getSharedPreferences(
            getString(R.string.preference_file), Context.MODE_PRIVATE)
        token = sharedPref?.getString("TOKEN", "")!!

        setupRecycler()

        loadRecipesCooked()
    }

    private fun setupRecycler() {
        val recyclerView: RecyclerView = rv_reviews
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        rvAdapter = ReviewItemAdapter(this, recipesCooked, token)
        recyclerView.adapter = rvAdapter

        /*recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (!recyclerView.canScrollVertically(1)) {
                    if(!loadingMore && urlNext != "null") {
                        cargarRecetas()
                    }
                }
            }
        })*/
    }

    private fun loadRecipesCooked() {
        val queue = Volley.newRequestQueue(this)
        var url = "https://tpp-remy.herokuapp.com/api/v1/profiles/cooked_recipes/"

        LoadingIndicatorFragment.show(this)
        val jsonObjectRequest = object: JsonObjectRequest(Request.Method.GET, url, null,
            Response.Listener { response ->
                Log.i("API", "Response: %s".format(response.toString()))
                var recipes = response.getJSONArray("results")
                for(i in 0 until recipes.length()) {
                    var element = recipes.getJSONObject(i)
                    var recipe = element.getJSONObject("recipe")
                    var nombre = recipe.getString("title")
                    var puntaje = element.get("rating").toString()
                    var id = recipe.getInt("id")
                    var img = recipe.getString("image")
                    var puntajeFloat:Float = (-1).toFloat()
                    if(puntaje != "null") {
                        puntajeFloat = puntaje.toFloat()
                    }
                    recipesCooked.add(ReviewItem(id, nombre, puntajeFloat, img))
                }
                rvAdapter.notifyDataSetChanged()
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
}
