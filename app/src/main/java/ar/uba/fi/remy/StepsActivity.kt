package ar.uba.fi.remy

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import ar.uba.fi.remy.model.ViewPagerAdapter
import ar.uba.fi.remy.ui.loadingIndicator.LoadingIndicatorFragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_steps.*
import me.relex.circleindicator.CircleIndicator3
import org.json.JSONArray
import org.json.JSONException




class StepsActivity : AppCompatActivity() {
    lateinit var token: String
    private var titlesList = mutableListOf<String>()
    private var stepsList = mutableListOf<String>()
    var idRecipe = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_steps)

        //Obtener token
        val sharedPref = this.getSharedPreferences(
            getString(R.string.preference_file), Context.MODE_PRIVATE)
        token = sharedPref?.getString("TOKEN", "")!!

        idRecipe = intent.getIntExtra("id_receta", 0)
        var steps = intent.getStringExtra("steps")

        loadData(steps)

        view_pager2.adapter = ViewPagerAdapter(titlesList, stepsList)

        val indicator = findViewById<CircleIndicator3>(R.id.indicator)
        indicator.setViewPager(view_pager2)


    }

    private fun addToList(title: String, instruction: String) {
        titlesList.add(title)
        stepsList.add(instruction)
    }

    private fun loadData(steps: String) {
        var array: JSONArray = JSONArray()
        try {
            array = JSONArray(steps)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        for (i in 0 until array.length()) {
            addToList("Paso ${(i+1)}", array.getString(i))
        }
    }

    fun finishCooking(view: View) {
        // TO-DO: Confirmar ingredientes usados para quitar y removeerlos del inventario
        removeIngredientes()

        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    private fun removeIngredientes() {
        val queue = Volley.newRequestQueue(this)
        val url = "https://tpp-remy.herokuapp.com/api/v1/cook_recipe?recipe_id=" + idRecipe

        val jsonObjectRequest = object: JsonObjectRequest(
            Request.Method.POST, url, null,
            Response.Listener { response ->
                Log.i("API", "Response: %s".format(response.toString()))
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
}
