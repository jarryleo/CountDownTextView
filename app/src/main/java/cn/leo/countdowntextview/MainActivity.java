package cn.leo.countdowntextview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private CountDownTextView mCountDownTextView;
    private CountDownTextView mCountDownTextView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCountDownTextView = findViewById(R.id.tvCountDown);
        mCountDownTextView2 = findViewById(R.id.tvCountDown2);
        mCountDownTextView.setNormalText("获取验证码1")
                .setCountDownText("重新获取1(", "s)")
                .setCloseKeepCountDown(true)//关闭页面保持倒计时开关
                .setCountDownClickable(false)//倒计时期间点击事件是否生效开关
                .setShowFormatTime(false)//是否格式化时间
                .setOnCountDownFinishListener(new CountDownTextView.OnCountDownFinishListener() {
                    @Override
                    public void onFinish() {
                        Toast.makeText(MainActivity.this, "倒计时完毕", Toast.LENGTH_SHORT).show();
                    }
                })
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MainActivity.this, "短信已发送", Toast.LENGTH_SHORT).show();

                    }
                });
        mCountDownTextView.startCount(30);
        mCountDownTextView2.setNormalText("获取验证码2")
                .setCountDownText("重新获取2(", "s)")
                .setCloseKeepCountDown(true)//关闭页面保持倒计时开关
                .setCountDownClickable(false)//倒计时期间点击事件是否生效开关
                .setShowFormatTime(false)//是否格式化时间
                .setOnCountDownFinishListener(new CountDownTextView.OnCountDownFinishListener() {
                    @Override
                    public void onFinish() {
                        Toast.makeText(MainActivity.this, "倒计时完毕", Toast.LENGTH_SHORT).show();
                    }
                })
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MainActivity.this, "短信已发送", Toast.LENGTH_SHORT).show();

                    }
                });
        mCountDownTextView2.startCountDown(40);
    }
}
