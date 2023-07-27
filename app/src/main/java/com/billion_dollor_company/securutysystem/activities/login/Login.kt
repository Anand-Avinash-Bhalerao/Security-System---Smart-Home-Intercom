package com.billion_dollor_company.securutysystem.activities.login

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.billion_dollor_company.securutysystem.R
import com.billion_dollor_company.securutysystem.activities.main.camera.CameraMainActivity
import com.billion_dollor_company.securutysystem.activities.main.user.UserMainActivity
import com.billion_dollor_company.securutysystem.activities.register.CameraRegister
import com.billion_dollor_company.securutysystem.activities.register.UserRegister
import com.billion_dollor_company.securutysystem.activities.splash.LOGIN_TYPE
import com.billion_dollor_company.securutysystem.activities.splash.SHARED_PREF_NAME
import com.billion_dollor_company.securutysystem.databinding.ActivityLoginBinding
import com.billion_dollor_company.securutysystem.other.LoadingDialogBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


private lateinit var binding: ActivityLoginBinding
private var typeOfLogin: Int = -1
private var loginIsOfCorrectType: Boolean = true

// 0 is device and 1 is user

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getTypeOfLogin()
        createAccount()
        login()
    }

    private fun getTypeOfLogin() {
        typeOfLogin = intent.getIntExtra(SELECTED, 0)
        val titleText: String = if (typeOfLogin == USER_CODE) {
            binding.loginLogoImg.setAnimation(R.raw.user_animated)
            "User Login"
        } else {
            binding.loginLogoImg.setAnimation(R.raw.camera_animated)
            "Admin Login"
        }
        binding.loginTypeText.text = titleText
    }

    private fun createAccount() {
        binding.alreadyHaveAcc.setOnClickListener {
            if (typeOfLogin == USER_CODE) {
                startActivity(Intent(this, UserRegister::class.java))
            } else {
                startActivity(Intent(this, CameraRegister::class.java))
            }
        }
    }

    private fun setSharedPref() {
        val sharedPref = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)
        val editor = sharedPref.edit()

        editor.apply {
            putInt(LOGIN_TYPE, typeOfLogin)
            apply()
        }
    }

    private fun login() {
        binding.loginButton.setOnClickListener {
            val emailString: String = binding.emailId.text.toString().trim()
            val passwordString: String = binding.passwordId.text.toString()
            when {
                TextUtils.isEmpty(emailString) -> {
                    binding.emailId.error = "Email cannot be empty!"
                    binding.emailId.requestFocus()
                }
                TextUtils.isEmpty(passwordString) -> {
                    binding.passwordId.error = "Password cannot be empty!"
                    binding.passwordId.requestFocus()
                }
                else -> {
                    val dialogBar = LoadingDialogBar(this)
                    dialogBar.showDialog("Logging in, Please Wait", R.raw.loading_dino)
                    FirebaseAuth.getInstance()
                        .signInWithEmailAndPassword(emailString, passwordString)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                val user = FirebaseAuth.getInstance().currentUser
                                val uid: String = user!!.uid

                                val typeName: String =
                                    if (typeOfLogin == USER_CODE) "UserDatabase" else "DeviceDatabase"

                                val dataRef: DatabaseReference = if (typeOfLogin == USER_CODE) {
                                    FirebaseDatabase.getInstance().reference.child(typeName)
                                        .child(uid)
                                } else {
                                    FirebaseDatabase.getInstance().reference.child(typeName)
                                        .child("Devices").child(uid)
                                }

                                dataRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        if (snapshot.value == null) {
                                            loginIsOfCorrectType = false
                                        }


                                        if (loginIsOfCorrectType) {
                                            dialogBar.setCheck()
                                            Handler(Looper.getMainLooper()).postDelayed({
                                                loginIsOfCorrectType = true
                                                setSharedPref()
                                                if (typeOfLogin == USER_CODE) {
                                                    startActivity(
                                                        Intent(
                                                            applicationContext,
                                                            UserMainActivity::class.java
                                                        )
                                                    )
                                                    finish()
                                                } else {
                                                    startActivity(
                                                        Intent(
                                                            applicationContext,
                                                            CameraMainActivity::class.java
                                                        )
                                                    )
                                                    finish()
                                                }
                                            }, 2000)

                                        } else {

                                            FirebaseAuth.getInstance().signOut();
                                            dialogBar.setError()
                                            Handler(Looper.getMainLooper()).postDelayed({
                                                loginIsOfCorrectType = true
                                                Toast.makeText(
                                                    applicationContext,
                                                    "No account found", Toast.LENGTH_SHORT
                                                ).show()
                                            }, 2000)
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                    }

                                })

                            } else {
                                dialogBar.setError()
                                Handler(Looper.getMainLooper()).postDelayed({
                                    Toast.makeText(
                                        applicationContext,
                                        it.exception!!.message, Toast.LENGTH_SHORT
                                    ).show()
                                }, 2000)

                            }
                        }
                }
            }
        }
    }


}