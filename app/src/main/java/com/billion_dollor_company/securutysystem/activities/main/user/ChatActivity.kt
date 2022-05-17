package com.billion_dollor_company.securutysystem.activities.main.user

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.billion_dollor_company.securutysystem.activities.main.user.ui.connected_devices.DEVICE_NAME
import com.billion_dollor_company.securutysystem.activities.main.user.ui.connected_devices.DEVICE_UID
import com.billion_dollor_company.securutysystem.databinding.ActivityChatBinding
import com.billion_dollor_company.securutysystem.model.MessageInfo
import com.billion_dollor_company.securutysystem.other.ChatAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList

class ChatActivity : AppCompatActivity() {

    lateinit var deviceUID: String
    lateinit var deviceName: String
    lateinit var list: ArrayList<MessageInfo>


    private lateinit var databaseReference: DatabaseReference
    private lateinit var adapter: ChatAdapter
    lateinit var user: FirebaseUser


    lateinit var binding: ActivityChatBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        initFirebase()
        setTheRecycler()
        sendMessage()
        childEventListener()
    }

    private fun childEventListener() {
        databaseReference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val map = snapshot.value as HashMap<*, *>
                val messageInfo = MessageInfo()
                messageInfo.dp_bitmap = map.getOrDefault("dp_bitmap","") as String
                messageInfo.name = map.getOrDefault("name","") as String
                messageInfo.text = map.getOrDefault("text","") as String
                messageInfo.uid = map.getOrDefault("uid","") as String
                messageInfo.sentFromDevice = map.getOrDefault("sentFromDevice",false) as Boolean
                messageInfo.isDevice = map.getOrDefault("device",false) as Boolean
                messageInfo.picPresent = map.getOrDefault("picPresent",false) as Boolean
                messageInfo.photo = map.getOrDefault("photo","") as String
                messageInfo.time = map.getOrDefault("time","") as String

//                Log.d("TESTING_", "The map is ${map.toString()}")
                Log.d("TESTING_", "The received is: ${messageInfo.toString()}")
                list.add(messageInfo!!)
                adapter.notifyItemInserted(list.size)
                binding.chatRecycler.scrollToPosition(list.size-1)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun init() {
        deviceUID = intent.getStringExtra(DEVICE_UID)!!
        deviceName = intent.getStringExtra(DEVICE_NAME)!!
        title = deviceName

//        actionBar!!.title = deviceName
        list = ArrayList()
        user = FirebaseAuth.getInstance().currentUser!!
        adapter = ChatAdapter(this, user.uid, list)
    }

    private fun initFirebase() {
        databaseReference =
            FirebaseDatabase.getInstance().reference.child("DeviceDatabase").child("Devices")
                .child(deviceUID).child("Messages")


    }

    private fun sendMessage() {
        binding.sendButton.setOnClickListener {
            val text = binding.messageEditText.text.toString()
            Log.d("TESTING_", "The text is:$text")
            if (text.isNotEmpty()) {
//                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, user.photoUrl)
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
                val time = "$day $monthName, $timeHr:$timeMinString"
                val message = MessageInfo(
                    name = user.displayName!!,
                    text = text,
                    uid = user.uid,
                    sentFromDevice = false,
                    dp_bitmap = "",
                    picPresent = false,
                    photo = "",
                    time = time
                )
                databaseReference.push().setValue(message)
                binding.messageEditText.setText("")
            }
        }
    }

//    data class MessageInfo(
//        val Name: String = "",
//        val Text: String = "",
//        val UID: String = "",
//        val isDevice: Boolean = false,
//        val DP_Bitmap: String = "",
//        val PicPresent: Boolean = false,
//        val Photo: String = "",
//        val Time:String = ""
//    )

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

    private fun setTheRecycler() {
        binding.chatRecycler.adapter = adapter
        binding.chatRecycler.layoutManager = LinearLayoutManager(this)

    }
}
