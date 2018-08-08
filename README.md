# CountDownTextView
倒计时文本控件，适合做短信验证码倒计时，启动页/广告页倒计时等等          
已做防内存泄漏处理，并可设置在倒计时期间无法点击 ;          
可设置页面关闭后再次开启，倒计时依然精确运行      


```
mCountDownTextView.setNormalText("获取验证码")
                .setCountDownText("重新获取(%1$ds)")
                .setCloseKeepCountDown(true)//关闭页面保持倒计时开关
                .setCountDownClickable(true)//倒计时期间点击事件是否生效开关
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
                        mCountDownTextView.startCountDown(60);
                    }
                });
        
```

