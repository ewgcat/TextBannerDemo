package com.lishuaihua.textbanner

import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.ViewFlipper
import androidx.annotation.AnimRes
import com.superluo.textbannerlibrary.R

class TextBannerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : RelativeLayout(context, attrs) {
    private var mViewFlipper: ViewFlipper? = null
    private var mInterval = 1000

    /**
     * 文字切换时间间隔,默认3s
     */
    private var isSingleLine = false

    /**
     * 文字是否为单行,默认false
     */
    private var mTextColor = -0x1000000

    /**
     * 设置文字颜色,默认黑色
     */
    private var mTextSize = 14

    /**
     * 设置文字尺寸,默认16px
     */
    private var mGravity = Gravity.LEFT or Gravity.CENTER_VERTICAL
    private var hasSetDirection = false
    private var direction = DIRECTION_BOTTOM_TO_TOP

    @AnimRes
    private var inAnimResId = R.anim.anim_right_in

    @AnimRes
    private var outAnimResId = R.anim.anim_left_out
    private var hasSetAnimDuration = false
    private var animDuration = 1000

    /**
     * 默认1.5s
     */
    private var mFlags = -1
    private var mTypeface = Typeface.NORMAL
    private var mDatas: List<String>? = null
    private var isStarted = false
    private var isDetachedFromWindow = false

