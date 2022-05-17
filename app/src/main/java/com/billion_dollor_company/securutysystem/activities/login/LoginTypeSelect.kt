package com.billion_dollor_company.securutysystem.activities.login

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.billion_dollor_company.securutysystem.R
import com.billion_dollor_company.securutysystem.databinding.ActivityLoginTypeSelectBinding
import com.google.firebase.auth.FirebaseAuth

private lateinit var binding: ActivityLoginTypeSelectBinding


private var selected: Int = -1
const val USER_CODE: Int = 1
const val DEVICE_CODE: Int = 0
const val SELECTED: String = "SELECTED"


class LoginTypeSelect : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginTypeSelectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val isLoggedIn:Boolean = FirebaseAuth.getInstance().currentUser == null

        setSelectedVariable()
        getStarted()
    }

    private fun setSelectedVariable() {
        binding.device.setOnClickListener {
            setSelected(
                image = binding.cameraLoginIcon,
                text = binding.cameraTextTp,
                code = DEVICE_CODE
            )
            deselect(image = binding.userLoginLogo, text = binding.userTextTp)
        }
        binding.user.setOnClickListener {
            setSelected(
                image = binding.userLoginLogo,
                text = binding.userTextTp,
                code = USER_CODE
            )
            deselect(image = binding.cameraLoginIcon, text = binding.cameraTextTp)
        }
    }

    private fun setSelected(image: ImageView, text: TextView, code: Int) {
        image.setColorFilter(
            ContextCompat.getColor(this, R.color.app_blue),
            PorterDuff.Mode.SRC_IN
        )
        text.setTextColor(ContextCompat.getColor(this, R.color.app_blue))
        selected = code
    }

    private fun deselect(image: ImageView, text: TextView) {
        image.setColorFilter(
            ContextCompat.getColor(this, R.color.light_grey),
            PorterDuff.Mode.SRC_IN
        )
        text.setTextColor(ContextCompat.getColor(this, R.color.light_grey))
    }

    private fun getStarted() {
        binding.getStarted.setOnClickListener {
            if (selected == -1) {
                Toast.makeText(this, "Please select type of login", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, Login::class.java).putExtra(SELECTED, selected)
                startActivity(intent)
            }
        }
    }
}