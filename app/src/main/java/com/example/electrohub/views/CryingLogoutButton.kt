package com.example.electrohub.views

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import android.view.animation.OvershootInterpolator

class CryingLogoutButton(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val tearPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val rect = RectF()
    private var tearOffset = 0f
    private var isHovering = false
    private var isLongPressing = false
    private var cryAnimation: ValueAnimator? = null
    private var longPressRunnable: Runnable? = null
    private var onClickListener: (() -> Unit)? = null

    private val faceCenterX = 70f
    private val faceCenterY = 40f

    init {
        textPaint.apply {
            color = Color.WHITE
            textSize = 50f
            textAlign = Paint.Align.LEFT
            typeface = Typeface.DEFAULT_BOLD
        }
        tearPaint.apply {
            color = Color.parseColor("#64B5F6")
            style = Paint.Style.FILL
        }
    }

    fun setOnCryingClickListener(listener: () -> Unit) {
        onClickListener = listener
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val isCrying = isHovering || isLongPressing

        // Button background
        rect.set(0f, 0f, width.toFloat(), height.toFloat())
        paint.color = if (isCrying) Color.parseColor("#E53935") else Color.parseColor("#FF6B6B")
        canvas.drawRoundRect(rect, 60f, 60f, paint)

        // Left eye
        paint.color = Color.WHITE
        canvas.drawCircle(faceCenterX - 12f, faceCenterY - 5f, 12f, paint)
        paint.color = Color.BLACK
        canvas.drawCircle(faceCenterX - 12f, faceCenterY - 5f, 6f, paint)

        // Right eye
        paint.color = Color.WHITE
        canvas.drawCircle(faceCenterX + 18f, faceCenterY - 5f, 12f, paint)
        paint.color = Color.BLACK
        canvas.drawCircle(faceCenterX + 18f, faceCenterY - 5f, 6f, paint)

        // Mouth
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 5f
        paint.color = Color.WHITE
        val mouthTop = if (isCrying) faceCenterY + 8f else faceCenterY + 2f
        val mouthRect = RectF(
            faceCenterX - 18f, mouthTop,
            faceCenterX + 24f, mouthTop + 35f
        )
        canvas.drawArc(mouthRect, 0f, 180f, false, paint)
        paint.style = Paint.Style.FILL

        // Tears
        if (isCrying) {
            drawTear(canvas, faceCenterX - 18f, faceCenterY + 8f + tearOffset)
            drawTear(canvas, faceCenterX + 12f, faceCenterY + 6f + tearOffset * 1.3f)
        }

        // Text
        val textX = faceCenterX + 50f
        val textY = faceCenterY + 18f
        val text = when {
            isCrying -> "Don't go! 😭"
            else -> "Logout"
        }
        canvas.drawText(text, textX, textY, textPaint)
    }

    private fun drawTear(canvas: Canvas, x: Float, y: Float) {
        val path = Path()
        path.moveTo(x, y)
        path.cubicTo(x - 8f, y + 20f, x + 8f, y + 20f, x, y)
        path.close()
        canvas.drawPath(path, tearPaint)
    }

    private fun startCrying() {
        cryAnimation?.cancel()
        cryAnimation = ValueAnimator.ofFloat(0f, 25f).apply {
            duration = 800
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            interpolator = OvershootInterpolator()
            addUpdateListener { animation ->
                tearOffset = animation.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    private fun stopCrying() {
        cryAnimation?.cancel()
        tearOffset = 0f
        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // Start long press detection
                longPressRunnable = Runnable {
                    if (!isLongPressing) {
                        isLongPressing = true
                        startCrying()
                        performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                        invalidate()
                    }
                }
                postDelayed(longPressRunnable, 500) // 500ms long press
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                longPressRunnable?.let { removeCallbacks(it) }

                if (isLongPressing) {
                    // Was long pressing — stop crying
                    isLongPressing = false
                    stopCrying()
                    invalidate()
                } else {
                    // Was a normal tap — trigger logout
                    onClickListener?.invoke()
                }
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    // For devices with mouse (tablets, ChromeOS)
    override fun onHoverEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_HOVER_ENTER -> {
                isHovering = true
                startCrying()
                invalidate()
            }
            MotionEvent.ACTION_HOVER_EXIT -> {
                isHovering = false
                stopCrying()
                invalidate()
            }
        }
        return true
    }
}