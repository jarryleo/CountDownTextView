package cn.leo.countdowntextview;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

/**
 * create by : Jarry Leo
 * date : 2018/7/26 10:19
 */
public class CountDownTextView extends TextView implements LifecycleObserver, View.OnClickListener {

    private CountDownTimer mCountDownTimer;
    private OnCountDownTickListener mOnCountDownTickListener;
    private OnCountDownFinishListener mOnCountDownFinishListener;
    private String mNormalText;
    private String mCountDownText;
    private OnClickListener mOnClickListener;

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

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private void onDestroy() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        onDestroy();
    }

    /**
     * 非倒计时状态本文
     *
     * @param normalText 文本
     */
    public CountDownTextView setNormalText(String normalText) {
        mNormalText = normalText;
        setText(normalText);
        return this;
    }

    /**
     * 设置倒计时文本内容 时间用 %1$d 表示
     *
     * @param countDownText 倒计时文本
     */
    public CountDownTextView setCountDownText(String countDownText) {
        mCountDownText = countDownText;
        return this;
    }

    /**
     * 默认按秒倒计时
     *
     * @param second 多少秒
     */
    public CountDownTextView startCountDown(long second) {
        startCountDown(second, TimeUnit.SECONDS);
        return this;
    }

    public CountDownTextView startCountDown(long time, final TimeUnit timeUnit) {
        if (mCountDownTimer != null) return this;
        final long millisInFuture = timeUnit.toMillis(time) + 500;
        long interval = TimeUnit.MILLISECONDS.convert(1, timeUnit);
        if (TextUtils.isEmpty(mCountDownText)) {
            mCountDownText = getText().toString();
        }
        mCountDownTimer = new CountDownTimer(millisInFuture, interval) {
            @Override
            public void onTick(long millisUntilFinished) {
                long l = timeUnit.convert(millisUntilFinished, TimeUnit.MILLISECONDS);
                setText(String.format(mCountDownText, l));
                if (mOnCountDownTickListener != null) {
                    mOnCountDownTickListener.onTick(millisUntilFinished);
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
        return this;
    }

    public void setOnCountDownTickListener(OnCountDownTickListener onCountDownTickListener) {
        mOnCountDownTickListener = onCountDownTickListener;
    }

    public void setOnCountDownFinishListener(OnCountDownFinishListener onCountDownFinishListener) {
        mOnCountDownFinishListener = onCountDownFinishListener;
    }

    interface OnCountDownTickListener {
        void onTick(long millisUntilFinished);
    }

    interface OnCountDownFinishListener {
        void onFinish();
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        mOnClickListener = l;
        super.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (mCountDownTimer != null) return;
        if (mOnClickListener != null) {
            mOnClickListener.onClick(v);
        }
    }
}
