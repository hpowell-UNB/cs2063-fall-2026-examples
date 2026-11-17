package mobiledev.unb.ca.pinchandzoomdemo

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import android.view.View
import android.view.View.OnTouchListener
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.math.max
import kotlin.math.min

class MainActivity : AppCompatActivity() {
    private lateinit var scaleGestureDetector: ScaleGestureDetector
    private lateinit var doubleTapDetector: GestureDetector
    private lateinit var imageView: ImageView

    private var scaleFactor = 1.0f
    private var isPinching = false

    // Values for the original position used with double-tap reset
    private var originalX = 0f
    private var originalY = 0f
    private var isOriginalPosCaptured = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        imageView = findViewById(R.id.imageView)

        // Initialize the scale gesture and double tap detectors
        scaleGestureDetector = ScaleGestureDetector(this, ScaleListener())
        doubleTapDetector = GestureDetector(this, DoubleTapListener())

        imageView.setOnTouchListener(createTouchListener())
    }

    // Capture the layout position once the view is laid out properly on screen
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus && !isOriginalPosCaptured) {
            originalX = imageView.x
            originalY = imageView.y
            isOriginalPosCaptured = true
        }
    }

    private inner class ScaleListener : SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scaleFactor *= detector.scaleFactor
            scaleFactor = max(0.5f, min(scaleFactor, 2.0f))

            imageView.scaleX = scaleFactor
            imageView.scaleY = scaleFactor
            return true
        }

        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            isPinching = true
            return true
        }

        override fun onScaleEnd(detector: ScaleGestureDetector) {
            isPinching = false
        }
    }

    private inner class DoubleTapListener : GestureDetector.SimpleOnGestureListener() {
        override fun onDoubleTap(e: MotionEvent): Boolean {
            // Reset scale factor back to baseline
            scaleFactor = 1.0f

            // Animate the view back to its baseline size and original coordinates
            imageView.animate()
                .scaleX(1.0f)
                .scaleY(1.0f)
                .x(originalX)
                .y(originalY)
                .setDuration(250) // 250ms smooth transition
                .start()
            return true
        }
    }

    private fun createTouchListener(): OnTouchListener {
        return object : OnTouchListener {
            var dX: Float = 0f
            var dY: Float = 0f

            @SuppressLint("ClickableViewAccessibility")
            override fun onTouch(view: View, event: MotionEvent): Boolean {
                // Pass touch details directly into our gesture engines
                scaleGestureDetector.onTouchEvent(event)
                doubleTapDetector.onTouchEvent(event)

                if (event.pointerCount > 1) {
                    isPinching = true
                }

                when (event.actionMasked) {
                    MotionEvent.ACTION_DOWN -> {
                        dX = view.x - event.rawX
                        dY = view.y - event.rawY
                    }

                    MotionEvent.ACTION_MOVE -> {
                        if (!isPinching) {
                            view.animate()
                                .x(event.rawX + dX)
                                .y(event.rawY + dY)
                                .setDuration(0)
                                .start()
                        }
                    }

                    MotionEvent.ACTION_UP -> {
                        view.performClick()
                        isPinching = false
                    }

                    MotionEvent.ACTION_POINTER_UP -> {
                        if (event.pointerCount <= 2) {
                            isPinching = false
                            val remainingIdx = if (event.actionIndex == 0) 1 else 0
                            dX = view.x - event.getRawX(remainingIdx)
                            dY = view.y - event.getRawY(remainingIdx)
                        }
                    }
                }
                return true
            }
        }
    }
}
