package com.billion_dollor_company.securutysystem.activities.main.user.ui.add

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.billion_dollor_company.securutysystem.R
import com.billion_dollor_company.securutysystem.databinding.ActivityAddDeviceBinding
import com.billion_dollor_company.securutysystem.other.LoadingDialogBar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class AddDeviceActivity : AppCompatActivity() {

    private lateinit var binding:ActivityAddDeviceBinding
    private var currentCode: String = ""
    private var codeArray = arrayOfNulls<TextView>(5)
    private var noInArray = arrayOfNulls<TextView>(10)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddDeviceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        handleNumberClick()
        handleDelete()
        sendRequest()
    }


    private fun init() {
        codeArray[0] = binding.firstNo
        codeArray[1] = binding.secondNo
        codeArray[2] = binding.thirdNo
        codeArray[3] = binding.fourthNo
        codeArray[4] = binding.fifthNo

        noInArray[0] = binding.no0
        noInArray[1] = binding.no1
        noInArray[2] = binding.no2
        noInArray[3] = binding.no3
        noInArray[4] = binding.no4
        noInArray[5] = binding.no5
        noInArray[6] = binding.no6
        noInArray[7] = binding.no7
        noInArray[8] = binding.no8
        noInArray[9] = binding.no9
    }

    private fun handleNumberClick() {
        for (i in 0..9) {
            noInArray[i]?.setOnClickListener {
                appendToCode(i)
            }
        }
    }

    private fun handleDelete() {
        binding.delete.setOnClickListener {

            val size = currentCode.length
            if (size != 0) {
                val newCode = currentCode.subSequence(0, size - 1).toString()
                currentCode = newCode
                updateValues()
            }
        }
    }

    private fun appendToCode(no: Int) {
        if (currentCode.length < 5) {
            currentCode += no.toString()
            updateValues()
        }
    }

    private fun updateValues() {
        val currentSize: Int = currentCode.length
        for (i in 0 until currentSize) {
            codeArray[i]!!.text = currentCode[i].toString()
        }
        for (i in currentSize until 5) {
            codeArray[i]!!.text = " "
        }

    }

    private fun sendRequest() {
        binding.sendReq.setOnClickListener { view ->
            if (currentCode.length == 5) {
                val loadingDialogBar= LoadingDialogBar(this)

                FirebaseDatabase.getInstance().reference.child("DeviceDatabase").child("Codes")
                    .child(currentCode).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.value == null) {
                                Toast.makeText(applicationContext, "Code invalid", Toast.LENGTH_SHORT).show()
                            } else {
                                loadingDialogBar.showDialog("Sending...", R.raw.loading_plane)
                                val deviceUID = snapshot.getValue(String::class.java)
                                val user = FirebaseAuth.getInstance().currentUser
                                val currentTime = Calendar.getInstance().time

                                val forDate = SimpleDateFormat("dd/MM/yyyy")
                                val forTime = SimpleDateFormat("HH:mm:ss")
                                val formattedDate = forDate.format(currentTime)
                                val formattedTime = forTime.format(currentTime)



                                val map: HashMap<String,String> = HashMap()
                                map["Name"] = user?.displayName.toString()
                                map["Date"] = formattedDate
                                map["Time"] = formattedTime
                                map["UID"] = user?.uid.toString()

                                //if no request has been sent earlier
                                FirebaseDatabase.getInstance().reference.child("DeviceDatabase")
                                    .child("Devices").child(deviceUID!!).child("Requests")
                                    .child(user!!.uid)
                                    .addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(snapshot1: DataSnapshot) {
                                            if (snapshot1.value == null) {

                                                //check if already connected or not
                                                FirebaseDatabase.getInstance().reference.child("DeviceDatabase")
                                                    .child("Devices").child(deviceUID).child("Connected")
                                                    .child(user.uid)
                                                    .addListenerForSingleValueEvent(object :
                                                        ValueEventListener {
                                                        override fun onDataChange(snapshot1: DataSnapshot) {
                                                            if (snapshot1.value == null) {

                                                                FirebaseDatabase.getInstance().reference.child("DeviceDatabase")
                                                                    .child("Devices")
                                                                    .child(deviceUID)
                                                                    .child("Requests")
                                                                    .child(user.uid)
                                                                    .setValue(map)

                                                                loadingDialogBar.setCheck()
                                                                Snackbar.make(view, "Request Sent", Snackbar.LENGTH_LONG)
                                                                    .setAction("Action", null).show()
                                                            }
                                                            else{
                                                                loadingDialogBar.setError()
                                                                Toast.makeText(applicationContext,"Already Connected",
                                                                    Toast.LENGTH_LONG).show()
                                                            }
                                                        }

                                                        override fun onCancelled(error: DatabaseError) {}
                                                    })
                                            }else{
                                                loadingDialogBar.setError()
                                                Toast.makeText(applicationContext,"Request already Sent",
                                                    Toast.LENGTH_LONG).show()

                                            }
                                        }

                                        override fun onCancelled(error: DatabaseError) {}
                                    })
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })
            }
        }
    }
}