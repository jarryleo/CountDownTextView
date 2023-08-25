package cn.leo.countdowntextview

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cn.leo.countdowntextview.databinding.ActivityMainBinding
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        count()
    }

    private fun count() {
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
    }
}
