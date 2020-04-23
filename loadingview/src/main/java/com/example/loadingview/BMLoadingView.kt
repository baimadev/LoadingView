package com.example.loadingview

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator

class BMLoadingView : View {
    constructor(ctx: Context):this(ctx,null,0)
    constructor(ctx: Context,attributeSet: AttributeSet? ):this(ctx,attributeSet,0)
    constructor(ctx: Context,attributes: AttributeSet?,defStyleAttr:Int):super(ctx,attributes,defStyleAttr){
        attributes?.let {
            val typeArray = ctx.obtainStyledAttributes(it,R.styleable.BMLoadingView)
            color1 = typeArray.getColor(R.styleable.BMLoadingView_gradientColor1,resources.getColor(R.color.gradient1))
            color2 = typeArray.getColor(R.styleable.BMLoadingView_gradientColor2,resources.getColor(R.color.gradient2))
            typeArray.recycle()
            initGradientColor(color1,color2)
        }
    }

    private val rectF = RectF()
    private val linePaint = Paint()
    private val circlePaint = Paint()

    private val dp = resources.displayMetrics.density
    private val radius = dp*7
    private var color1 = 0
    private var color2 = 0
    private var shadowColor = resources.getColor(R.color.shadow)
    private val colorList = ArrayList<Int>(7)

    private val startPoint1=PointF()
    private val endPoint1=PointF()
    private val controlPoint1=PointF()
    private var showPoint1=PointF()

    private val startPoint2=PointF()
    private val endPoint2=PointF()
    private val controlPoint2=PointF()
    private var showPoint2=PointF()

    private val conToFirst = 30*dp
    private val defaultWidth = 7*radius*2+2*conToFirst
    private val endToBase = 2*dp
    private val defaultHeight = endToBase+2*radius*2
    private var ovalTop = 0f

    private val animationSet = AnimatorSet()

    private var offWidth = 0f
    private var offHeight = 0f

    init {
        linePaint.run {
            isAntiAlias = true
            linePaint.color = shadowColor
            style = Paint.Style.FILL
        }
        circlePaint.run {
            isAntiAlias = true
            style = Paint.Style.FILL
        }

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val height = (defaultHeight+4*radius).toInt()
        val width = (defaultWidth+radius).toInt()
        setMeasuredDimension(reSize(width,widthMeasureSpec),reSize(height,heightMeasureSpec))
    }

    private fun reSize(default:Int,spec:Int):Int{
        val size = MeasureSpec.getSize(spec)
        return when(MeasureSpec.getMode(spec)){
            MeasureSpec.AT_MOST ->{
                default
            }
            MeasureSpec.EXACTLY ->{
                size
            }
            MeasureSpec.UNSPECIFIED ->{
                default
            }
            else ->{
                default
            }
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        offWidth = (width - defaultWidth)/2f
        offHeight = (height - defaultHeight)*0.3f

        val circleHeight = defaultHeight-radius+offHeight
        showPoint1.set(conToFirst+radius+offWidth,circleHeight)
        startPoint1.set(showPoint1)
        endPoint1.set(radius+offWidth,radius+offHeight)
        controlPoint1.set(conToFirst/2f+offWidth,defaultHeight+offHeight)

        startPoint2.set(defaultWidth-conToFirst-radius+offWidth,circleHeight)
        endPoint2.set(defaultWidth-radius+offWidth,radius+offHeight)
        controlPoint2.set(defaultWidth-conToFirst/2f+offWidth,defaultHeight+offHeight)
        showPoint2.set(startPoint2)

        val availableHeight = (height-defaultHeight)*0.7f
        ovalTop = if(availableHeight<4*radius){
            height-2*dp
        }else{
            offHeight+defaultHeight+4*radius
        }

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        var circleWidth = conToFirst+radius+offWidth

        circlePaint.color = colorList[0]
        canvas!!.drawCircle(showPoint1.x,showPoint1.y,radius,circlePaint)
        rectF.set(showPoint1.x-radius*0.9f,ovalTop,showPoint1.x+radius*0.9f,ovalTop+2*dp)
        canvas.drawOval(rectF,linePaint)

        for(i in 1..5){
            circleWidth += radius*2
            circlePaint.color = colorList[i]
            canvas.drawCircle(circleWidth,offHeight+defaultHeight-radius,radius,circlePaint)
            rectF.set(circleWidth-radius*0.9f,ovalTop,circleWidth+radius*0.9f,ovalTop+2*dp)
            canvas.drawOval(rectF,linePaint)
        }

        circlePaint.color = colorList[6]
        canvas.drawCircle(showPoint2.x,showPoint2.y,radius,circlePaint)
        rectF.set(showPoint2.x-radius*0.9f,ovalTop,showPoint2.x+radius*0.9f,ovalTop+2*dp)
        canvas.drawOval(rectF,linePaint)
    }

    /**
     * 初始化渐变色
     */
    private fun initGradientColor(color1:Int,color2:Int){
        for(i in 0 until 7){
            val part = i/6f
            val r =Color.red(color1)+(Color.red(color2)-Color.red(color1))*part
            val g =Color.green(color1)+(Color.green(color2)-Color.green(color1))*part
            val b =Color.blue(color1)+(Color.blue(color2)-Color.blue(color1))*part
            val a = Color.alpha(color1)
            val color =Color.argb(a,r.toInt(),g.toInt(),b.toInt())
            colorList.add(color)
        }
    }

    fun pauseAnim(){
        if(animationSet.isRunning)animationSet.pause()
    }

    fun beginAnim(){
        //左边小球
        val valueAnimation = ValueAnimator.ofFloat(1f,0f,0f)
        valueAnimation.interpolator = LinearInterpolator()
        valueAnimation.duration=500L
        valueAnimation.repeatCount = ValueAnimator.INFINITE
        valueAnimation.repeatMode = ValueAnimator.REVERSE
        valueAnimation.addUpdateListener {
            showPoint1 = secondBessel(it.animatedValue as Float,startPoint1,controlPoint1,endPoint1)
            postInvalidate()
        }
        //右边小球
        val valueAnimation1 = ValueAnimator.ofFloat(0f,0f,1f)
        valueAnimation1.interpolator = LinearInterpolator()
        valueAnimation1.duration=500L
        valueAnimation1.repeatCount = ValueAnimator.INFINITE
        valueAnimation1.repeatMode = ValueAnimator.REVERSE
        valueAnimation1.addUpdateListener {
            showPoint2 = secondBessel(it.animatedValue as Float,startPoint2,controlPoint2,endPoint2)
            postInvalidate()
        }
        animationSet.play(valueAnimation).with(valueAnimation1)
        animationSet.start()
    }

    /**
     * 二阶贝塞尔函数
     */
    private fun secondBessel(t:Float,startPoint: PointF,controlPoint: PointF,endPoint: PointF):PointF{
        val pointF = PointF()
        val tem =1-t
        pointF.x = startPoint.x*tem*tem+2*t*tem*controlPoint.x+t*t*endPoint.x
        pointF.y = startPoint.y*tem*tem+2*t*tem*controlPoint.y+t*t*endPoint.y
        return pointF
    }

}
