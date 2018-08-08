package cn.leo.countdowntextview;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * create by : Jarry Leo
 * date : 2018/7/26 10:19
 */
public class CountDownTextView extends TextView implements LifecycleObserver, View.OnClickListener {
    private static final String SHARED_PREFERENCES_FILE = "CountDownTextView";
    private static final String SHARED_PREFERENCES_FIELD_TIMESTAMP = "last_count_timestamp";
    private static final String SHARED_PREFERENCES_FIELD_INTERVAL = "count_interval";

    private CountDownTimer mCountDownTimer;
    private OnCountDownTickListener mOnCountDownTickListener;
    private OnCountDownFinishListener mOnCountDownFinishListener;
    private String mNormalText;
    private String mCountDownText;
    private OnClickListener mOnClickListener;
    //倒计时期间是否允许点击
    private boolean mClickable = false;
    //页面关闭后倒计时是否保持，再次开启倒计时继续；持久化存储，不会执行代码
    private boolean mCloseKeepCountDown = false;
    //是否把时间格式化成时分秒
    private boolean mShowFormatTime = false;

    public CountDownTextView(Context context) {
        this(context, null);
    }

    public CountDownTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountDownTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        if (context instanceof LifecycleOwner) {
            ((LifecycleOwner) context).getLifecycle().addObserver(this);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private void onResume() {
        checkLastCountTimestamp();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private void onDestroy() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        onDestroy();
    }

    /**
     * 非倒计时状态文本
     *
     * @param normalText 文本
     */
    public CountDownTextView setNormalText(String normalText) {
        mNormalText = normalText;
        setText(normalText);
        return this;
    }

    /**
     * 设置倒计时文本内容
     *
     * @param front  倒计时文本前部分
     * @param latter 倒计时文本后部分
     */
    public CountDownTextView setCountDownText(String front, String latter) {
        mCountDownText = front + "%1$s" + latter;
        return this;
    }

    /**
     * 顺序计时，非倒计时
     *
     * @param second 计时时间秒
     */
    public void startCount(long second) {
        startCount(second, TimeUnit.SECONDS);
    }

    public void startCount(long second, final TimeUnit timeUnit) {
        count(second, timeUnit, false);
    }

    /**
     * 默认按秒倒计时
     *
     * @param second 多少秒
     */
    public void startCountDown(long second) {
        startCountDown(second, TimeUnit.SECONDS);
    }

    public void startCountDown(long time, final TimeUnit timeUnit) {
        count(time, timeUnit, true);
    }

    /**
     * 计时方案
     *
     * @param time        时间
     * @param timeUnit    时间单位
     * @param isCountDown 是否是倒计时，false正向计时
     */
    private void count(final long time, final TimeUnit timeUnit, final boolean isCountDown) {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }
        final long millisInFuture = timeUnit.toMillis(time) + 500;
        long interval = TimeUnit.MILLISECONDS.convert(1, timeUnit);
        if (mCloseKeepCountDown) {
            setLastCountTimestamp(millisInFuture, interval);
        }
        if (TextUtils.isEmpty(mCountDownText)) {
            mCountDownText = getText().toString();
        }
        mCountDownTimer = new CountDownTimer(millisInFuture, interval) {
            @Override
            public void onTick(long millisUntilFinished) {
                long count = isCountDown ? millisUntilFinished : (millisInFuture - millisUntilFinished);
                long l = timeUnit.convert(count, TimeUnit.MILLISECONDS);
                String showTime;
                if (mShowFormatTime) {
                    showTime = generateTime(count);
                } else {
                    showTime = String.valueOf(l);
                }
                setText(String.format(mCountDownText, showTime));
                if (mOnCountDownTickListener != null) {
                    mOnCountDownTickListener.onTick(l);
                }
            }

            @Override
            public void onFinish() {
                mCountDownTimer = null;
                setText(mNormalText);
                if (mOnCountDownFinishListener != null) {
                    mOnCountDownFinishListener.onFinish();
                }
            }
        };
        mCountDownTimer.start();
    }

    public CountDownTextView setOnCountDownTickListener(OnCountDownTickListener onCountDownTickListener) {
        mOnCountDownTickListener = onCountDownTickListener;
        return this;
    }

    public CountDownTextView setOnCountDownFinishListener(OnCountDownFinishListener onCountDownFinishListener) {
        mOnCountDownFinishListener = onCountDownFinishListener;
        return this;
    }

    /**
     * 倒计时期间，点击事件是否响应
     *
     * @param clickable 是否响应
     */
    public CountDownTextView setCountDownClickable(boolean clickable) {
        mClickable = clickable;
        return this;
    }

    /**
     * 关闭页面是否保持倒计时
     *
     * @param keep 是否保持
     */
    public CountDownTextView setCloseKeepCountDown(boolean keep) {
        mCloseKeepCountDown = keep;
        return this;
    }

    /**
     * 是否格式化时间
     *
     * @param formatTime 是否格式化
     */
    public CountDownTextView setShowFormatTime(boolean formatTime) {
        mShowFormatTime = formatTime;
        return this;
    }

    public interface OnCountDownTickListener {
        void onTick(long untilFinished);
    }

    public interface OnCountDownFinishListener {
        void onFinish();
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        mOnClickListener = l;
        super.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (mCountDownTimer != null && !mClickable) return;
        if (mOnClickListener != null) {
            mOnClickListener.onClick(v);
        }
    }

    @SuppressLint("ApplySharedPref")
    private void setLastCountTimestamp(long time, long interval) {
        getContext()
                .getSharedPreferences(SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE)
                .edit()
                .putLong(SHARED_PREFERENCES_FIELD_TIMESTAMP, Calendar.getInstance().getTimeInMillis() + time)
                .putLong(SHARED_PREFERENCES_FIELD_INTERVAL, interval)
                .commit();

    }

    private void checkLastCountTimestamp() {
        SharedPreferences sp = getContext().getSharedPreferences(SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);
        long lastCountTimestamp = sp.getLong(SHARED_PREFERENCES_FIELD_TIMESTAMP, -1);
        long nowTimeMillis = Calendar.getInstance().getTimeInMillis();
        long diff = lastCountTimestamp - nowTimeMillis;
        if (diff <= 0) {
            return;
        }
        long interval = sp.getLong(SHARED_PREFERENCES_FIELD_INTERVAL, -1);
        for (TimeUnit timeUnit : TimeUnit.values()) {
            long convert = timeUnit.convert(interval, TimeUnit.MILLISECONDS);
            if (convert == 1) {
                long time = timeUnit.convert(diff, TimeUnit.MILLISECONDS);
                startCountDown(time, timeUnit);
                return;
            }
        }
    }

    /**
     * 将毫秒转时分秒
     */
    @SuppressLint("DefaultLocale")
    public static String generateTime(long time) {
        String format;
        int totalSeconds = (int) (time / 1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        if (hours > 0) {
            format = String.format("%02d时%02d分%02d秒", hours, minutes, seconds);
        } else if (minutes > 0) {
            format = String.format("%02d分%02d秒", minutes, seconds);
        } else {
            format = String.format("%2d秒", seconds);
        }
        return format;
    }
}
