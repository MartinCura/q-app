package ar.uba.fi.remy.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ar.uba.fi.remy.R
import ar.uba.fi.remy.model.RecommendedItem
import ar.uba.fi.remy.model.RecommendedItemAdapter

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)

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

        val adapter = RecommendedItemAdapter(recomendaciones)
        recyclerView.adapter = adapter
    }
}
