### CountDownTextView

倒计时文本控件，适合做短信验证码倒计时，启动页/广告页倒计时等等          
已做防内存泄漏处理，并可设置在倒计时期间无法点击 ;          
可设置页面关闭后再次开启，倒计时依然精确运行      
可正向计时和倒计时,
一个文件复制使用，就不用浪费时间依赖了

```
binding.tvCountDown
            .setNormalText("获取验证码")
            .setCountDownText("重新获取(%sS)")
            .setCloseKeepCountDown(true) //关闭页面保持倒计时开关
            .setCountDownClickable(false) //倒计时期间点击事件是否生效开关
            .setShowFormatTime(false) //是否格式化时间
            .setIntervalUnit(TimeUnit.SECONDS)
            .setOnCountDownStartListener {
                Toast.makeText(
                    this@MainActivity,
                    "开始计时",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .setOnCountDownTickListener { untilFinished ->
                Log.d(
                    "countdown",
                    "onTick: $untilFinished"
                )
            }
            .setOnCountDownFinishListener {
                Toast.makeText(
                    this@MainActivity,
                    "倒计时完毕",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .setOnClickListener {
                Toast.makeText(this@MainActivity, "短信已发送", Toast.LENGTH_SHORT).show()
                binding.tvCountDown.startCountDown(60)
            }
        
```

