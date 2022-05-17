package com.billion_dollor_company.securutysystem.activities.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.billion_dollor_company.securutysystem.activities.login.DEVICE_CODE
import com.billion_dollor_company.securutysystem.activities.login.LoginTypeSelect
import com.billion_dollor_company.securutysystem.activities.login.USER_CODE
import com.billion_dollor_company.securutysystem.activities.main.camera.CameraMainActivity
import com.billion_dollor_company.securutysystem.activities.main.user.UserMainActivity
import com.billion_dollor_company.securutysystem.databinding.ActivitySplashScreenBinding
import com.google.firebase.auth.FirebaseAuth


private lateinit var binding: ActivitySplashScreenBinding

public val SHARED_PREF_NAME = "SHARED_PREF"
public val LOGIN_TYPE = "LOGIN_TYPE"

private var typeOfLogin: Int = -1

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getLoginType()
        setTheTimer()
    }

    private fun signOut() {
        if (FirebaseAuth.getInstance().currentUser != null)
            FirebaseAuth.getInstance().signOut()
    }

    private fun setTheTimer() {
        binding.appTitleBottom.visibility = View.INVISIBLE
        Handler(Looper.getMainLooper()).postDelayed({


            val intent = if (FirebaseAuth.getInstance() != null) {
                if (typeOfLogin == USER_CODE) {
                    Intent(this, UserMainActivity::class.java)
                } else if (typeOfLogin == DEVICE_CODE) {
                    Intent(this, CameraMainActivity::class.java)
                } else {
                    signOut()
                    Intent(this, LoginTypeSelect::class.java)
                }
            } else {
                Intent(this, LoginTypeSelect::class.java)
            }

            startActivity(intent)
            finish()
        }, 4000)

        Handler(Looper.getMainLooper()).postDelayed({
            binding.appTitleBottom.visibility = View.VISIBLE
        },2000)
    }

    private fun getLoginType() {
        val sharedPref = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)
        typeOfLogin = sharedPref.getInt(LOGIN_TYPE, -1)
    }
}