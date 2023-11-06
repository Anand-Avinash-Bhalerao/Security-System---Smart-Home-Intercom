package com.billion_dollor_company.securutysystem.activities.register

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.billion_dollor_company.securutysystem.R
import com.billion_dollor_company.securutysystem.databinding.ActivityUserRegisterBinding
import com.billion_dollor_company.securutysystem.other.LoadingDialogBar
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

private lateinit var binding: ActivityUserRegisterBinding
var imageAdded = false
var imagePath: Uri? = null

class UserRegister : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setProfilePicture()
        alreadyHaveAccount()
        createUser()
    }

    private fun alreadyHaveAccount() {
        binding.alreadyHaveAcc.setOnClickListener { onBackPressed() }
    }

    private fun setProfilePicture() {
        binding.setDP.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, 33)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && data!!.data != null) {
            imagePath = data.data
            binding.profileImage.setImageURI(imagePath)
            imageAdded = true
        } else {
            Toast.makeText(this, "Picture not selected", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun createUser() {
        binding.registerButton.setOnClickListener {
            val emailString = binding.emailId.text.toString().trim()
            val passwordString = binding.passwordId.text.toString()
            val nameString = binding.nameId.text.toString().trim()
            val phoneString = binding.phoneId.text.toString().trim()

            if (TextUtils.isEmpty(emailString)) {
                binding.emailId.error = "Email Cannot be empty!"
                binding.emailId.requestFocus()
            } else if (TextUtils.isEmpty(passwordString)) {
                binding.passwordId.error = "Password Cannot be empty!"
                binding.passwordId.requestFocus()
            } else if (TextUtils.isEmpty(nameString)) {
                binding.nameId.error = "Name cannot be empty!"
                binding.nameId.requestFocus()
            } else if (TextUtils.isEmpty(phoneString)) {
                binding.phoneId.error = "Phone cannot be empty!"
                binding.phoneId.requestFocus()
            } else if (!imageAdded) {
                Toast.makeText(this, "Please add a profile picture", Toast.LENGTH_SHORT).show()
            } else {
                val progressDialog = LoadingDialogBar(this)
                progressDialog.showDialog("Creating an account, Please wait", R.raw.register)
                val map = HashMap<String, String>()
                map["Name"] = nameString
                map["Email"] = emailString
                map["PhoneNo"] = phoneString
                val auth = FirebaseAuth.getInstance()
                auth.createUserWithEmailAndPassword(emailString, passwordString)
                    .addOnCompleteListener { task: Task<AuthResult?> ->
                        if (task.isSuccessful) {
                            val user = FirebaseAuth.getInstance().currentUser!!
                            val uid = user.uid
                            Log.d("RegisterLogs", "The uid is $uid")
                            Log.d("RegisterLogs", "The map is $map")

                            // to add the image to the storage

                            val reference: StorageReference = FirebaseStorage.getInstance().reference.child("profile_pictures")
                                    .child(auth.currentUser!!.uid+"."+getExtensionType(imagePath!!))


                            val uploadTask = reference.putFile(imagePath!!)
                            uploadTask.addOnSuccessListener {
                                reference.downloadUrl.addOnSuccessListener {
                                    Log.d("DOWN_URL","The url is:$it")
                                    val profileUpdates = UserProfileChangeRequest.Builder()
                                        .setDisplayName(nameString)
                                        .setPhotoUri(imagePath)
                                        .build()

                                    map["dp_url"] = it.toString()
                                    user.updateProfile(profileUpdates)
                                        .addOnCompleteListener {
                                            Toast.makeText(
                                                applicationContext,
                                                "User registered Successfully",
                                                Toast.LENGTH_SHORT
                                            ).show()
////
////
////                                            // mene ui
////                                            // textbox ka daya
////                                            progressDialog.setCheck()
////                                            Handler(Looper.getMainLooper()).postDelayed({
////                                                onBackPressed()
////                                            }, 1500)
                                        }
                                    FirebaseDatabase.getInstance().reference.child("UserDatabase")
                                        .child(uid).setValue(map).addOnCompleteListener {
                                            if(it.isSuccessful){
                                                Toast.makeText(
                                                    applicationContext,
                                                    "User registered Successfully",
                                                    Toast.LENGTH_SHORT
                                                ).show()


                                                // mene ui
                                                // textbox ka daya
                                                progressDialog.setCheck()
                                                Handler(Looper.getMainLooper()).postDelayed({
                                                    onBackPressed()
                                                }, 1500)
                                            }
                                        }
                                }.addOnFailureListener{
                                    Log.d("DOWN_URL","failed")
                                }
                                Toast.makeText(applicationContext,"Image uploaded",Toast.LENGTH_SHORT).show()
                            }.addOnFailureListener {
                                Toast.makeText(applicationContext,"Image not uploaded",Toast.LENGTH_SHORT).show()
                            }



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

    private fun getExtensionType(uri:Uri): String? {
        val cr = contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cr.getType(uri))
    }
}