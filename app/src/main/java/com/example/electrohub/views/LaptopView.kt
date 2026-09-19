package com.example.electrohub.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class LaptopView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    enum class Mood {
        OPEN,    // Laptop open, smiley screen
        CLOSED,  // Lid closed, privacy message
        ERROR    // Warning screen with "Don't leave!"
    }

    // Paint objects for different parts
    private val bodyPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val screenPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val lidPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val iconPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val hingePaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var currentMood = Mood.OPEN

    // Rectangles for positioning
    private val screenRect = RectF()
    private val bodyRect = RectF()
    private val lidRect = RectF()

    // Bounce animation for error icon
    private var bounceOffset = 0f
    private var isAnimating = false

    init {
        // Body: dark gray
        bodyPaint.style = Paint.Style.FILL
        bodyPaint.color = Color.parseColor("#37474F")

        // Screen: light blue
        screenPaint.style = Paint.Style.FILL
        screenPaint.color = Color.parseColor("#81D4FA")

        // Lid: silver
        lidPaint.style = Paint.Style.FILL
        lidPaint.color = Color.parseColor("#B0BEC5")

        // Text: dark, bold, centered
        textPaint.style = Paint.Style.FILL
        textPaint.color = Color.parseColor("#263238")
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.textSize = 28f
        textPaint.typeface = Typeface.DEFAULT_BOLD

        // Hinge: medium gray
        hingePaint.style = Paint.Style.FILL
        hingePaint.color = Color.parseColor("#455A64")

        // Icon: orange stroke
        iconPaint.style = Paint.Style.STROKE
        iconPaint.strokeWidth = 4f
        iconPaint.color = Color.parseColor("#FF6D00")
    }

    // Change laptop mood and redraw
    fun setMood(mood: Mood) {
        currentMood = mood
        if (mood == Mood.ERROR) startBounceAnimation() else stopBounceAnimation()
        invalidate() // Triggers onDraw()
    }

    // For login screen: maps password visibility to mood
    fun setPasswordVisible(visible: Boolean) {
        setMood(if (visible) Mood.OPEN else Mood.CLOSED)
    }

    // Looping animation that bounces the error icon
    private fun startBounceAnimation() {
        if (isAnimating) return
        isAnimating = true
        bounceOffset = 0f
        post(object : Runnable {
            override fun run() {
                if (!isAnimating) return
                bounceOffset = if (bounceOffset < 8f) bounceOffset + 0.5f else 0f
                invalidate()
                postDelayed(this, 30)
            }
        })
    }

    private fun stopBounceAnimation() {
        isAnimating = false
        bounceOffset = 0f
    }

    // Calculate screen and body positions based on view size
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val bodyHeight = h * 0.35f
        bodyRect.set(30f, h - bodyHeight - 10f, w - 30f, h - 10f)

        val screenHeight = h * 0.50f
        screenRect.set(40f, 15f, w - 40f, 15f + screenHeight)

        lidRect.set(screenRect)
    }

    // Main draw method — draws screen, hinge, then body
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        when (currentMood) {
            Mood.OPEN -> {
                drawOpenScreen(canvas)
                drawBody(canvas)
            }
            Mood.CLOSED -> {
                drawClosedLid(canvas)
                drawBody(canvas)
            }
            Mood.ERROR -> {
                drawErrorScreen(canvas)
                drawBody(canvas)
            }
        }
        drawHinge(canvas)
    }

    // OPEN state: blue screen with smiley face and "Welcome!"
    private fun drawOpenScreen(canvas: Canvas) {
        canvas.drawRoundRect(screenRect, 20f, 20f, screenPaint)

        val centerX = screenRect.centerX()
        val centerY = screenRect.centerY() - 20f

        // Left eye
        iconPaint.style = Paint.Style.FILL
        iconPaint.color = Color.parseColor("#263238")
        canvas.drawCircle(centerX - 25f, centerY - 15f, 6f, iconPaint)

        // Right eye
        canvas.drawCircle(centerX + 25f, centerY - 15f, 6f, iconPaint)

        // Smile mouth (upward arc)
        iconPaint.style = Paint.Style.STROKE
        iconPaint.strokeWidth = 4f
        val mouthRect = RectF(centerX - 20f, centerY + 5f, centerX + 20f, centerY + 25f)
        canvas.drawArc(mouthRect, 0f, 180f, false, iconPaint)

        iconPaint.style = Paint.Style.STROKE

        canvas.drawText("Welcome!", centerX, screenRect.bottom - 25f, textPaint)
    }

    // CLOSED state: silver lid covering screen with privacy message
    private fun drawClosedLid(canvas: Canvas) {
        canvas.drawRoundRect(lidRect, 20f, 20f, lidPaint)

        // Inner bezel
        val innerLid = Paint(Paint.ANTI_ALIAS_FLAG)
        innerLid.style = Paint.Style.FILL
        innerLid.color = Color.parseColor("#90A4AE")
        val innerRect = RectF(lidRect.left + 15f, lidRect.top + 15f, lidRect.right - 15f, lidRect.bottom - 15f)
        canvas.drawRoundRect(innerRect, 12f, 12f, innerLid)

        // Logo circle
        innerLid.color = Color.parseColor("#CFD8DC")
        canvas.drawCircle(lidRect.centerX(), lidRect.top + 60f, 18f, innerLid)

        // Privacy text
        val lidTextPaint = Paint(textPaint)
        lidTextPaint.textSize = 36f
        lidTextPaint.color = Color.parseColor("#37474F")
        canvas.drawText("Shhh...", lidRect.centerX(), lidRect.centerY() + 10f, lidTextPaint)
    }

    // ERROR state: red screen with bouncing warning icon and "Don't leave!"
    private fun drawErrorScreen(canvas: Canvas) {
        // Red background
        val errorBgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        errorBgPaint.style = Paint.Style.FILL
        errorBgPaint.color = Color.parseColor("#FFCDD2")
        canvas.drawRoundRect(screenRect, 20f, 20f, errorBgPaint)

        val centerX = screenRect.centerX()
        val iconY = screenRect.centerY() - 40f - bounceOffset

        // Warning triangle
        val trianglePath = Path()
        val triangleSize = 35f
        trianglePath.moveTo(centerX, iconY - triangleSize)
        trianglePath.lineTo(centerX + triangleSize, iconY + triangleSize)
        trianglePath.lineTo(centerX - triangleSize, iconY + triangleSize)
        trianglePath.close()

        iconPaint.style = Paint.Style.STROKE
        iconPaint.strokeWidth = 5f
        iconPaint.color = Color.parseColor("#FF6D00")
        canvas.drawPath(trianglePath, iconPaint)

        iconPaint.style = Paint.Style.FILL
        iconPaint.color = Color.parseColor("#FFAB40")
        canvas.drawPath(trianglePath, iconPaint)

        // Exclamation mark
        iconPaint.color = Color.parseColor("#BF360C")
        canvas.drawRoundRect(centerX - 4f, iconY - 15f, centerX + 4f, iconY + 10f, 3f, 3f, iconPaint)
        canvas.drawCircle(centerX, iconY + 20f, 5f, iconPaint)

        iconPaint.style = Paint.Style.STROKE

        // Error text
        val errorTextPaint = Paint(textPaint)
        errorTextPaint.color = Color.parseColor("#BF360C")
        errorTextPaint.textSize = 30f
        canvas.drawText("Don't leave!", centerX, screenRect.bottom - 40f, errorTextPaint)

        errorTextPaint.textSize = 18f
        errorTextPaint.color = Color.parseColor("#E65100")
        canvas.drawText("We'll miss you 💔", centerX, screenRect.bottom - 15f, errorTextPaint)
    }

    // Thin strip connecting screen to body
    private fun drawHinge(canvas: Canvas) {
        val hingeRect = RectF(
            screenRect.left - 5f, screenRect.bottom - 5f,
            screenRect.right + 5f, screenRect.bottom + 15f
        )
        canvas.drawRoundRect(hingeRect, 8f, 8f, hingePaint)
    }

    // Laptop base with keyboard and trackpad
    private fun drawBody(canvas: Canvas) {
        // Main casing
        canvas.drawRoundRect(bodyRect, 18f, 18f, bodyPaint)

        // Top edge highlight
        val highlightPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        highlightPaint.style = Paint.Style.FILL
        highlightPaint.color = Color.parseColor("#546E7A")
        val highlightRect = RectF(bodyRect.left + 5f, bodyRect.top + 5f, bodyRect.right - 5f, bodyRect.top + 12f)
        canvas.drawRoundRect(highlightRect, 6f, 6f, highlightPaint)

        // Keyboard inset
        val keyboardPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        keyboardPaint.style = Paint.Style.FILL
        keyboardPaint.color = Color.parseColor("#263238")
        val keyboardRect = RectF(bodyRect.left + 25f, bodyRect.top + 20f, bodyRect.right - 25f, bodyRect.bottom - 60f)
        canvas.drawRoundRect(keyboardRect, 10f, 10f, keyboardPaint)

        // Key rows (simple lines)
        val keyLinePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        keyLinePaint.style = Paint.Style.FILL
        keyLinePaint.color = Color.parseColor("#455A64")
        val rowHeight = (keyboardRect.bottom - keyboardRect.top) / 5f
        for (i in 1..4) {
            val rowY = keyboardRect.top + rowHeight * i - 3f
            canvas.drawRoundRect(keyboardRect.left + 10f, rowY, keyboardRect.right - 10f, rowY + 5f, 2f, 2f, keyLinePaint)
        }

        // Trackpad
        val trackpadPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        trackpadPaint.style = Paint.Style.FILL
        trackpadPaint.color = Color.parseColor("#90A4AE")
        val trackpadRect = RectF(bodyRect.centerX() - 50f, bodyRect.bottom - 55f, bodyRect.centerX() + 50f, bodyRect.bottom - 15f)
        canvas.drawRoundRect(trackpadRect, 8f, 8f, trackpadPaint)
    }
}