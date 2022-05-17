package com.billion_dollor_company.securutysystem.activities.main.user

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.billion_dollor_company.securutysystem.R
import com.billion_dollor_company.securutysystem.activities.main.user.ui.connected_devices.DEVICE_NAME
import com.billion_dollor_company.securutysystem.activities.main.user.ui.connected_devices.DEVICE_UID
import com.billion_dollor_company.securutysystem.databinding.ActivityModifyBinding
import com.billion_dollor_company.securutysystem.model.ConnectedDeviceInfo
import com.billion_dollor_company.securutysystem.other.ConnectedAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ModifyActivity : AppCompatActivity(),ConnectedAdapter.OnNoteClickListener {

    private lateinit var binding:ActivityModifyBinding
    private lateinit var list: ArrayList<ConnectedDeviceInfo>
    private lateinit var adapter: ConnectedAdapter
    private lateinit var user: FirebaseUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityModifyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        initialState()
        getTheDevices()
    }


    private fun initialState() {
        binding.connectedRecycler.visibility = View.INVISIBLE
        binding.noFound.visibility = View.INVISIBLE

    }

    private fun noReqState() {
        binding.connectedRecycler.visibility = View.INVISIBLE
        binding.contentLoading.pauseAnimation()
        binding.contentLoading.visibility = View.INVISIBLE
        binding.noFound.visibility = View.VISIBLE
    }

    private fun finalState() {
        binding.connectedRecycler.visibility = View.VISIBLE
        binding.contentLoading.pauseAnimation()
        binding.noFound.visibility = View.INVISIBLE
        binding.contentLoading.visibility = View.INVISIBLE
    }

    private fun getTheDevices() {
        FirebaseDatabase.getInstance().reference.child("UserDatabase").child(user.uid).child("Connected").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                if (snapshot.exists()) {
                    for (snapshots in snapshot.children) {
                        val current = snapshots.getValue(ConnectedDeviceInfo::class.java)
                        if (current != null) {
                            list.add(current)
                        }
                    }
                }
                setTheRecycler()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun setTheRecycler() {

        if (list.size != 0) {
            finalState()
            adapter = ConnectedAdapter(applicationContext, list, this)
            binding.connectedRecycler.adapter = adapter
            binding.connectedRecycler.layoutManager = LinearLayoutManager(applicationContext)
        }else{
            noReqState()
        }

    }

    private fun init(){
        list = ArrayList()

        user = FirebaseAuth.getInstance().currentUser!!
    }


    override fun onDelete(pos: Int) {
        val deviceName = list[pos].Name
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure you want to remove\n$deviceName")
            .setCancelable(false)
            .setPositiveButton("Yes") { _: DialogInterface?, _: Int ->
                removeAtPos(pos)
            }
            .setNegativeButton("No") { dialog: DialogInterface, _: Int -> dialog.cancel() }
        val alert = builder.create()
        alert.show()
    }

    override fun onNoteClick(pos: Int) {
//        Toast.makeText(context,"Clicked",Toast.LENGTH_SHORT).show()
//        val current = list[pos]
//        val intent = Intent(applicationContext, ChatActivity::class.java)
//        intent.putExtra(DEVICE_UID,current.UID)
//        intent.putExtra(DEVICE_NAME,current.Name)
//        startActivity(intent)
    }

    private fun removeAtPos(pos:Int){

        //current wale me device ka data hoga. user me user ka
        val current = list[pos]
        FirebaseDatabase.getInstance().reference.child("DeviceDatabase").child("Devices")
            .child(current.UID).child("Connected").child(user.uid).removeValue()

        FirebaseDatabase.getInstance().reference.child("UserDatabase").child(user.uid)
            .child("Connected").child(current.UID).removeValue()

        Log.d("THE_LIST","After changing: $list")
    }
}