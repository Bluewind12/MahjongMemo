package momonyan.mahjongmemo

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.name_setting_dialog.view.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
            val dialog = AlertDialog.Builder(this)
            val dialog_view = layoutInflater.inflate(R.layout.name_setting_dialog, null)


            dialog.setView(dialog_view)
                .setPositiveButton("OK") { _, _ ->
                    nameTextView.text = dialog_view.input1.text.toString()
                    nameTextView2.text = dialog_view.input2.text.toString()
                    nameTextView3.text = dialog_view.input3.text.toString()
                    nameTextView4.text = dialog_view.input4.text.toString()
                }
                .show()

        }
    }
}
