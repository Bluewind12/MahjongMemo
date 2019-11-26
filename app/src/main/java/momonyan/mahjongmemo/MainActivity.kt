package momonyan.mahjongmemo

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.InputFilter
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.gridlayout.widget.GridLayout
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.leinardi.android.speeddial.SpeedDialView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.data_add_dialog.view.*
import kotlinx.android.synthetic.main.name_setting_dialog.view.*
import permissions.dispatcher.*
import java.io.File
import java.io.FileOutputStream

@RuntimePermissions
class MainActivity : AppCompatActivity() {

    private var names = arrayListOf("プレイヤー1", "プレイヤー2", "プレイヤー3", "プレイヤー4")
    private var points = arrayListOf<Int>()
    private var stageName = "-------"
    private var flowCount = 1
    private var saveDataArray = arrayListOf<String>()
    private var bufferDataArray = arrayListOf<String>()
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var mInterstitialAd: InterstitialAd
    private var saveCount = 0

    private var sumPoints = arrayListOf(0, 0, 0, 0)

    private var pointTextViews = arrayListOf<TextView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        MobileAds.initialize(this)

        val adRequest = AdRequest.Builder().build()
        mainAdView.loadAd(adRequest)


        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = "ca-app-pub-3940256099942544/1033173712"
        mInterstitialAd.loadAd(AdRequest.Builder().build())

        sharedPreferences =
            getSharedPreferences("MarjongMemoData", Context.MODE_PRIVATE)

        names =
            stringToArray(sharedPreferences.getString("name", "プレイヤー1,プレイヤー2,プレイヤー3,プレイヤー4")!!)
        saveCount = sharedPreferences.getInt("saveCount", 0)
        //表示変更
        nameTextView.text = names[0]
        nameTextView2.text = names[1]
        nameTextView3.text = names[2]
        nameTextView4.text = names[3]

        saveDataArray = stringToArray(sharedPreferences.getString("data", "")!!)
        bufferDataArray = saveDataArray

