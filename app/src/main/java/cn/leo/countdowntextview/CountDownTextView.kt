package cn.leo.countdowntextview

import android.annotation.SuppressLint
import android.content.Context
import android.os.CountDownTimer
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * create by : Jarry Leo
 * date : 2018/7/26 10:19
 * update : 2023/8/25 11:49
 */
@SuppressLint("UNUSED")
class CountDownTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr), DefaultLifecycleObserver,
    View.OnClickListener {
    private var mCountDownTimer: CountDownTimer? = null
    private var mOnCountDownStartListener: OnCountDownStartListener? = null
    private var mOnCountDownTickListener: OnCountDownTickListener? = null
    private var mOnCountDownFinishListener: OnCountDownFinishListener? = null
    private var mNormalText: String? = null
    private var mCountDownText: String? = null
    private var mOnClickListener: OnClickListener? = null

    /**
     * 倒计时期间是否允许点击
     */
    private var mClickable = false

    /**
     * 页面关闭后倒计时是否保持，再次开启倒计时继续；
     */
    private var mCloseKeepCountDown = false

    /**
     * 是否把时间格式化成时分秒
     */
    private var mShowFormatTime = false

    /**
     * 倒计时间隔
     */
    private var mIntervalUnit = TimeUnit.SECONDS

    init {
        init(context)
    }

    private fun init(context: Context) {
        autoBindLifecycle(context)
    }

    /**
     * 控件自动绑定生命周期,宿主可以是activity或者fragment
     */
    private fun autoBindLifecycle(context: Context) {
        if (context is FragmentActivity) {
            val fm = context.supportFragmentManager
            fm.fragments.find {
                it.view?.findViewById<View>(id) === this
            }?.lifecycle?.addObserver(this)
        }
        if (context is LifecycleOwner) {
            (context as LifecycleOwner).lifecycle.addObserver(this)
        }
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        if (mCountDownTimer == null) {
            checkLastCountTimestamp()
        }
    }

        private fun onDestroy() {
        if (mCountDownTimer != null) {
            mCountDownTimer?.cancel()
            mCountDownTimer = null
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        onDestroy()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        onDestroy()
    }

    /**
     * 非倒计时状态文本
     *
     * @param normalText 文本
     */
    fun setNormalText(normalText: String?): CountDownTextView {
        mNormalText = normalText
        text = normalText
        return this
    }

    /**
     * 设置倒计时文本内容
     *
     * @param front  倒计时文本前部分
     * @param latter 倒计时文本后部分
     */
    fun setCountDownText(front: String, latter: String): CountDownTextView {
        mCountDownText = "$front%1\$s$latter"
        return this
    }

    /**
     * 设置倒计时文本内容
     *
     * @param text  倒计时文本,需包含%s作为倒计时文本占位符
     */
    fun setCountDownText(text: String): CountDownTextView {
        mCountDownText = text
        return this
    }

    /**
     * 设置倒计时间隔
     *
     * @param intervalUnit
     */
    fun setIntervalUnit(intervalUnit: TimeUnit): CountDownTextView {
        mIntervalUnit = intervalUnit
        return this
    }

    /**
     * 顺序计时，非倒计时
     *
     * @param second 计时时间秒
     */
    fun startCount(second: Long) {
        startCount(second, TimeUnit.SECONDS)
    }

    fun startCount(time: Long, timeUnit: TimeUnit) {
        if (mCloseKeepCountDown && checkLastCountTimestamp()) {
            return
        }
        count(time, 0, timeUnit, false)
    }

    /**
     * 默认按秒倒计时
     *
     * @param second 多少秒
     */
    fun startCountDown(second: Long) {
        startCountDown(second, TimeUnit.SECONDS)
    }

    fun startCountDown(time: Long, timeUnit: TimeUnit) {
        if (mCloseKeepCountDown && checkLastCountTimestamp()) {
            return
        }
        count(time, 0, timeUnit, true)
    }

    /**
     * 计时方案
     *
     * @param time        计时时长
     * @param timeUnit    时间单位
     * @param isCountDown 是否是倒计时，false正向计时
     */
    private fun count(time: Long, offset: Long, timeUnit: TimeUnit, isCountDown: Boolean) {
        mCountDownTimer?.cancel()
        mCountDownTimer = null
        isEnabled = mClickable
        val millisInFuture = timeUnit.toMillis(time) + 500
        val interval = TimeUnit.MILLISECONDS.convert(1, mIntervalUnit)
        if (mCloseKeepCountDown && offset == 0L) {
            setLastCountTimestamp(millisInFuture, interval, isCountDown)
        }
        if (offset == 0L) {
            mOnCountDownStartListener?.onStart()
        }
        if (TextUtils.isEmpty(mCountDownText)) {
            mCountDownText = text.toString()
        }
        mCountDownTimer = object : CountDownTimer(millisInFuture, interval) {
            override fun onTick(millisUntilFinished: Long) {
                val count =
                    if (isCountDown) millisUntilFinished else millisInFuture - millisUntilFinished + offset
                val l = timeUnit.convert(count, TimeUnit.MILLISECONDS)
                val showTime: String = if (mShowFormatTime) {
                    generateTime(count)
                } else {
                    l.toString()
                }
                text = try {
                    val countDownText = mCountDownText
                    if (countDownText.isNullOrBlank()){
                        showTime
                    } else {
                        String.format(countDownText, showTime)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    showTime
                }
                mOnCountDownTickListener?.onTick(l)
            }

            override fun onFinish() {
                isEnabled = true
                mCountDownTimer = null
                text = mNormalText
                mOnCountDownFinishListener?.onFinish()
            }
        }
        mCountDownTimer?.start()
    }

    fun setOnCountDownStartListener(onCountDownStartListener: OnCountDownStartListener?): CountDownTextView {
        mOnCountDownStartListener = onCountDownStartListener
        return this
    }

    fun setOnCountDownTickListener(onCountDownTickListener: OnCountDownTickListener?): CountDownTextView {
        mOnCountDownTickListener = onCountDownTickListener
        return this
    }

    fun setOnCountDownFinishListener(onCountDownFinishListener: OnCountDownFinishListener?): CountDownTextView {
        mOnCountDownFinishListener = onCountDownFinishListener
        return this
    }

    /**
     * 倒计时期间，点击事件是否响应
     *
     * @param clickable 是否响应
     */
    fun setCountDownClickable(clickable: Boolean): CountDownTextView {
        mClickable = clickable
        return this
    }

    /**
     * 关闭页面是否保持倒计时
     *
     * @param keep 是否保持
     */
    fun setCloseKeepCountDown(keep: Boolean): CountDownTextView {
        mCloseKeepCountDown = keep
        return this
    }

    /**
     * 是否格式化时间
     *
     * @param formatTime 是否格式化
     */
    fun setShowFormatTime(formatTime: Boolean): CountDownTextView {
        mShowFormatTime = formatTime
        return this
    }

    fun interface OnCountDownStartListener {
        /**
         * 计时开始回调;反序列化时不会回调
         */
        fun onStart()
    }

    fun interface OnCountDownTickListener {
        /**
         * 计时回调
         *
         * @param untilFinished 剩余时间,单位为开始计时传入的单位
         */
        fun onTick(untilFinished: Long)
    }

    fun interface OnCountDownFinishListener {
        /**
         * 计时结束回调
         */
        fun onFinish()
    }

    override fun setOnClickListener(l: OnClickListener?) {
        mOnClickListener = l
        super.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        if (mCountDownTimer != null && !mClickable) {
            return
        }
        mOnClickListener?.onClick(v)
    }

    /**
     * 持久化
     *
     * @param time        倒计时时长
     * @param interval    倒计时间隔
     * @param isCountDown 是否是倒计时而不是正向计时
     */
    @SuppressLint("ApplySharedPref")
    private fun setLastCountTimestamp(time: Long, interval: Long, isCountDown: Boolean) {
        context
            .getSharedPreferences(SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE)
            .edit()
            .putLong(SHARED_PREFERENCES_FIELD_TIME + id, time)
            .putLong(
                SHARED_PREFERENCES_FIELD_TIMESTAMP + id,
                Calendar.getInstance().timeInMillis + time
            )
            .putLong(SHARED_PREFERENCES_FIELD_INTERVAL + id, interval)
            .putBoolean(SHARED_PREFERENCES_FIELD_COUNTDOWN + id, isCountDown)
            .commit()
    }

    /**
     * 检查持久化参数
     *
     * @return 是否要保持持久化计时
     */
    private fun checkLastCountTimestamp(): Boolean {
        val sp = context.getSharedPreferences(SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE)
        val lastCountTimestamp = sp.getLong(SHARED_PREFERENCES_FIELD_TIMESTAMP + id, -1)
        val nowTimeMillis = Calendar.getInstance().timeInMillis
        val diff = lastCountTimestamp - nowTimeMillis
        if (diff <= 0) {
            return false
        }
        val time = sp.getLong(SHARED_PREFERENCES_FIELD_TIME + id, -1)
        val interval = sp.getLong(SHARED_PREFERENCES_FIELD_INTERVAL + id, -1)
        val isCountDown = sp.getBoolean(SHARED_PREFERENCES_FIELD_COUNTDOWN + id, true)
        for (timeUnit in TimeUnit.values()) {
            val convert = timeUnit.convert(interval, TimeUnit.MILLISECONDS)
            if (convert == 1L) {
                val last = timeUnit.convert(diff, TimeUnit.MILLISECONDS)
                val offset = time - diff
                count(last, offset, timeUnit, isCountDown)
                return true
            }
        }
        return false
    }

    companion object {
        private const val SHARED_PREFERENCES_FILE = "CountDownTextView"
        private const val SHARED_PREFERENCES_FIELD_TIME = "last_count_time"
        private const val SHARED_PREFERENCES_FIELD_TIMESTAMP = "last_count_timestamp"
        private const val SHARED_PREFERENCES_FIELD_INTERVAL = "count_interval"
        private const val SHARED_PREFERENCES_FIELD_COUNTDOWN = "is_countdown"

        /**
         * 将毫秒转时分秒
         */
        @SuppressLint("DefaultLocale")
        fun generateTime(time: Long): String {
            val format: String
            val totalSeconds = (time / 1000).toInt()
            val seconds = totalSeconds % 60
            val minutes = totalSeconds / 60 % 60
            val hours = totalSeconds / 3600
            format = if (hours > 0) {
                String.format("%02d时%02d分%02d秒", hours, minutes, seconds)
            } else if (minutes > 0) {
                String.format("%02d分%02d秒", minutes, seconds)
            } else {
                String.format("%2d秒", seconds)
            }
            return format
        }
    }
}