package momonyan.mahjongmemo

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.data_add_dialog.view.*
import kotlinx.android.synthetic.main.name_setting_dialog.view.*

class MainActivity : AppCompatActivity() {

    private var names = arrayListOf("プレイヤー1", "プレイヤー2", "プレイヤー3", "プレイヤー4")
    private var points = arrayListOf<Int>()
    private var stageCount = 1
    private var stageCountDiff = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        dataAddFloatActionButton.setOnClickListener {
            val addDialog = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.data_add_dialog, null)
            dialogView.dataAddNameTextView.text = names[0]
            dialogView.dataAddNameTextView2.text = names[1]
            dialogView.dataAddNameTextView3.text = names[2]
            dialogView.dataAddNameTextView4.text = names[3]

            val dataInputTextViews = arrayListOf(
                dialogView.dataAddTextInput,
                dialogView.dataAddTextInput2,
                dialogView.dataAddTextInput3,
                dialogView.dataAddTextInput4
            )

            addDialog.setView(dialogView)
                .setPositiveButton("追加") { _, _ ->
                    points = arrayListOf()
                    dataInputTextViews.forEach {
                        if (it.text.toString() == "") {
                            points.add(0)
                        } else {
                            points.add(it.text.toString().toInt())
                        }
                    }

                    //局数の表示
                    if (stageCount != stageCountDiff) {
                        val stageTextView = TextView(applicationContext)
                        val stageString = when ((stageCount % 16) / 4) {
                            0 -> "東"
                            1 -> "南"
                            2 -> "西"
                            3 -> "北"
                            else -> "エラー"
                        }
                        stageTextView.text = stageString + (stageCount % 4 + 1) + "局"

                        val gridParams =
                            GridLayout.LayoutParams(
                                GridLayout.spec(stageCount),
                                GridLayout.spec(0)
                            )

                        stageTextView.layoutParams = gridParams
                        mainGridLayout.addView(stageTextView)

                        //直りん
                        stageCountDiff = stageCount
                    }

                    //点数表示
                    for (i in 0 until points.size) {
                        val textView = TextView(applicationContext)
                        textView.text = points[i].toString()
                        if (points[i] < 0) {
                            textView.setTextColor(Color.RED)
                        }
                        val gridParams =
                            GridLayout.LayoutParams(
                                GridLayout.spec(stageCount),
                                GridLayout.spec(i + 1)
                            )
                        textView.layoutParams = gridParams
                        mainGridLayout.addView(textView)
                    }

                    //流れたかどうかの確認
                    if (dialogView.dataAddFlowToggleButton.isChecked) {
                        stageCount++
                    }
                }

        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nameEntry -> {
                val dialog = AlertDialog.Builder(this)
                val dialogView = layoutInflater.inflate(R.layout.name_setting_dialog, null)


                dialog.setView(dialogView)
                    .setPositiveButton("OK") { _, _ ->
                        //データ入れ
                        names = arrayListOf(
                            dialogView.input1.text.toString(),
                            dialogView.input2.text.toString(),
                            dialogView.input3.text.toString(),
                            dialogView.input4.text.toString()
                        )

                        //表示変更
                        nameTextView.text = dialogView.input1.text.toString()
                        nameTextView2.text = dialogView.input2.text.toString()
                        nameTextView3.text = dialogView.input3.text.toString()
                        nameTextView4.text = dialogView.input4.text.toString()
                    }
                    .show()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
