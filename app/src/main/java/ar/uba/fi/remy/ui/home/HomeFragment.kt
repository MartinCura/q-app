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

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        homeViewModel.text.observe(this, Observer {
            textView.text = it
        })

        val recyclerView:RecyclerView = root.findViewById(R.id.rv_recomendados)
        recyclerView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)

        val recomendaciones = ArrayList<RecommendedItem>()
        recomendaciones.add(RecommendedItem("Recomendación 1", 10, 15, 20, 1))
        recomendaciones.add(RecommendedItem("Recomendación 2", 20, 25, 30, 1))
        recomendaciones.add(RecommendedItem("Recomendación 3", 10, 15, 20, 1))
        recomendaciones.add(RecommendedItem("Recomendación 4", 20, 25, 30, 1))
        recomendaciones.add(RecommendedItem("Recomendación 5", 10, 15, 20, 1))
        recomendaciones.add(RecommendedItem("Recomendación 6", 20, 25, 30, 1))

        val adapter = RecommendedItemAdapter(recomendaciones)
        recyclerView.adapter = adapter

        return root
    }
}
