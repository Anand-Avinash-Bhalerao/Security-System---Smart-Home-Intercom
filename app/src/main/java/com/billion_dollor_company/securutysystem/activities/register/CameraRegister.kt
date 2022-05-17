package com.billion_dollor_company.securutysystem.activities.register

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.billion_dollor_company.securutysystem.R
import com.billion_dollor_company.securutysystem.databinding.ActivityCameraRegisterBinding
import com.billion_dollor_company.securutysystem.other.LoadingDialogBar
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

private lateinit var binding: ActivityCameraRegisterBinding

class CameraRegister : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        registerCamera()
        setTheDropdownMenu()
    }

    private fun setTheDropdownMenu() {
        val types = resources.getStringArray(R.array.types)
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_textview, types)
        binding.autoCompleteTextView.setAdapter(arrayAdapter)
    }

    private fun registerCamera() {
        binding.registerButton.setOnClickListener {

            val emailString = binding.emailId.text.toString().trim()
            val passwordString = binding.passwordId.text.toString()
            val nameString = binding.nameId.text.toString().trim()
            val phoneString = binding.phoneId.text.toString().trim()
            val locationString = binding.locationId.text.toString().trim()
            val locationType = binding.autoCompleteTextView.text.toString().trim()
            when {
                TextUtils.isEmpty(emailString) -> {
                    binding.emailId.error = "Email Cannot be empty!"
                    binding.emailId.requestFocus()
                }
                TextUtils.isEmpty(passwordString) -> {
                    binding.passwordId.error = "Password Cannot be empty!"
                    binding.passwordId.requestFocus()
                }
                TextUtils.isEmpty(nameString) -> {
                    binding.nameId.error = "Name cannot be empty!"
                    binding.nameId.requestFocus()
                }
                TextUtils.isEmpty(phoneString) -> {
                    binding.phoneId.error = "Phone number cannot be empty!"
                    binding.phoneId.requestFocus()
                }
                TextUtils.isEmpty(locationString) -> {
                    binding.locationId.error = "Location cannot be empty!"
                    binding.locationId.requestFocus()
                }
                TextUtils.isEmpty(locationType) -> {
                    binding.locationTypeContainer.error = "Location Type cannot be empty!"
                    binding.locationTypeContainer.requestFocus()
                }
                else -> {
                    val progressDialog = LoadingDialogBar(this)
                    progressDialog.showDialog("Creating an account, Please wait", R.raw.register)
                    FirebaseAuth.getInstance()
                        .createUserWithEmailAndPassword(emailString, passwordString)
                        .addOnCompleteListener { task: Task<AuthResult?> ->
                            if (task.isSuccessful) {
                                val user = FirebaseAuth.getInstance().currentUser!!
                                val uid = user.uid
                                val databaseReference = FirebaseDatabase.getInstance().reference
                                databaseReference.child("DeviceDatabase").child("Values")
                                    .child("Code")
                                    .addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            val value = snapshot.getValue(Int::class.java)!!
                                            val temp = StringBuilder(value.toString())
                                            while (temp.length != 5) {
                                                temp.insert(0, "0")
                                            }
                                            val code = temp.toString()
                                            val map = HashMap<String, String>()
                                            map["DeviceName"] = nameString
                                            map["Email"] = emailString
                                            map["PhoneNo"] = phoneString
                                            map["Location"] = locationString
                                            map["LocationType"] = locationType
                                            map["Code"] = code
                                            databaseReference.child("DeviceDatabase")
                                                .child("Devices")
                                                .child(uid).setValue(map)
                                            databaseReference.child("DeviceDatabase").child("Codes")
                                                .child(code)
                                                .setValue(
                                                    user.uid
                                                )

                                            val profileUpdates = UserProfileChangeRequest.Builder()
                                                .setDisplayName(nameString)
                                                .build()
                                            user.updateProfile(profileUpdates)

                                            val randomNumber = (10..100).random()
                                            val newValue = value + randomNumber
                                            databaseReference.child("DeviceDatabase")
                                                .child("Values")
                                                .child("Code").setValue(newValue)
                                        }

                                        override fun onCancelled(error: DatabaseError) {}

                                    })
                                Toast.makeText(
                                    applicationContext,
                                    "Camera registered Successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                progressDialog.setCheck()
                                Handler(Looper.getMainLooper()).postDelayed({
                                    onBackPressed()
                                }, 1500)
                            } else {
                                progressDialog.setError()
                                Handler(Looper.getMainLooper()).postDelayed({
                                    Toast.makeText(
                                        applicationContext,
                                        "Registration Error: " + task.exception!!.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }, 1500)

                            }
                        }
                }
            }
        }
    }
}