        //保存データ表示
        for (data in 0 until saveDataArray.size step 5) {
            val stageTextView = TextView(this)
            stageTextView.text = saveDataArray[0 + data]
            pointTextViews.add(stageTextView)
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
                pointTextViews.add(textView)
                textView.text = saveDataArray[i + data + 1]
                sumPoints[i] += saveDataArray[i + data + 1].toInt()
                if (saveDataArray[i + data + 1].toInt() < 0) {
                    textView.setTextColor(Color.RED)
                } else {
                    textView.setTextColor(Color.BLACK)
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
        pointAddCheck()

        //Fab
        dataAddFloatActionButton.inflate(R.menu.fab_menu)
        dataAddFloatActionButton.setOnActionSelectedListener(SpeedDialView.OnActionSelectedListener { actionItem ->
            when (actionItem.id) {
                R.id.fab_set_name -> {
                    val inputFilter = InputFilter { source, _, _, _, _, _ ->
                        if (source.toString().matches("^[,]$".toRegex())) {
                            Toast.makeText(this, "使用できない文字です", Toast.LENGTH_LONG).show()
                            ""
                        } else source
                    }
                    val dialog = AlertDialog.Builder(this)
                    val dialogView = layoutInflater.inflate(R.layout.name_setting_dialog, null)
                    val filters = arrayOf(inputFilter)
                    setNameChangeEditTextData(dialogView.input1, names[0], filters)
                    setNameChangeEditTextData(dialogView.input2, names[1], filters)
                    setNameChangeEditTextData(dialogView.input3, names[2], filters)
                    setNameChangeEditTextData(dialogView.input4, names[3], filters)
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
                            if (mInterstitialAd.isLoaded) {
                                mInterstitialAd.show()
                            }
                        }
                        .show()
                }
                R.id.fab_set_point -> {
                    addDataDialogCreate()
                }
                R.id.fab_output -> {
                    showContactsWithPermissionCheck()
                }
            }
            false
        })

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
                pointTextViews.add(stageTextView)
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
                    pointTextViews.add(textView)
                    textView.text = points[i].toString()
                    sumPoints[i] += points[i]
                    if (points[i] < 0) {
                        textView.setTextColor(Color.RED)
                    } else {
                        textView.setTextColor(Color.BLACK)
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
                pointAddCheck()

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

        //EditText配列
        val dataInputTextViews = arrayListOf(
            dialogView.dataAddTextInput,
            dialogView.dataAddTextInput2,
            dialogView.dataAddTextInput3,
            dialogView.dataAddTextInput4
        )
        val bufferPoints = arrayListOf(0, 0, 0, 0)

        //EditTextへのデータ入力
        for (index in 0 until dataInputTextViews.size) {
            dataInputTextViews[index].setText(arrayText[index].text, TextView.BufferType.EDITABLE)
            bufferPoints[index] = arrayText[index].text.toString().toInt()
        }

        //ラジオボタンの初期設定
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

        //ダイアログ
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
                    sumPoints[i] += points[i]
                    sumPoints[i] -= bufferPoints[i]
                    if (points[i] < 0) {
                        arrayText[i].setTextColor(Color.RED)
                    } else {
                        arrayText[i].setTextColor(Color.BLACK)
                    }
                    arrayText[i].setBackgroundColor(color)
                }
                val pointStrings = arrayListOf<String>()
                for (pointText in pointTextViews) {
                    pointStrings.add(pointText.text.toString())
                    Log.d("DataCheck", pointText.text.toString())
                }
                Log.d("DataCheckAAAA", arrayToString(bufferDataArray))

                bufferDataArray = pointStrings
                val editor = sharedPreferences.edit()
                editor.putString("data", arrayToString(bufferDataArray))

                Log.d("DataCheckAAAA", arrayToString(bufferDataArray))

                editor.apply()
                if (mInterstitialAd.isLoaded) {
                    mInterstitialAd.show()
                }
                pointAddCheck()
            }
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuReset -> {
                val dialog = AlertDialog.Builder(this)
                dialog.setTitle("データリセット")
                    .setMessage("リセットしてもよろしいですか？")
                    .setPositiveButton("リセット") { _, _ ->
                        val edit = sharedPreferences.edit()
                        edit.putString("name", "プレイヤー1,プレイヤー2,プレイヤー3,プレイヤー4")
                        edit.putString("data", "")
                        edit.apply()
                        val intent = Intent(this, MainActivity::class.java)
                        finish()
                        startActivity(intent)
                    }
                    .setNegativeButton("キャンセル", null)
                    .show()
            }
            R.id.menuPrivacyPolicy -> {
                val url = Uri.parse(getString(R.string.privacy_url))
                val intent = Intent(Intent.ACTION_VIEW, url)
                startActivity(intent)
            }
            R.id.menuReview -> {
                val url = Uri.parse(getString(R.string.review_url))
                val intent = Intent(Intent.ACTION_VIEW, url)
                startActivity(intent)
            }
            R.id.menuGensouApp -> {
                val url = Uri.parse(getString(R.string.gensou_url))
                val intent = Intent(Intent.ACTION_VIEW, url)
                startActivity(intent)

            }
            R.id.menuToolApp -> {
                val packageName = "momonyan.mahjongg_tools"
                val className = "momonyan.mahjongg_tools.MainActivity"
                intent.setClassName(packageName, className)
                try {
                    startActivity(intent)
                } catch (e: Exception) {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                        )
                    )
                }
            }
            R.id.menuRecorderApp -> {
                val packageName = "momonyan.mahjongrecorder"
                val className = "momonyan.mahjongrecorder.MainActivity"
                intent.setClassName(packageName, className)
                try {
                    startActivity(intent)
                } catch (e: Exception) {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                        )
                    )
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setNameChangeEditTextData(
        editText: EditText,
        stringData: String,
        filter: Array<InputFilter>
    ) {
        editText.setText(stringData, TextView.BufferType.EDITABLE)
        editText.filters = filter

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

    private fun saveCapture(view: View, file: File) {
        // キャプチャを撮る

        val capture: Bitmap? = getViewCapture(view)

        try {
            val fos = FileOutputStream(file, false)
            // 画像のフォーマットと画質と出力先を指定して保存
            capture?.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
            fos.close()
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put("_data", file.absolutePath)
            }
            contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues
            )
            Toast.makeText(this, "画像を保存しました", Toast.LENGTH_LONG).show()
            saveCount++
            sharedPreferences.edit().putInt("saveCount", saveCount).apply()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "保存を失敗しました", Toast.LENGTH_LONG).show()
        }
        Log.e("TAGTAG", "完成")
    }


    private fun getViewCapture(view: View): Bitmap? {
        view.isDrawingCacheEnabled = true
        // Viewのキャプチャを取得
        val cache = view.drawingCache ?: return null
        val screenShot = Bitmap.createBitmap(cache)
        view.isDrawingCacheEnabled = false
        return screenShot
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // 自動生成された関数にパーミッション・リクエストの結果に応じた処理の呼び出しを委譲
        onRequestPermissionsResult(requestCode, grantResults)
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun showContacts() {
        val file =
            File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + "/Mahjong",
                "$saveCount.jpeg"
            )
        // 指定したファイル名が無ければ作成する。
        Log.d("tagtag", file.toString())
        file.parentFile.mkdir()

        saveCapture(mainGridLayout, file)
    }

    @OnPermissionDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun onContactsDenied() {
        Toast.makeText(this, "「許可しない」が選択されました", Toast.LENGTH_SHORT).show()
    }

    @OnShowRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun showRationaleForContacts(request: PermissionRequest) {
        AlertDialog.Builder(this)
            .setPositiveButton("許可") { _, _ -> request.proceed() }
            .setNegativeButton("許可しない") { _, _ -> request.cancel() }
            .setCancelable(false)
            .setMessage("画像を保存するためにストレージにアクセスする必要があります。")
            .show()
    }

    @OnNeverAskAgain(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun onContactsNeverAskAgain() {
        Toast.makeText(this, "「今後表示しない」が選択されました", Toast.LENGTH_SHORT).show()
    }

    private fun pointAddCheck() {
        //Title
        sumPointTitleTextView.layoutParams =
            GridLayout.LayoutParams(
                GridLayout.spec(flowCount + 1),
                GridLayout.spec(0, 1.0f)
            )


        val sumPointTextViews =
            arrayListOf(sumPointTextView, sumPointTextView2, sumPointTextView3, sumPointTextView4)

        for (i in 0 until sumPointTextViews.size) {
            sumPointTextViews[i].layoutParams =
                GridLayout.LayoutParams(
                    GridLayout.spec(flowCount + 1),
                    GridLayout.spec(i + 1, 1.0f)
                )
            sumPointTextViews[i].text = sumPoints[i].toString()
            if (sumPoints[i] < 0) {
                sumPointTextViews[i].setTextColor(Color.RED)
            } else {
                sumPointTextViews[i].setTextColor(Color.BLACK)
            }
        }
        //PL2
        sumPointTextView2.layoutParams =
            GridLayout.LayoutParams(
                GridLayout.spec(flowCount + 1),
                GridLayout.spec(2, 1.0f)
            )
        sumPointTextView2.text = sumPoints[1].toString()

        //PL3
        sumPointTextView3.layoutParams =
            GridLayout.LayoutParams(
                GridLayout.spec(flowCount + 1),
                GridLayout.spec(3, 1.0f)
            )
        sumPointTextView3.text = sumPoints[2].toString()

        //PL4
        sumPointTextView4.layoutParams =
            GridLayout.LayoutParams(
                GridLayout.spec(flowCount + 1),
                GridLayout.spec(4, 1.0f)
            )
        sumPointTextView4.text = sumPoints[3].toString()

    }
}
