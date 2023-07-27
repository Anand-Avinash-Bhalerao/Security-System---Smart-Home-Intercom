package com.billion_dollor_company.securutysystem.activities.main.camera

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.billion_dollor_company.securutysystem.R
import com.billion_dollor_company.securutysystem.activities.login.LoginTypeSelect
import com.billion_dollor_company.securutysystem.activities.splash.LOGIN_TYPE
import com.billion_dollor_company.securutysystem.activities.splash.SHARED_PREF_NAME
import com.billion_dollor_company.securutysystem.databinding.ActivityCameraMainBinding
import com.billion_dollor_company.securutysystem.model.RequestInfo
import com.billion_dollor_company.securutysystem.model.UserPersonalInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase


class CameraMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraMainBinding


    private lateinit var databaseReference: FirebaseDatabase
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initFirebase()
        buttonClicks()

    }


    private fun initFirebase() {
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance()
    }
    private fun buttonClicks(){
        requestButton()
        connectedButton()
        cameraButton()
        setName()
        setTheCode()
    }

    private fun setTheCode() {
        FirebaseDatabase.getInstance().reference.child("DeviceDatabase").child("Devices").child(FirebaseAuth.getInstance().currentUser?.uid.toString()).child("Code").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {

                        Log.d("FIREBASE_VALUE","The value from the firebase is "+snapshot.value.toString())
                        val current = snapshot.value.toString()
                        binding.deviceCode.text = current

                    }

                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    private fun setName() {
        val name: String? = FirebaseAuth.getInstance().currentUser!!.displayName
        binding.deviceName.text = name
    }

    private fun signout(){
        val sharedPref = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)
        val editor = sharedPref.edit()

        editor.apply{
            putInt(LOGIN_TYPE, -1)
            apply()
        }
        auth.signOut()
    }



    private fun makeSignOutDialog(title: String = "Are you sure you want to Sign out?") {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(title)
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog: DialogInterface?, id: Int ->
                signout()
                startActivity(Intent(applicationContext, LoginTypeSelect::class.java))
                finish()
            }
            .setNegativeButton("No") { dialog: DialogInterface, id: Int -> dialog.cancel() }
        val alert = builder.create()
        alert.show()
    }

    private fun requestButton(){
        binding.requests.setOnClickListener{
            startActivity(Intent(this,AcceptRequestActivity::class.java))
        }
    }
    private fun connectedButton(){
        binding.connected.setOnClickListener{
            startActivity(Intent(this,ConnectedDevicesActivity::class.java))
        }
    }

    private fun cameraButton(){
        binding.camera.setOnClickListener{
            startActivity(Intent(this,PicCaptureActivity::class.java))
        }
    }


    override fun onBackPressed() {
        makeSignOutDialog()
    }


}