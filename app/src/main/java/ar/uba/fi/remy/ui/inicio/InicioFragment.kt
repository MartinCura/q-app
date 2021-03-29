package ar.uba.fi.remy.ui.inicio

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ar.uba.fi.remy.R
import ar.uba.fi.remy.model.RecommendedItem
import ar.uba.fi.remy.model.RecommendedItemAdapter
import ar.uba.fi.remy.ui.loadingIndicator.LoadingIndicatorFragment
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.fragment_inicio.*
import java.lang.Math.round

class InicioFragment : Fragment() {
    var urlNext:String = ""
    var loadingMore = false
    var allIngredients = true
    val recomendaciones = ArrayList<RecommendedItem>()
    lateinit var rvAdapter:RecommendedItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_inicio, container, false)

        //Titulo de la pantalla
        val textView: TextView = root.findViewById(R.id.text_home)
        textView.text = getString(R.string.home_title)

        setupRecycler(root)

        configSwitch(root)

        //Obtengo las recetas desde la API y las inserto en el RecyclerView
        cargarRecetas()

        return root
    }

    private fun configSwitch(root: View) {
        val switch: SwitchCompat = root.findViewById(R.id.switch_ingredients)
        switch.setOnCheckedChangeListener { _, isChecked ->
            allIngredients = isChecked
            urlNext = ""
            recomendaciones.clear()
            rvAdapter.notifyDataSetChanged()
            cargarRecetas()
        }
    }

    private fun setupRecycler(root: View) {
        val recyclerView:RecyclerView = root.findViewById(R.id.rv_recomendados)
        recyclerView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)

        rvAdapter = RecommendedItemAdapter(recomendaciones)
        recyclerView.adapter = rvAdapter

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (!recyclerView.canScrollVertically(1)) {
                    if(!loadingMore && urlNext != "null") {
                        cargarRecetas()
                    }
                }
            }
        })
    }

    private fun cargarRecetas() {
        loadingMore = true
        //Access sharedPreferences
        val sharedPref = activity?.getSharedPreferences(
            getString(R.string.preference_file), Context.MODE_PRIVATE)
        val token = sharedPref?.getString("TOKEN", "")
        Log.i("API", token)

        val queue = Volley.newRequestQueue(activity)
        var url = urlNext
        if(url.isBlank()) {
            url = "https://tpp-remy.herokuapp.com/api/v1/recommend/recipes/me/?need_all_ingredients=" + allIngredients
        }
        Log.i("API", url)

        LoadingIndicatorFragment.show(requireContext())
        val jsonObjectRequest = object: JsonObjectRequest(Request.Method.GET, url, null,
            Response.Listener { response ->
                Log.i("API", "Response: %s".format(response.toString()))
                if(response.getInt("count") == 0) {
                    img_nofood.visibility = View.VISIBLE
                } else {
                    img_nofood.visibility = View.GONE
                    var recomendacionesArray = response.getJSONArray("results")
                    loadingMore = false
                    urlNext = response.getString("next")
                    for (i in 0 until recomendacionesArray.length()) {
                        var recomendacion = recomendacionesArray.getJSONObject(i)
                        var recipe = recomendacion.getJSONObject("recipe")
                        var title = recipe.getString("title")
                        var id = recipe.getInt("id")
                        var score = round(recomendacion.getString("rating").toDouble())
                        var img = recipe.getString("description").split('\'')[3]
                        var duration = recipe.getInt("duration")
                        recomendaciones.add(RecommendedItem(id, title, score.toInt(), duration,  20, img))
                        rvAdapter.notifyDataSetChanged()
                    }
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

        jsonObjectRequest.retryPolicy =
            DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        queue.add(jsonObjectRequest)
    }
}
