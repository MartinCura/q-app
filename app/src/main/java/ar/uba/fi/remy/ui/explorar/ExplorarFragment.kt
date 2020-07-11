package ar.uba.fi.remy.ui.explorar

import android.os.Bundle
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

class ExplorarFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_explorar, container, false)

        //Título de la pantalla
        /*val textView: TextView = root.findViewById(R.id.dashboard_title)
        textView.text = getString(R.string.dashboard_title)*/

        //Obtengo las categorias de la API
        cargarCategorias(root)

        return root
    }

    private fun cargarCategorias(root: View) {
        val recyclerView: RecyclerView = root.findViewById(R.id.rv_categorias)
        recyclerView.layoutManager = GridLayoutManager(activity, 4)

        //Reemplazar por el llamado a la API para obtener las recetas
        val categorias = arrayOf("Categoria 1", "Categoria 2", "Categoria 3", "Categoria 4", "Categoria 5")


        val adapter = CategoryItemAdapter(categorias)
        recyclerView.adapter = adapter
    }
}