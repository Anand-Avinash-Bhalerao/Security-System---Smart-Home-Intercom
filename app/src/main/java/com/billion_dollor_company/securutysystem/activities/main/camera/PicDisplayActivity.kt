package com.billion_dollor_company.securutysystem.activities.main.camera

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.net.toFile
import com.billion_dollor_company.securutysystem.R
import com.billion_dollor_company.securutysystem.databinding.ActivityPicDisplayBinding
import com.billion_dollor_company.securutysystem.model.MessageInfo
import com.billion_dollor_company.securutysystem.other.LoadingDialogBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import com.firebase.ui.storage.images.FirebaseImageLoader

import com.google.firebase.storage.StorageReference

import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule

import com.bumptech.glide.module.AppGlideModule
import com.google.firebase.database.core.Context
import java.io.InputStream
import java.util.*


class PicDisplayActivity : AppCompatActivity() {
    lateinit var binding:ActivityPicDisplayBinding

    lateinit var imageUri:Uri
    lateinit var imageName:String

    //firebase
    lateinit var user:FirebaseUser
    lateinit var database: FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPicDisplayBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        initFirebase()
        setTheImage()
        sendButtonClick()

    }

    private fun init(){
        imageUri = intent.getParcelableExtra(IMAGE_URI)!!
        imageName = intent.getStringExtra(IMAGE_NAME)!!

    }
    private fun initFirebase(){
        user = FirebaseAuth.getInstance().currentUser!!
        database = FirebaseDatabase.getInstance()
    }
    private fun setTheImage(){
        binding.image.setImageURI(imageUri)
    }
    private fun sendButtonClick(){
        binding.send.setOnClickListener{
            sendImage()
        }
    }
    private fun sendImage(){
        val loadingDialogBar = LoadingDialogBar(this)
        loadingDialogBar.showDialog("Sending", R.raw.loading_plane)
        val databaseReference = database.reference.child("DeviceDatabase").child("Devices").child(user.uid).child("Messages")

        val storageReference = FirebaseStorage.getInstance().reference.child("Messages").child(user.uid).child(imageName)
        storageReference.putFile(imageUri).addOnSuccessListener {

            storageReference.downloadUrl.addOnSuccessListener {
                Log.d("DOWN_URL","The url is $it")
                val message = MessageInfo(name=user.displayName!!,text="",uid=user.uid,sentFromDevice=true, dp_bitmap = it.toString(), picPresent = true, photo = imageName, time = getTime())
                databaseReference.push().setValue(message)
                Toast.makeText(applicationContext,"Upload Successful",Toast.LENGTH_SHORT).show()
                loadingDialogBar.setCheck()
            }
        }.addOnFailureListener{
            Toast.makeText(applicationContext,"Upload Failed",Toast.LENGTH_LONG).show()
            loadingDialogBar.setError()
        }
    }

    private fun getTime():String{
        val c = Calendar.getInstance()
        val day = c.get(Calendar.DAY_OF_MONTH)
        val timeHr = c.get(Calendar.HOUR)
        val timeMin = c.get(Calendar.MINUTE)
        val timeMinString = if (timeMin < 10) {
            "0$timeMin"
        } else {
            timeMin.toString()
        }
        val monthName = getMonthName(c.get(Calendar.MONTH))
        return "$day $monthName, $timeHr:$timeMinString"
    }
    private fun getMonthName(no: Int): String {
        return when (no) {
            1 -> "Jan"
            2 -> "Feb"
            3 -> "Mar"
            4 -> "Apr"
            5 -> "May"
            6 -> "Jun"
            7 -> "Jul"
            8 -> "Aug"
            9 -> "Sep"
            10 -> "Oct"
            11 -> "Nov"
            else -> "Dec"
        }
    }
}

