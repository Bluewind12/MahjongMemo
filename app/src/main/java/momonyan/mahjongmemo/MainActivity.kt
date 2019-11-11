package momonyan.mahjongmemo

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.gridlayout.widget.GridLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.data_add_dialog.view.*
import kotlinx.android.synthetic.main.name_setting_dialog.view.*


class MainActivity : AppCompatActivity() {

    private var names = arrayListOf("プレイヤー1", "プレイヤー2", "プレイヤー3", "プレイヤー4")
    private var points = arrayListOf<Int>()
    private var stageName = "-------"
    private var flowCount = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        dataAddFloatActionButton.setOnClickListener {
            addDataDialogCreate()
        }

    }

    private fun addDataDialogCreate() {
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


        when (stageName.substring(0, 1)) {
            "東" -> dialogView.radioGroup.check(R.id.radioButton)
            "南" -> dialogView.radioGroup.check(R.id.radioButton2)
            "西" -> dialogView.radioGroup.check(R.id.radioButton3)
            "北" -> dialogView.radioGroup.check(R.id.radioButton4)
        }

        when (stageName.substring(1, 2)) {
            "1" -> dialogView.radioGroup2.check(R.id.radioButton5)
            "2" -> dialogView.radioGroup2.check(R.id.radioButton6)
            "3" -> dialogView.radioGroup2.check(R.id.radioButton7)
            "4" -> dialogView.radioGroup2.check(R.id.radioButton8)
        }

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


                //点数表示
                val pointTexts = arrayListOf<TextView>()
                for (i in 0 until points.size) {
                    val textView = TextView(this)
                    textView.text = points[i].toString()
                    if (points[i] < 0) {
                        textView.setTextColor(Color.RED)
                    }
                    val gridParams =
                        GridLayout.LayoutParams(
                            GridLayout.spec(flowCount),
                            GridLayout.spec(i + 1, 1.0f)
                        )
                    textView.width = 0
                    textView.gravity = Gravity.END
                    mainGridLayout.addView(textView, gridParams)
                    pointTexts.add(textView)
                }


                //局数の表示
                val checkedId = dialogView.radioGroup.checkedRadioButtonId
                val stageTitleText = if (-1 != checkedId) {
                    dialogView.findViewById<RadioButton>(checkedId).text.toString()
                } else {
                    ""
                }
                val checkedId2 = dialogView.radioGroup2.checkedRadioButtonId
                val stageNumberText = if (-1 != checkedId2) {
                    dialogView.findViewById<RadioButton>(checkedId2).text.toString()
                } else {
                    ""
                }

                val stageTextView = TextView(this)
                stageTextView.text = stageTitleText + stageNumberText

                val gridParams = GridLayout.LayoutParams(
                    GridLayout.spec(flowCount),
                    GridLayout.spec(0, 1.0f)
                )
                stageTextView.width = 0
                mainGridLayout.addView(stageTextView, gridParams)

                //直りん
                stageName = stageTitleText + stageNumberText

                flowCount++

                stageTextView.setOnClickListener {
                    changeDataDialogCreate(pointTexts, stageTextView)
                }
            }
            .show()
    }

    private fun changeDataDialogCreate(arrayText: ArrayList<TextView>, stageText: TextView) {
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

        for (index in 0 until dataInputTextViews.size) {
            dataInputTextViews[index].setText(arrayText[index].text, TextView.BufferType.EDITABLE)
        }
        when (stageText.text.toString().substring(0, 1)) {
            "東" -> dialogView.radioGroup.check(R.id.radioButton)
            "南" -> dialogView.radioGroup.check(R.id.radioButton2)
            "西" -> dialogView.radioGroup.check(R.id.radioButton3)
            "北" -> dialogView.radioGroup.check(R.id.radioButton4)
        }

        when (stageText.text.toString().substring(1, 2)) {
            "1" -> dialogView.radioGroup2.check(R.id.radioButton5)
            "2" -> dialogView.radioGroup2.check(R.id.radioButton6)
            "3" -> dialogView.radioGroup2.check(R.id.radioButton7)
            "4" -> dialogView.radioGroup2.check(R.id.radioButton8)
        }

        addDialog.setView(dialogView)
            .setPositiveButton("変更") { _, _ ->
                points = arrayListOf()
                //局数の表示
                val checkedId = dialogView.radioGroup.checkedRadioButtonId
                val stageTitleText = if (-1 != checkedId) {
                    dialogView.findViewById<RadioButton>(checkedId).text.toString()
                } else {
                    ""
                }
                val checkedId2 = dialogView.radioGroup2.checkedRadioButtonId
                val stageNumberText = if (-1 != checkedId2) {
                    dialogView.findViewById<RadioButton>(checkedId2).text.toString()
                } else {
                    ""
                }

                stageText.text = stageTitleText + stageNumberText

                dataInputTextViews.forEach {
                    if (it.text.toString() == "") {
                        points.add(0)
                    } else {
                        points.add(it.text.toString().toInt())
                    }
                }
                for (i in 0 until points.size) {
                    arrayText[i].text = points[i].toString()
                    if (points[i] < 0) {
                        arrayText[i].setTextColor(Color.RED)
                    }
                }


            }
            .show()
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
