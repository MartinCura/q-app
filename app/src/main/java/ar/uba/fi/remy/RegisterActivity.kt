package ar.uba.fi.remy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_register.*


class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Hide the status bar.
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        actionBar?.hide()

        setContentView(R.layout.activity_register)

        register_btn_crear.setOnClickListener(View.OnClickListener {
            crearCuenta()
        })
    }

    private fun crearCuenta() {
        if(validarCampos()){
            //Request API register


            //Back to login activity
            goLogin()
        }
    }

    private fun validarCampos(): Boolean {
        if(register_txt_usuario.text.isNullOrEmpty()){
            register_txt_usuario.error = "Este campo es obligatorio"
            register_txt_usuario.requestFocus()
            return false
        }

        if(register_txt_email.text.isNullOrEmpty()){
            register_txt_email.error = "Este campo es obligatorio"
            register_txt_email.requestFocus()
            return false
        }

        val EMAIL_REGEX = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"

        if(!EMAIL_REGEX.toRegex().matches(register_txt_email.text.toString())){
            register_txt_email.error = "Debe ingresar un mail válido"
            register_txt_email.requestFocus()
            return false
        }

        if(register_txt_psw1.text.isNullOrEmpty()){
            register_txt_psw1.error = "Este campo es obligatorio"
            register_txt_psw1.requestFocus()
            return false
        }

        if(register_txt_psw1.text.toString().length < 6){
            register_txt_psw1.error = "La contraseña debe tener al menos 6 caracteres"
            register_txt_psw1.requestFocus()
            return false
        }

        val PSW_REGEX = ".*\\d.*"
        if(!PSW_REGEX.toRegex().matches(register_txt_psw1.text.toString())){
            register_txt_psw1.error = "La contraseña debe tener al menos 1 número"
            register_txt_psw1.requestFocus()
            return false
        }

        if(register_txt_psw2.text.isNullOrEmpty()){
            register_txt_psw2.error = "Este campo es obligatorio"
            register_txt_psw2.requestFocus()
            return false
        }

        if(!register_txt_psw2.text.toString().equals(register_txt_psw1.text.toString())){
            register_txt_psw2.error = "Las contraseñas no coinciden"
            register_txt_psw2.requestFocus()
            return false
        }

        return true
    }

    private fun goLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        this.finish()
    }
}
