package momonyan.mahjongmemo

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.name_setting_dialog.view.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        dataAddFloatActionButton.setOnClickListener {



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
