package momonyan.mahjongmemo

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.text.InputFilter
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.gridlayout.widget.GridLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.data_add_dialog.view.*
import kotlinx.android.synthetic.main.name_setting_dialog.view.*


class MainActivity : AppCompatActivity() {

    private var names = arrayListOf("プレイヤー1", "プレイヤー2", "プレイヤー3", "プレイヤー4")
    private var points = arrayListOf<Int>()
    private var stageName = "-------"
    private var flowCount = 1
    private var saveDataArray = arrayListOf<String>()
    private var bufferDataArray = arrayListOf<String>()
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences =
            getSharedPreferences("MarjongMemoData", Context.MODE_PRIVATE)

        names =
            stringToArray(sharedPreferences.getString("name", "プレイヤー1,プレイヤー2,プレイヤー3,プレイヤー4")!!)

        //表示変更
        nameTextView.text = names[0]
        nameTextView2.text = names[1]
        nameTextView3.text = names[2]
        nameTextView4.text = names[3]

        saveDataArray = stringToArray(sharedPreferences.getString("data", "")!!)
        bufferDataArray = saveDataArray
        for (data in 0 until saveDataArray.size step 5) {
            val stageTextView = TextView(this)
            stageTextView.text = saveDataArray[0 + data]
            var color = 0
            when (stageTextView.text.toString().substring(1, 3)) {
                "1局", "3局" -> {
                    color = ContextCompat.getColor(this, R.color.blueBackColor)
                }
                "2局", "4局" -> {
                    color = ContextCompat.getColor(this, R.color.whiteBackColor)
                }
            }

            //点数表示
            val pointTexts = arrayListOf<TextView>()
            for (i in 0 until 4) {
                val textView = TextView(this)
                textView.text = saveDataArray[i + data + 1]
                if (saveDataArray[i + data + 1].toInt() < 0) {
                    textView.setTextColor(Color.RED)
                }
                val gridParams =
                    GridLayout.LayoutParams(
                        GridLayout.spec(flowCount),
                        GridLayout.spec(i + 1, 1.0f)
                    )
                textView.width = 0
                textView.gravity = Gravity.END
                textView.setBackgroundColor(color)
                mainGridLayout.addView(textView, gridParams)
                pointTexts.add(textView)
            }


            val gridParams = GridLayout.LayoutParams(
                GridLayout.spec(flowCount),
                GridLayout.spec(0, 1.0f)
            )
            stageTextView.width = 0
            stageTextView.setBackgroundColor(color)
            mainGridLayout.addView(stageTextView, gridParams)

            //本場表示
            stageName = saveDataArray[0 + data]

            flowCount++

            stageTextView.setOnClickListener {
                changeDataDialogCreate(pointTexts, stageTextView)
            }
        }

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
                var color = 0
                when (stageNumberText) {
                    "1局", "3局" -> {
                        color = ContextCompat.getColor(this, R.color.blueBackColor)
                    }
                    "2局", "4局" -> {
                        color = ContextCompat.getColor(this, R.color.whiteBackColor)
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
                    textView.setBackgroundColor(color)
                    mainGridLayout.addView(textView, gridParams)
                    pointTexts.add(textView)
                }



                val gridParams = GridLayout.LayoutParams(
                    GridLayout.spec(flowCount),
                    GridLayout.spec(0, 1.0f)
                )
                stageTextView.width = 0
                stageTextView.setBackgroundColor(color)
                mainGridLayout.addView(stageTextView, gridParams)

                //本場表示
                stageName = stageTitleText + stageNumberText

                bufferDataArray.add(stageName)
                bufferDataArray.add(points[0].toString())
                bufferDataArray.add(points[1].toString())
                bufferDataArray.add(points[2].toString())
                bufferDataArray.add(points[3].toString())


                val editor = sharedPreferences.edit()
                editor.putString("data", arrayToString(bufferDataArray))
                editor.apply()

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
                var color: Int = 0
                when (stageNumberText) {
                    "1局", "3局" -> {
                        color = ContextCompat.getColor(this, R.color.blueBackColor)
                    }
                    "2局", "4局" -> {
                        color = ContextCompat.getColor(this, R.color.whiteBackColor)
                    }
                }

                stageText.text = stageTitleText + stageNumberText
                stageText.setBackgroundColor(color)
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
                    arrayText[i].setBackgroundColor(color)
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
                val inputFilter = InputFilter { source, _, _, _, _, _ ->
                    if (source.toString().matches("^[,]$".toRegex())) {
                        Toast.makeText(this, "使用できない文字です", Toast.LENGTH_LONG).show()
                        ""
                    } else source
                }
                val dialog = AlertDialog.Builder(this)
                val dialogView = layoutInflater.inflate(R.layout.name_setting_dialog, null)
                val filters = arrayOf(inputFilter)
                dialogView.input1.filters = filters
                dialogView.input2.filters = filters
                dialogView.input3.filters = filters
                dialogView.input4.filters = filters

                dialog.setView(dialogView)
                    .setPositiveButton("OK") { _, _ ->
                        //データ入れ
                        names = arrayListOf(
                            dialogView.input1.text.toString(),
                            dialogView.input2.text.toString(),
                            dialogView.input3.text.toString(),
                            dialogView.input4.text.toString()
                        )

                        for (i in 0 until names.size) {
                            if (names[i] == "") {
                                names[i] = "プレイヤー"
                            }
                        }

                        val editor = sharedPreferences.edit()
                        editor.putString("name", arrayToString(names))
                        editor.apply()

                        //表示変更
                        nameTextView.text = names[0]
                        nameTextView2.text = names[1]
                        nameTextView3.text = names[2]
                        nameTextView4.text = names[3]
                    }
                    .show()
            }
            R.id.menuReset -> {
                val dialog = AlertDialog.Builder(this)
                dialog.setTitle("データリセット")
                    .setMessage("リセットしてもよろしいですか？")
                    .setPositiveButton("リセット") { _, _ ->
                        val edit = sharedPreferences.edit()
                        edit.putString("name", "プレイヤー1,プレイヤー2,プレイヤー3,プレイヤー4")
                        edit.putString("data", "")
                        edit.apply()
                    }
                    .setNegativeButton("キャンセル", null)
                    .show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun arrayToString(array: ArrayList<String>): String {
        val buffer = StringBuffer()
        for (string in array) {
            buffer.append("$string,")
        }
        val buf = buffer.toString()
        return buf.substring(0, buf.length - 1)
    }

    private fun stringToArray(stringData: String): ArrayList<String> {
        if (stringData.isNotEmpty()) {
            return ArrayList(stringData.split(","))
        } else {
            return ArrayList()
        }
    }
}
