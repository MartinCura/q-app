package ar.uba.fi.remy

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ar.uba.fi.remy.model.RecommendedItem
import ar.uba.fi.remy.model.RecommendedItemAdapter
import ar.uba.fi.remy.ui.loadingIndicator.LoadingIndicatorFragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

class CategoryDishes : AppCompatActivity() {

    lateinit var token: String
    var urlNext:String = ""
    var loadingMore = false
    val recomendaciones = ArrayList<RecommendedItem>()
    lateinit var rvAdapter: RecommendedItemAdapter
    var id = 0
    var query = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_dishes)

        // Hide the status bar.
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        actionBar?.hide()


        Log.i("API", intent.getIntExtra("idCategory", 0).toString())

        //Obtener token
        val sharedPref = this.getSharedPreferences(
            getString(R.string.preference_file), Context.MODE_PRIVATE)
        token = sharedPref?.getString("TOKEN", "")!!

        id =  intent.getIntExtra("idCategory", 0)
        query = intent.getStringExtra("query")
        setupRecycler()
        loadDishes()
    }

    private fun setupRecycler() {
        val recyclerView: RecyclerView = this.findViewById(R.id.rv_explore)
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        rvAdapter = RecommendedItemAdapter(recomendaciones)
        recyclerView.adapter = rvAdapter

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (!recyclerView.canScrollVertically(1)) {
                    if(!loadingMore && urlNext != "null") {
                        loadDishes()
                    }
                }
            }
        })
    }

    private fun loadDishes() {
        loadingMore = true
        val queue = Volley.newRequestQueue(this)
        var url = urlNext
        if(url.isBlank() && id != 0) {
            url = "https://tpp-remy.herokuapp.com/api/v1/recipes/?dish__labels=" + id
        } else {
            url = "https://tpp-remy.herokuapp.com/api/v1/recipes/?search=" + query
        }

        LoadingIndicatorFragment.show(this)
        val jsonObjectRequest = object: JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                Log.i("API", response.toString())
                var recomendacionesArray = response.getJSONArray("results")
                loadingMore = false
                urlNext = response.getString("next")
                for (i in 0 until recomendacionesArray.length()) {
                    var recomendacion = recomendacionesArray.getJSONObject(i)
                    var title = recomendacion.getString("title")
                    var id = recomendacion.getInt("id")
                    /*var score = round(recomendacion.getString("score").toDouble() / 2)*/
                    var score = 3
                    var img = recomendacion.getString("description").split('\'')[3]
                    var duration = recomendacion.getString("duration")
                    recomendaciones.add(RecommendedItem(id, title, score.toInt(), duration.toInt(),  20, img))
                    rvAdapter.notifyDataSetChanged()
                }
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
