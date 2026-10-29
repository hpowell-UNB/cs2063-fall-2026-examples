package mobiledev.unb.ca.preferencesdatastoredemo

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private lateinit var highScoreTextView: TextView
    private lateinit var gameScoreTextView: TextView

    private var currentHighScore: Int = INITIAL_HIGH_SCORE

    // Lazily initialize the repository using the top-level singleton dataStore extension
    val dataStoreRepository: DataStoreRepository by lazy {
        DataStoreRepository(appPreferencesDataStore)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        highScoreTextView = findViewById(R.id.highScoreText)
        // Setup listener for changed values
        lifecycleScope.launch {
            //• Standard lifecycleScope.launch continues to collect data in the
            // background even when the app is minimized which can
            // lead to wasted processing power or bugs if UI operations run while invisible.
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // repeatOnLifecycle(Lifecycle.State.STARTED) helps with battery consumption by automatically
                // destroying the internal coroutine block when the switching apps (entering the STOPPED state).
                // When the app is restarted this automatically launches a fresh coroutine
                // block and grabs the latest state from the helper file.
                val scoreFlow = dataStoreRepository.getIntFlow(KEY_HIGH_SCORE, defaultValue = 0)
                scoreFlow.collect { score ->
                    currentHighScore = score
                    setHighScoreText(score)
                }
            }
        }

        gameScoreTextView = findViewById(R.id.gameScoreText)
        setGameScoreText(INITIAL_HIGH_SCORE)

        val playButton: Button = findViewById(R.id.playButton)
        playButton.setOnClickListener {
            handlePlayButton()
        }

        val resetButton: Button = findViewById(R.id.resetButton)
        resetButton.setOnClickListener {
            handleResetButton()
        }
    }

    private fun handlePlayButton() {
        val randomScore = Random.nextInt(1000)
        setGameScoreText(randomScore)
        checkAndUpdateHighScore(randomScore)
    }

    private fun handleResetButton() {
        // Reset the high score
        resetHighScore()
        setGameScoreText(INITIAL_HIGH_SCORE)
        setHighScoreText(INITIAL_HIGH_SCORE)
    }

    private fun resetHighScore() {
        // Since the clearHighScore() is a suspend function,
        // it has to be launched in a coroutine scope
        lifecycleScope.launch {
            dataStoreRepository.removeIntValue(KEY_HIGH_SCORE)
        }
    }

    private fun setHighScoreText(highScore: Int) {
        highScoreTextView.text = getString(R.string.high_score, highScore)
    }

    private fun setGameScoreText(gameScore: Int) {
        gameScoreTextView.text = gameScore.toString()
    }

    private fun checkAndUpdateHighScore(newScore: Int) {
        if (newScore > currentHighScore) {
            lifecycleScope.launch {
                dataStoreRepository.saveIntValue(KEY_HIGH_SCORE, newScore)
            }
        }
    }

    companion object {
        private const val KEY_HIGH_SCORE = "HIGH_SCORE"
        private const val INITIAL_HIGH_SCORE = 0
    }
}
