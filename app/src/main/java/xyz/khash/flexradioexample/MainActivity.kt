package xyz.khash.flexradioexample

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.core.view.get
import xyz.khash.flexboxradiogroup.FlexboxRadioGroup

class MainActivity : AppCompatActivity(), View.OnClickListener,
    FlexboxRadioGroup.OnCheckedChangeListener {

    private lateinit var flexRadioGroup: FlexboxRadioGroup
    private var counter = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.button_add).setOnClickListener(this)
        findViewById<Button>(R.id.button_sub).setOnClickListener(this)
        findViewById<Button>(R.id.button_reset).setOnClickListener(this)

        flexRadioGroup = findViewById(R.id.flex_group)
        flexRadioGroup.setOnCheckedChangeListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.button_add -> {
                flexRadioGroup.addView(getRadioButton())
            }
            R.id.button_sub -> {
                if (flexRadioGroup.childCount > 0) {
                    flexRadioGroup.removeView(flexRadioGroup[flexRadioGroup.childCount - 1])
                }
            }

            R.id.button_reset -> {
                flexRadioGroup.clearCheck()
            }
        }
    }

    private fun getRadioButton(): AppCompatRadioButton {
        val button = AppCompatRadioButton(this)
        button.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        button.text = "Radio $counter"
        button.id = counter
        counter++
        return button
    }

    override fun onCheckedChanged(group: FlexboxRadioGroup?, checkedId: Int) {
        Toast.makeText(
            this, "Checked radio button id = $checkedId",
            Toast.LENGTH_SHORT
        ).show()
    }
}