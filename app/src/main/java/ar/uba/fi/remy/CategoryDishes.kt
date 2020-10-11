package ar.uba.fi.remy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class CategoryDishes : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_dishes)
        Log.i("API", intent.getIntExtra("idCategory", 0).toString())
    }
}
