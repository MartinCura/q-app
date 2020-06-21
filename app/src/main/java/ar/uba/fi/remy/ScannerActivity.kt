package ar.uba.fi.remy

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_scanner.*
import android.R.attr.data
import android.widget.Toast
import com.google.zxing.integration.android.IntentResult
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T



class ScannerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)

        scanner_btn.setOnClickListener(View.OnClickListener {
            val scanner = IntentIntegrator(this)
            scanner.initiateScan()
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == Activity.RESULT_OK){
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if (result != null) {
                if (result.contents == null) {
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Scanned: " + result.contents, Toast.LENGTH_LONG).show()
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }
}
