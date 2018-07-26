# CountDownTextView
倒计时文本控件，适合做短信验证码倒计时;          
已做防内存泄漏处理，并在倒计时期间无法点击 ;          
页面关闭后再次开启，倒计时依然精确运行      


```
mCountDownTextView.setNormalText("获取验证码")
                .setCountDownText("重新获取(%1$ds)")
                .setOnCountDownFinishListener(new CountDownTextView.OnCountDownFinishListener() {
                    @Override
                    public void onFinish() {
                        Toast.makeText(MainActivity.this, "倒计时完毕", Toast.LENGTH_SHORT).show();
                    }
                });
        mCountDownTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "短信已发送", Toast.LENGTH_SHORT).show();
                mCountDownTextView.startCountDown(60);
            }
        });
        
```

