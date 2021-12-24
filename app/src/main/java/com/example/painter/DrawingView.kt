package com.example.painter

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class DrawingView(context: Context?, attrs: AttributeSet?) :
    View(context, attrs) {

    private var drawPath: Path
    private var drawPaint: Paint
    private var canvasPaint: Paint
    private var paintColor = Color.rgb(255,0, 0)

    private lateinit var drawCanvas: Canvas
    private lateinit var canvasBitmap: Bitmap

    fun getBitmap(): Bitmap {
        return canvasBitmap
    }

    init {
        drawPath = Path()
        drawPaint = Paint()
        drawPaint.color = paintColor
        drawPaint.isAntiAlias = true
        drawPaint.strokeWidth = 20f
        drawPaint.style = Paint.Style.STROKE
        drawPaint.strokeJoin = Paint.Join.ROUND
        drawPaint.strokeCap = Paint.Cap.ROUND
        canvasPaint = Paint(Paint.DITHER_FLAG)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        canvasBitmap.eraseColor(Color.WHITE);
        drawCanvas = Canvas(canvasBitmap)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawBitmap(canvasBitmap, 0f, 0f, canvasPaint)
        canvas.drawPath(drawPath, drawPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchX = event.x
        val touchY = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                drawPath.moveTo(touchX, touchY)
                drawPath.lineTo(touchX, touchY)
            }
            MotionEvent.ACTION_MOVE -> drawPath.lineTo(touchX, touchY)
            MotionEvent.ACTION_UP -> {
                drawCanvas.drawPath(drawPath, drawPaint)
                drawPath.reset()
            }
            else -> return false
        }
        invalidate()
        return true
    }

    fun setColor(newColor: String) {
        invalidate()
        paintColor = Color.parseColor(newColor)
        drawPaint.color = paintColor
        drawPaint.strokeWidth = 20f
        if (newColor == "#ffffff") {
            drawPaint.strokeWidth = 50f
        }
    }

    fun startNew() {
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR)
        invalidate()
    }
}
