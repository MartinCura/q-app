package ar.uba.fi.remy.ui.explorar

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ar.uba.fi.remy.R
import ar.uba.fi.remy.model.CategoryItemAdapter
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

class ExplorarFragment : Fragment() {

    lateinit var token: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_explorar, container, false)

        //Obtener token
        val sharedPref = activity?.getSharedPreferences(
            getString(R.string.preference_file), Context.MODE_PRIVATE)
        token = sharedPref?.getString("TOKEN", "")!!

        //Obtengo las categorias de la API
        cargarCategorias(root)

        return root
    }

    private fun cargarCategorias(root: View) {
        val recyclerView: RecyclerView = root.findViewById(R.id.rv_categorias)
        recyclerView.layoutManager = GridLayoutManager(activity, 4)

        //Reemplazar por el llamado a la API para obtener las recetas
        //val categorias = arrayOf("Categoria 1", "Categoria 2", "Categoria 3", "Categoria 4", "Categoria 5")

        val queue = Volley.newRequestQueue(activity)
        val url = "https://tpp-remy.herokuapp.com/api/v1/dishlabels/"

        val jsonObjectRequest = object: JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                val categoriasArray = response.getJSONArray("results")
                var categorias: Array<String> = emptyArray()
                for (i in 0 until categoriasArray.length()) {
                    categorias = append(categorias, categoriasArray.getJSONObject(i).getString("name"))
                }
                val adapter = CategoryItemAdapter(categorias)
                recyclerView.adapter = adapter
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

    fun append(arr: Array<String>, element: String): Array<String> {
        val list: MutableList<String> = arr.toMutableList()
        list.add(element)
        return list.toTypedArray()
    }
}