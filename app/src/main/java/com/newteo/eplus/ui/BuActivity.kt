package com.newteo.eplus.ui

import android.R
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.*
import android.view.MenuItem
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.AnimationSet
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.newteo.eplus.base.BaseActivity
import com.newteo.eplus.base.Echo
import com.newteo.eplus.base.toast
import com.newteo.eplus.databinding.ActivityBuBinding
import kotlinx.android.synthetic.main.activity_bu.view.*
import java.lang.Math.random
import java.lang.Thread.sleep
import kotlin.concurrent.thread
import kotlin.math.floor

class BuActivity : BaseActivity() {
    private lateinit var binding: ActivityBuBinding

    private val whatIs = 1
    private var step = 0
    private var codes = ArrayList<List<Int>>()
    private var guaList: ArrayList<GuaListItem>? = null
    private var finalGuaList = ArrayList<GuaListItem>()

    private val handler = object : Handler(Looper.getMainLooper()) {
        private var step = 0
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            when(msg.what) {
                whatIs -> {
                    if (step < 6) step += 1 else step = 1
                    var yao = resources.getIdentifier("gua","id", packageName)
                    var which: View = findViewById(yao)
                    which.isVisible = true

                    val result = calculate()
                    when(result[0]) {
                        6 -> {
                            val oldStr = if (result[2] == 6) "Old" else ""
                            yao = resources.getIdentifier("yao${step}Yin${oldStr}","id", packageName)
                            which = findViewById(yao)
                            which.isVisible = true
                        }
                        9 -> {
                            val oldStr = if (result[2] == 9) "Old" else ""
                            yao = resources.getIdentifier("yao${step}Yang${oldStr}","id", packageName)
                            which = findViewById(yao)
                            which.isVisible = true
                        }
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        intent.getParcelableArrayListExtra<GuaListItem>("guaList")?.let {
            guaList = it
        }

        title = "周易天演"
        binding.whatFor.text = "所求何事?"
        binding.once.text = "遇事不明"
        binding.healthy.text = "福寿"
        binding.cause.text = "前程"
        binding.fate.text = "机缘"
        binding.go.text = "起卦"

        binding.go.setOnClickListener {
            "周易天演，走起～".toast()
            VibrationEffect.createOneShot(1800, 6)

            it.go.text = "起卦..."
            initAction()
            handleGua()
            Echo.d("codes", "$codes")
            handleGuaResult()

            AnimatorSet().apply {
                play(handleTip())
                    .before(showFinalGuaList())
                start()
            }

            handleInform()
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.home -> finish()
        }
        return true
    }

    private fun initAction() {
        step = 0
        binding.tip.alpha = (1).toFloat()
        for (i in 1..6) {
            findViewById<View>(
                resources.getIdentifier("yao${i}Yang", "id", packageName)
            ).isVisible = false

            findViewById<View>(
                resources.getIdentifier("yao${i}Yin", "id", packageName)
            ).isVisible = false

            findViewById<View>(
                resources.getIdentifier("yao${i}YangOld", "id", packageName)
            ).isVisible = false

            findViewById<View>(
                resources.getIdentifier("yao${i}YinOld", "id", packageName)
            ).isVisible = false
        }

        codes.clear()
        finalGuaList.clear()
        binding.guaRec.isVisible = false
    }

    private fun showFinalGuaList() : ObjectAnimator {
        val target = binding.guaRec
        target.adapter = BuRecyclerViewAdaptor(finalGuaList)
        target.layoutManager = GridLayoutManager(this,2,RecyclerView.HORIZONTAL, false)
        target.isVisible = true
        target.x = (target.width * 2).toFloat()

        val from = -(target.width * 1.5).toFloat()
        val to = (target.left).toFloat()
        return ObjectAnimator
            .ofFloat(target, "x", from, to).apply {
                duration = 1000
                interpolator = AccelerateInterpolator()
            }
    }

    private fun handleTip() : ObjectAnimator {
        val target = binding.tip
        return ObjectAnimator
            .ofFloat(target, "alpha", (1).toFloat(), (0).toFloat()).apply {
                duration = 800
                interpolator = AccelerateInterpolator()
            }
    }

    private fun handleInform() {

    }

    private fun handleGuaResult() {
        val benCode = codes.map {
            it[0]
        }
        val benGua = matchGua(benCode)
        benGua.title = "${benGua.title}@本卦"

        // 本卦中的第三、四、五爻，拿出来作为互卦的上卦，本卦的第二、三、四爻，拿出来作为互卦的下卦
        val huCode = listOf(benCode[1], benCode[2], benCode[3], benCode[2], benCode[3], benCode[5])
        val huGua = matchGua(huCode)
        huGua.title = "${huGua.title}@互卦"

        val cuoCode = benCode.map {
            if (it == 6) 9 else 6
        }
        val cuoGua = matchGua(cuoCode)
        cuoGua.title = "${cuoGua.title}@错卦"

        val zongCode = benCode.reversed()
        val zongGua = matchGua(zongCode)
        zongGua.title = "${zongGua.title}@综卦"

        val bianGua = getBianGua()
        bianGua.title = "${bianGua.title}@变卦"

        finalGuaList.addAll(listOf(benGua, huGua, cuoGua, zongGua, bianGua))

        val laoGua = getLaoGua()
        var laoBianGua: GuaListItem? = null
        if (laoGua != null) {
            laoGua.title = "${laoGua.title}@老卦"

            laoBianGua = getLaoBianGua(laoGua.copy())
            laoBianGua.title = "${laoBianGua.title}@老变卦"

            finalGuaList.addAll(listOf(laoGua, laoBianGua))
        }

        Echo.d(
            "codes",
            "code: $benCode \n" +
                    "本卦: ${benGua.title}, \n" +
                    "互卦: ${huGua.title}, \n" +
                    "错卦: ${cuoGua.title}, \n" +
                    "综卦: ${zongGua.title}, \n" +
                    "变卦: ${bianGua.title}, \n" +
                    "老卦: ${laoGua?.title}, \n" +
                    "老变卦: ${laoBianGua?.title}"
        )
    }

    private fun getBianGua() : GuaListItem {
        val moveYao = getMoveYao()
        val copyCodes = codes.map {it} as ArrayList
        if (copyCodes[moveYao][0] == 6) {
            copyCodes[moveYao] = listOf(9, copyCodes[moveYao][1])
        } else {
            copyCodes[moveYao] = listOf(6, copyCodes[moveYao][1])
        }

        val code = copyCodes.map {
            it[0]
        }
        return matchGua(code)
    }

    private fun getMoveYao() : Int {
        var score = 0
        codes.forEach {
            score += it[1]
        }

        return score % 6
    }

    private fun getLaoGua() : GuaListItem? {
        val laoCode = ArrayList<Int>()
        var hasLao = false
        codes.forEach {
            val code = it[0]
            val score = it[1]
            if (code == score) {
                hasLao = true
                laoCode.add(if (code == 9) 6 else 9)
            } else {
                laoCode.add(code)
            }
        }

        return if (laoCode.isNotEmpty() && hasLao) matchGua(laoCode) else null
    }

    private fun getLaoBianGua(laoGua: GuaListItem) : GuaListItem {
        val moveYao = getMoveYao()
        val moveYaoCode = laoGua.yaoCodeList[moveYao]
        val laoCode = laoGua.yaoCodeList.map { it } as MutableList

        if (moveYaoCode == 6) {
            laoCode[moveYao] = 9
        } else {
            laoCode[moveYao] = 6
        }

        return matchGua(laoCode)
    }

    private fun matchGua(code: List<Int>) : GuaListItem {
        return guaList?.let {
            it.filter { it2 ->
                it2.yaoCodeList == code
            }
        }!![0].copy()
    }

    private fun handleGua() {
        repeat(6) {
            if (step < 6) step += 1 else step = 1
            var yao = resources.getIdentifier("gua","id", packageName)
            var which: View = findViewById(yao)
            which.isVisible = true

            val result = calculate()
            when(result[0]) {
                6 -> {
                    val oldStr = if (result[1] == 6) "Old" else ""
                    yao = resources.getIdentifier("yao${step}Yin${oldStr}","id", packageName)
                    which = findViewById(yao)
                    which.isVisible = true
                    animate(which)
                }
                9 -> {
                    val oldStr = if (result[1] == 9) "Old" else ""
                    yao = resources.getIdentifier("yao${step}Yang${oldStr}","id", packageName)
                    which = findViewById(yao)
                    which.isVisible = true
                    animate(which)
                }
            }

            codes.add(result)
        }
    }

    private fun calculate(): List<Int> {
        // [code, rd, old] => [6, 2, 0] => [9, 9, 9]

        val rd = random().run {
            (floor(this * 10) + 1).toInt()
        }
        val code = if (rd % 2 == 0) 6 else 9

        val final = when(true) {
            (code == 6 && rd == 6) -> listOf(6, 6)
            (code == 9 && rd == 9) -> listOf(9, 9)
            else -> listOf(code, rd)
        }

        Echo.d("calculate", "random is: $rd result is: $code final is: $final")

        return final
    }

    private fun animate(which: View) {
        val wrapper: View = binding.gua
        val item: View = binding.yao1Yang
        val to = (wrapper.height - wrapper.height * 0.15 - step * item.height * 1.5).toFloat()
        val from = wrapper.top.toFloat() - 600
        Echo.d("ooxx", "from is: $from, to is: $to")
        ObjectAnimator
            .ofFloat(which, "y", from, to).apply {
                duration = (300 * step).toLong()
                interpolator = AccelerateInterpolator()
                start()
            }
    }

    companion object {
        fun go(context: Context, data: ArrayList<GuaListItem>, ) {
            context.startActivity(Intent(context, BuActivity::class.java).run {
                putExtra("guaList", data)
            })
        }
    }

    private fun handleGuaAsync() {
        repeat (6) {
            thread {
                sleep((300 * (it + 1) ).toLong())
                handler.sendMessage(Message().apply {
                    what = whatIs
                })
            }
        }

        binding.go.text = "起卦"

    }

}