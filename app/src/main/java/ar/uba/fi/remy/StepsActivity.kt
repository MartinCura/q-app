package ar.uba.fi.remy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ar.uba.fi.remy.model.ViewPagerAdapter
import kotlinx.android.synthetic.main.activity_steps.*
import me.relex.circleindicator.CircleIndicator3

class StepsActivity : AppCompatActivity() {

    private var titlesList = mutableListOf<String>()
    private var stepsList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_steps)

        loadData()

        view_pager2.adapter = ViewPagerAdapter(titlesList, stepsList)

        val indicator = findViewById<CircleIndicator3>(R.id.indicator)
        indicator.setViewPager(view_pager2)
    }

    private fun addToList(title: String, instruction: String) {
        titlesList.add(title)
        stepsList.add(instruction)
    }

    private fun loadData() {
        // TO-DO: Request to API
        for (i in 1..5) {
            addToList("Paso $i", "Bla bla bla bla bla bla")
        }
    }
}
