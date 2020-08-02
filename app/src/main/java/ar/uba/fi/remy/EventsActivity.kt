package ar.uba.fi.remy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ar.uba.fi.remy.model.EventItem
import ar.uba.fi.remy.model.EventItemAdapter
import kotlinx.android.synthetic.main.activity_events.*

class EventsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events)

        cargarEventos()
    }

    private fun cargarEventos() {
        val recyclerView: RecyclerView = rv_eventos
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        //Reemplazar por el llamado a la API para obtener las recetas
        val recomendaciones = ArrayList<EventItem>()
        recomendaciones.add(EventItem("Evento 1", 1, 15))
        recomendaciones.add(EventItem("Evento 2", 2, 25))
        recomendaciones.add(EventItem("Evento 3", 1, 15))
        recomendaciones.add(EventItem("Evento 4", 3, 25))
        recomendaciones.add(EventItem("Evento 5", 4, 15))
        recomendaciones.add(EventItem("Evento 6", 5, 25))

        val adapter = EventItemAdapter(recomendaciones)
        recyclerView.adapter = adapter
    }
}
