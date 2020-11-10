package ar.uba.fi.remy

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import ar.uba.fi.remy.model.DatePickerFragment
import ar.uba.fi.remy.model.TimePicker
import kotlinx.android.synthetic.main.activity_new_event.*
import java.util.*

class NewEventActivity : AppCompatActivity() {

    lateinit var timePicker: TimePicker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_event)

        new_event_date.setOnClickListener {
            showDatePickerDialog()
        }

        timePicker = TimePicker(this, false, false)

        new_event_time.setOnClickListener {
            showTimePicker()
        }

        new_event_btn_crear.setOnClickListener {
            createEvent()
        }
    }

    private fun showTimePicker() {
        val cal = Calendar.getInstance()
        val h = cal.get(Calendar.HOUR_OF_DAY)
        val m = cal.get(Calendar.MINUTE)
        timePicker.showDialog(h, m, object : TimePicker.Callback {
            override fun onTimeSelected(hourOfDay: Int, minute: Int) {
                val hourStr = if (hourOfDay < 10) "0${hourOfDay}" else "${hourOfDay}"
                val minuteStr = if (minute < 10) "0${minute}" else "${minute}"
                val finalText = hourStr + ":" + minuteStr
                new_event_time.setText(finalText)
            }
        })
    }

    private fun createEvent() {
        if(validarCampos()) {
            var date = new_event_date.text.toString()
            date.replace(" / ", "-")

            Log.i("API", date)
            Log.i("API", new_event_title.text.toString())
        }
    }

    private fun showDatePickerDialog() {
        val newFragment = DatePickerFragment.newInstance(DatePickerDialog.OnDateSetListener { _, year, month, day ->
            // +1 because January is zero
            val selectedDate = day.toString() + " / " + (month + 1) + " / " + year
            new_event_date.setText(selectedDate)
        })

        newFragment.show(supportFragmentManager, "datePicker")
    }

    private fun validarCampos(): Boolean {
        if(new_event_title.text.isNullOrEmpty()){
            new_event_title.error = "Este campo es obligatorio"
            new_event_title.requestFocus()
            return false
        }

        if(new_event_date.text.isNullOrEmpty()){
            new_event_date.error = "Este campo es obligatorio"
            new_event_date.requestFocus()
            return false
        }

        return true
    }
}
