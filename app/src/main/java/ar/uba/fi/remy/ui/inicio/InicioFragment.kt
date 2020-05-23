package ar.uba.fi.remy.ui.inicio

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ar.uba.fi.remy.MainActivity
import ar.uba.fi.remy.R
import ar.uba.fi.remy.model.RecommendedItem
import ar.uba.fi.remy.model.RecommendedItemAdapter
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class InicioFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_inicio, container, false)

        //Titulo de la pantalla
        val textView: TextView = root.findViewById(R.id.text_home)
        textView.text = getString(R.string.home_title)

        //Obtengo las recetas desde la API y las inserto en el RecyclerView
        cargarRecetas(root)

        return root
    }

    private fun cargarRecetas(root: View) {
        val recyclerView:RecyclerView = root.findViewById(R.id.rv_recomendados)
        recyclerView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)

        //Reemplazar por el llamado a la API para obtener las recetas
        val recomendaciones = ArrayList<RecommendedItem>()
        recomendaciones.add(RecommendedItem("Recomendación 1", 10, 15, 20, 1))
        recomendaciones.add(RecommendedItem("Recomendación 2", 20, 25, 30, 1))
        recomendaciones.add(RecommendedItem("Recomendación 3", 10, 15, 20, 1))
        recomendaciones.add(RecommendedItem("Recomendación 4", 20, 25, 30, 1))
        recomendaciones.add(RecommendedItem("Recomendación 5", 10, 15, 20, 1))
        recomendaciones.add(RecommendedItem("Recomendación 6", 20, 25, 30, 1))

 /*       val queue = Volley.newRequestQueue(activity)
        val url = "https://tpp-remy.herokuapp.com/api/v1/ingredients/"

        val jsonObjectRequest = object: JsonObjectRequest(Request.Method.GET, url, null,
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
                headers["Authorization"] = "Token 758fedb9f337fe54019228dbbf9b46f828fbc945"
                return headers
            }
        }


        queue.add(jsonObjectRequest)*/

        val adapter = RecommendedItemAdapter(recomendaciones)
        recyclerView.adapter = adapter
    }
}
