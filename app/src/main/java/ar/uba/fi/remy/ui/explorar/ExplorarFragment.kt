package ar.uba.fi.remy.ui.explorar

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ar.uba.fi.remy.R
import ar.uba.fi.remy.model.Category
import ar.uba.fi.remy.model.CategoryItemAdapter
import ar.uba.fi.remy.ui.loadingIndicator.LoadingIndicatorFragment
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

        val queue = Volley.newRequestQueue(activity)
        val url = "https://tpp-remy.herokuapp.com/api/v1/dishlabels/"

        LoadingIndicatorFragment.show(requireContext())
        val jsonObjectRequest = object: JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                val categoriasArray = response.getJSONArray("results")
                Log.i("API", categoriasArray.toString())
                var categorias: Array<Category> = emptyArray()
                for (i in 0 until categoriasArray.length()) {
                    val nombre = categoriasArray.getJSONObject(i).getString("name")
                    val id = categoriasArray.getJSONObject(i).getString("id")
                    val categoriaNueva = Category(nombre, id.toInt())
                    categorias = append(categorias, categoriaNueva)
                }
                val adapter = CategoryItemAdapter(categorias)
                recyclerView.adapter = adapter
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

    fun append(arr: Array<Category>, element: Category): Array<Category> {
        val list: MutableList<Category> = arr.toMutableList()
        list.add(element)
        return list.toTypedArray()
    }
}