    /**
     * 初始化控件
     */
    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TextBannerViewStyle, defStyleAttr, 0)
        mInterval = typedArray.getInteger(R.styleable.TextBannerViewStyle_setInterval, mInterval) //文字切换时间间隔
        isSingleLine = typedArray.getBoolean(R.styleable.TextBannerViewStyle_setSingleLine, false) //文字是否为单行
        mTextColor = typedArray.getColor(R.styleable.TextBannerViewStyle_setTextColor, mTextColor) //设置文字颜色
        if (typedArray.hasValue(R.styleable.TextBannerViewStyle_setTextSize)) { //设置文字尺寸
            mTextSize = typedArray.getDimension(R.styleable.TextBannerViewStyle_setTextSize, mTextSize.toFloat()).toInt()
            mTextSize = DisplayUtils.px2sp(context, mTextSize.toFloat())
        }
        val gravityType = typedArray.getInt(R.styleable.TextBannerViewStyle_setGravity, GRAVITY_LEFT) //显示位置
        when (gravityType) {
            GRAVITY_LEFT -> mGravity = Gravity.LEFT or Gravity.CENTER_VERTICAL
            GRAVITY_CENTER -> mGravity = Gravity.CENTER
            GRAVITY_RIGHT -> mGravity = Gravity.RIGHT or Gravity.CENTER_VERTICAL
        }
        hasSetAnimDuration = typedArray.hasValue(R.styleable.TextBannerViewStyle_setAnimDuration)
        animDuration = typedArray.getInt(R.styleable.TextBannerViewStyle_setAnimDuration, animDuration) //动画时间
        hasSetDirection = typedArray.hasValue(R.styleable.TextBannerViewStyle_setDirection)
        direction = typedArray.getInt(R.styleable.TextBannerViewStyle_setDirection, direction) //方向
        if (hasSetDirection) {
            when (direction) {
                DIRECTION_BOTTOM_TO_TOP -> {
                    inAnimResId = R.anim.anim_bottom_in
                    outAnimResId = R.anim.anim_top_out
                }
                DIRECTION_TOP_TO_BOTTOM -> {
                    inAnimResId = R.anim.anim_top_in
                    outAnimResId = R.anim.anim_bottom_out
                }
                DIRECTION_RIGHT_TO_LEFT -> {
                    inAnimResId = R.anim.anim_right_in
                    outAnimResId = R.anim.anim_left_out
                }
                DIRECTION_LEFT_TO_RIGHT -> {
                    inAnimResId = R.anim.anim_left_in
                    outAnimResId = R.anim.anim_right_out
                }
            }
        } else {
            inAnimResId = R.anim.anim_right_in
            outAnimResId = R.anim.anim_left_out
        }
        mFlags = typedArray.getInt(R.styleable.TextBannerViewStyle_setFlags, mFlags) //字体划线
        mFlags = when (mFlags) {
            STRIKE -> Paint.STRIKE_THRU_TEXT_FLAG or Paint.ANTI_ALIAS_FLAG
            UNDER_LINE -> Paint.UNDERLINE_TEXT_FLAG or Paint.ANTI_ALIAS_FLAG
            else -> 0 or Paint.ANTI_ALIAS_FLAG
        }
        mTypeface = typedArray.getInt(R.styleable.TextBannerViewStyle_setTypeface, mTypeface) //字体样式
        when (mTypeface) {
            TYPE_BOLD -> mTypeface = Typeface.BOLD
            TYPE_ITALIC -> mTypeface = Typeface.ITALIC
            TYPE_ITALIC_BOLD -> mTypeface = Typeface.ITALIC or Typeface.BOLD
            else -> {
            }
        }
        mViewFlipper = ViewFlipper(getContext()) //new 一个ViewAnimator
        mViewFlipper!!.layoutParams = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        addView(mViewFlipper)
        startViewAnimator()
    }

    /**
     * 暂停动画
     */
    fun stopViewAnimator() {
        if (isStarted) {
            removeCallbacks(mRunnable)
            isStarted = false
        }
    }

    /**
     * 开始动画
     */
    fun startViewAnimator() {
        if (!isStarted) {
            if (!isDetachedFromWindow) {
                isStarted = true
                postDelayed(mRunnable, mInterval.toLong())
            }
        }
    }

    /**
     * 设置延时间隔
     */
    private val mRunnable = AnimRunnable()

    private inner class AnimRunnable : Runnable {
        override fun run() {
            if (isStarted) {
                setInAndOutAnimation(inAnimResId, outAnimResId)
                mViewFlipper!!.showNext() //手动显示下一个子view。
                postDelayed(this, mInterval + animDuration.toLong())
            } else {
                stopViewAnimator()
            }
        }
    }

    /**
     * 设置进入动画和离开动画
     *
     * @param inAnimResId  进入动画的resID
     * @param outAnimResID 离开动画的resID
     */
    private fun setInAndOutAnimation(@AnimRes inAnimResId: Int, @AnimRes outAnimResID: Int) {
        val inAnim = AnimationUtils.loadAnimation(getContext(), inAnimResId)
        inAnim.duration = animDuration.toLong()
        mViewFlipper!!.inAnimation = inAnim
        val outAnim = AnimationUtils.loadAnimation(getContext(), outAnimResID)
        outAnim.duration = animDuration.toLong()
        mViewFlipper!!.outAnimation = outAnim
    }

    /**
     * 设置数据集合
     */
    fun setDatas(context: Context?, datas: List<String>?) {
        mDatas = datas
        if (DisplayUtils.notEmpty(mDatas)) {
            mViewFlipper!!.removeAllViews()
            for (i in mDatas!!.indices) {
                val textView = TextView(getContext())
                setTextView(textView, i)
                mViewFlipper!!.addView(textView, i) //添加子view,并标识子view位置
            }
        }
    }

    /**
     * 设置TextView
     */
    private fun setTextView(textView: TextView, position: Int) {
        textView.text = mDatas!![position]
        //任意设置你的文字样式，在这里
        textView.isSingleLine = isSingleLine
        textView.ellipsize = TextUtils.TruncateAt.END
        textView.setTextColor(mTextColor)
        textView.textSize = mTextSize.toFloat()
        textView.gravity = mGravity
        textView.paint.flags = mFlags //字体划线
        textView.setTypeface(null, mTypeface) //字体样式
        textView.background = context!!.getDrawable(R.drawable.gray_react_bg)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        isDetachedFromWindow = true
        stopViewAnimator()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        isDetachedFromWindow = false
        startViewAnimator()
    }

    companion object {
        /**
         * 文字显示位置,默认左边居中
         */
        private const val GRAVITY_LEFT = 0
        private const val GRAVITY_CENTER = 1
        private const val GRAVITY_RIGHT = 2
        private const val DIRECTION_BOTTOM_TO_TOP = 0
        private const val DIRECTION_TOP_TO_BOTTOM = 1
        private const val DIRECTION_RIGHT_TO_LEFT = 2
        private const val DIRECTION_LEFT_TO_RIGHT = 3

        /**
         * 文字划线
         */
        private const val STRIKE = 0
        private const val UNDER_LINE = 1

        /**
         * 设置字体类型：加粗、斜体、斜体加粗
         */
        private const val TYPE_NORMAL = 0
        private const val TYPE_BOLD = 1
        private const val TYPE_ITALIC = 2
        private const val TYPE_ITALIC_BOLD = 3
    }

    init {
        init(context, attrs, 0)
    }
}