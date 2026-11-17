package mobiledev.unb.ca.canvasdemo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.scale

class BubbleContainerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val activeBubbles = mutableListOf<Bubble>()
    private val painter = Paint(Paint.ANTI_ALIAS_FLAG)
    private val scaledBitmap: Bitmap
    private val radius: Float = BITMAP_SIZE / 2f

    init {
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.b64)
        scaledBitmap = bitmap.scale(BITMAP_SIZE, BITMAP_SIZE, false)
    }

    /**
     * Creates multiple bubbles originating from a single point
     */
    fun createBubbles(startX: Float, startY: Float, count: Int) {
        val centerX = startX - radius
        val centerY = startY - radius

        repeat(count) {
            val stepX = generateNonZeroRandomStep(-10, 10)
            val stepY = generateNonZeroRandomStep(-10, 10)
            activeBubbles.add(Bubble(centerX, centerY, stepX, stepY))
        }

        // Kick off loop if it isn't running already
        postInvalidateOnAnimation()
    }

    override fun onDraw(canvas: Canvas) {
        if (activeBubbles.isEmpty()) {
            return
        }

        val iterator = activeBubbles.iterator()
        while (iterator.hasNext()) {
            val bubble = iterator.next()

            // Update location of the bubble
            bubble.x += bubble.stepX
            bubble.y += bubble.stepY

            // Bound Check & Render
            if (isStillOnScreen(bubble)) {
                canvas.drawBitmap(scaledBitmap, bubble.x, bubble.y, painter)
            } else {
                iterator.remove()
            }
        }

        // Keep rendering frames if there are still active bubbles
        if (activeBubbles.isNotEmpty()) {
            postInvalidateOnAnimation()
        }
    }

    private fun isStillOnScreen(bubble: Bubble): Boolean {
        return bubble.x <= width &&
                bubble.x + BITMAP_SIZE >= 0 &&
                bubble.y <= height &&
                bubble.y + BITMAP_SIZE >= 0
    }

    private fun generateNonZeroRandomStep(min: Int, max: Int): Float {
        var step = (min..max).random()
        while (step == 0) {
            step = (min..max).random()
        }
        return step.toFloat()
    }

    companion object {
        private const val BITMAP_SIZE = 64
    }
}
