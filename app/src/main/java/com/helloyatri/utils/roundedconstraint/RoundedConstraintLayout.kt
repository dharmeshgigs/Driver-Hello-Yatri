package com.helloyatri.utils.roundedconstraint

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Outline
import android.graphics.Path
import android.graphics.RectF
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import androidx.annotation.ColorInt
import androidx.constraintlayout.widget.ConstraintLayout
import com.helloyatri.R


class RoundedConstraintLayout : ConstraintLayout {

    var path: Path? = null

    /** corner radius */
    var cornerLeftTop: Float = 0F
        set(value) {
            field = value
            postInvalidate()
        }

    var cornerRightTop: Float = 0F
        set(value) {
            field = value
            postInvalidate()
        }

    var cornerLeftBottom: Float = 0F
        set(value) {
            field = value
            postInvalidate()
        }

    var cornerRightBottom: Float = 0F
        set(value) {
            field = value
            postInvalidate()
        }

    var cornerLeftSide: Float = 0F
        set(value) {
            field = value

            if (field != 0F) {
                cornerLeftTop = field
                cornerLeftBottom = field
            }

            postInvalidate()
        }

    var cornerRightSide: Float = 0F
        set(value) {
            field = value

            if (field != 0F) {
                cornerRightTop = field
                cornerRightBottom = field
            }

            postInvalidate()
        }


    var cornerAll: Float = 0F
        set(value) {
            field = value

            if (field != 0F) {
                cornerLeftSide = field
                cornerRightSide = field
            }

            postInvalidate()
        }

    /** background color */
    var backgroundColor: Int? = null
        set(@ColorInt value) {
            field = value
            postInvalidate()
        }

    override fun setBackgroundColor(color: Int) {
        backgroundColor = color
    }

    /** stroke & dash options */
    var strokeLineWidth: Float = 0F
        set(value) {
            field = value
            postInvalidate()
        }

    var strokeLineColor = 0XFFFFFFFF.toInt()
        set(@ColorInt value) {
            field = value
            postInvalidate()
        }

    var dashLineGap: Float = 0F
        set(value) {
            field = value
            postInvalidate()
        }

    var dashLineWidth: Float = 0F
        set(value) {
            field = value
            postInvalidate()
        }

    constructor(
            context: Context,
            attrs: AttributeSet,
            defStyleAttr: Int,
    ) : super(context, attrs, defStyleAttr) {
        render(attrs)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        render(attrs)
    }

    constructor(context: Context) : super(context) {
        render(null)
    }

    private fun render(attrs: AttributeSet?) {
        attrs?.let {
            /** set corner radii */
            with(context.obtainStyledAttributes(it, R.styleable.RoundedConstraintLayout)) {
                cornerLeftTop = getDimensionPixelSize(R.styleable.RoundedConstraintLayout_cornerLeftTop, 0).toFloat()
                cornerRightTop = getDimensionPixelSize(R.styleable.RoundedConstraintLayout_cornerRightTop, 0).toFloat()

                cornerLeftBottom = getDimensionPixelSize(R.styleable.RoundedConstraintLayout_cornerLeftBottom, 0).toFloat()

                cornerRightBottom = getDimensionPixelSize(R.styleable.RoundedConstraintLayout_cornerRightBottom, 0).toFloat()

                backgroundColor = getColor(R.styleable.RoundedConstraintLayout_backgroundColor, Color.WHITE)
                strokeLineWidth = getDimensionPixelSize(R.styleable.RoundedConstraintLayout_strokeLineWidth, 0).toFloat()
                strokeLineColor = getColor(R.styleable.RoundedConstraintLayout_strokeLineColor, Color.BLACK)
                dashLineWidth = getDimensionPixelSize(R.styleable.RoundedConstraintLayout_dashLineWidth, 0).toFloat()
                dashLineGap = getDimensionPixelSize(R.styleable.RoundedConstraintLayout_dashLineGap, 0).toFloat()
                cornerLeftSide = getDimensionPixelSize(R.styleable.RoundedConstraintLayout_cornerLeftSide, 0).toFloat()
                cornerRightSide = getDimensionPixelSize(R.styleable.RoundedConstraintLayout_cornerRightSide, 0).toFloat()
                cornerAll = getDimensionPixelSize(R.styleable.RoundedConstraintLayout_cornerAll, 0).toFloat()
                recycle()
            }
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        /** for outline remake whenenver draw */
        path = null

        if (path == null) {
            path = Path()
        }

        floatArrayOf(cornerLeftTop, cornerLeftTop, cornerRightTop, cornerRightTop, cornerRightBottom, cornerRightBottom, cornerLeftBottom, cornerLeftBottom).let {
            clipPathCanvas(canvas, it)
        }

        /** set drawable resource corner & background & stroke */
        with(GradientDrawable()) {
            cornerRadii = floatArrayOf(cornerLeftTop, cornerLeftTop, cornerRightTop, cornerRightTop, cornerRightBottom, cornerRightBottom, cornerLeftBottom, cornerLeftBottom)

            if (strokeLineWidth != 0F) setStroke(strokeLineWidth.toInt(), strokeLineColor, dashLineWidth, dashLineGap)

            setColor(backgroundColor ?: Color.WHITE)

            background = this
        }

        outlineProvider = outlineProvider

        clipChildren = false

        super.dispatchDraw(canvas)
    }

    private fun clipPathCanvas(canvas: Canvas, floatArray: FloatArray) {
        path?.let {
            it.addRoundRect(RectF(0F, 0F, canvas.width.toFloat(), canvas.height.toFloat()), floatArray, Path.Direction.CW)
            canvas.clipPath(it)
        }
    }

    /** For not showing red underline */
    override fun setOutlineProvider(provider: ViewOutlineProvider?) {
        super.setOutlineProvider(provider)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun getOutlineProvider(): ViewOutlineProvider {
        return object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                path?.let {
                    outline.setConvexPath(it)
                } ?: throw Exception()
            }
        }
    }
}