package ar.uba.fi.remy

import android.app.DatePickerDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import ar.uba.fi.remy.model.DatePickerFragment
import ar.uba.fi.remy.model.TimePicker
import ar.uba.fi.remy.ui.loadingIndicator.LoadingIndicatorFragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_new_event.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class NewEventActivity : AppCompatActivity() {

    lateinit var token: String
    lateinit var timePicker: TimePicker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_event)

        //Obtener token
        val sharedPref = this.getSharedPreferences(
            getString(R.string.preference_file), Context.MODE_PRIVATE)
        token = sharedPref?.getString("TOKEN", "")!!

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
                new_event_time.setText(finalText)
            }
        })
    }

    private fun createEvent() {
        if(validarCampos()) {
            var name = new_event_title.text.toString()
            var date = new_event_date.text.toString()
            var hour = new_event_time.text.toString()
            var splitDate = date.split(" / ");
            var formattedDate: String
            formattedDate = splitDate[2] + "-" + splitDate[1] + "-" + splitDate[0]
            var finalDate = formattedDate + "T" + hour +":00"
            var sharedInventory = false // new_event_shared_inventory.isChecked


            val body = JSONObject()
            body.put("attendees_id",JSONArray())
            body.put("name", name)
            body.put("starting_datetime", finalDate)
            body.put("finishing_datetime", finalDate)
            body.put("only_host_inventory", sharedInventory)

            val queue = Volley.newRequestQueue(this)
            val url = "https://tpp-remy.herokuapp.com/api/v1/events/"

            Log.i("API", body.toString())

            LoadingIndicatorFragment.show(this)
            val jsonObjectRequest = object: JsonObjectRequest(
                Request.Method.POST, url, body,
                Response.Listener { response ->
                    Log.i("API", "Response: %s".format(response.toString()))
                    finish()
                    LoadingIndicatorFragment.hide()
                },
                Response.ErrorListener { error ->
                    Log.e("API", "Error en POST")
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

        if(new_event_time.text.isNullOrEmpty()){
            new_event_time.error = "Este campo es obligatorio"
            new_event_time.requestFocus()
            return false
        }

        return true
    }
}
