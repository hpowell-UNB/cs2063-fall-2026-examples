package mobiledev.unb.ca.speechtotextdemo

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private var txtSpeechInput: TextView? = null
    private var speechToTextActivityResultLauncher: ActivityResultLauncher<Intent>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        txtSpeechInput = findViewById(R.id.textView)

        // Default to Light Mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val commandButton: Button = findViewById(R.id.cmdButton)
        commandButton.setOnClickListener { promptSpeechInput() }

        // Register the activity listener
        setSpeechToTextActivityResultLauncher()
    }

    private fun setSpeechToTextActivityResultLauncher() {
        speechToTextActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data!!
                showSpeechInput(data)
            }
        }
    }

    private fun showSpeechInput(data: Intent?) {
        // safe-call operator ?. is used to access properties of nullable types
        val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
        txtSpeechInput!!.text = result!![0]
    }

    private fun promptSpeechInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(
            RecognizerIntent.EXTRA_PROMPT,
            getString(R.string.say_something)
        )
        try {
            speechToTextActivityResultLauncher!!.launch(intent)
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(
                applicationContext,
                getString(R.string.not_supported),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}