package ar.uba.fi.remy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ar.uba.fi.remy.model.ViewPagerAdapter
import kotlinx.android.synthetic.main.activity_steps.*
import me.relex.circleindicator.CircleIndicator3
import org.json.JSONArray
import org.json.JSONException




class StepsActivity : AppCompatActivity() {
    private var titlesList = mutableListOf<String>()
    private var stepsList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_steps)

        var idRecipe = intent.getIntExtra("id_receta", 0)
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
}
