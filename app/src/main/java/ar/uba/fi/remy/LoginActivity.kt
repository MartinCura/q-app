package ar.uba.fi.remy

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import ar.uba.fi.remy.model.VolleySingleton
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Access sharedPreferences
        val sharedPref = this.getSharedPreferences(
            getString(R.string.preference_file), Context.MODE_PRIVATE)
        /*val editor = sharedPref.edit()
        editor.clear()
        editor.apply()*/
        val token = sharedPref.getString("TOKEN", "")
        if(token !== ""){
            iniciarMain()
        }

        // Hide the status bar.
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        actionBar?.hide()

        setContentView(R.layout.activity_login)


        login_btn_ingresar.setOnClickListener(View.OnClickListener {
            iniciarSesion()
        })

    }

    private fun iniciarSesion() {

        if(validarCampos()){
            val url = "https://tpp-remy.herokuapp.com/api/v1/rest-auth/login/"

            val params = HashMap<String,String>()
            params["username"] = login_txt_usuario.text.toString()
            params["password"] = login_txt_psw.text.toString()
            val jsonObject = JSONObject(params)

            val request = JsonObjectRequest(Request.Method.POST,url,jsonObject,
                Response.Listener { response ->
                    // Process the json
                    /*Log.i("API", "Response: %s".format(response.toString()))*/

                    //Access sharedPreferences
                    val sharedPref = this.getSharedPreferences(
                        getString(R.string.preference_file), Context.MODE_PRIVATE)
                    val editor = sharedPref.edit()
                    editor.putString("TOKEN", response["key"].toString())
                    editor.apply()

                    iniciarMain()
                }, Response.ErrorListener{error ->
                    // Error in request
                    Log.e("API", "Response: %s".format(error.toString()))
                })

            VolleySingleton.getInstance(this).addToRequestQueue(request)
        }

    }

    private fun validarCampos(): Boolean {

        if(login_txt_usuario.text.isNullOrEmpty()){
            login_txt_usuario.error = "Este campo es obligatorio"
            login_txt_usuario.requestFocus()
            return false
        }

        if(login_txt_psw.text.isNullOrEmpty()){
            login_txt_psw.error = "Este campo es obligatorio"
            login_txt_psw.requestFocus()
            return false
        }

        return true
    }

    private fun iniciarMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}
