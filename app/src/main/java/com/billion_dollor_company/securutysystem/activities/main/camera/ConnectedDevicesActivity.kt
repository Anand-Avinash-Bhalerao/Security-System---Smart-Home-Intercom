package com.billion_dollor_company.securutysystem.activities.main.camera

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.billion_dollor_company.securutysystem.databinding.ActivityConnectedDevicesBinding
import com.billion_dollor_company.securutysystem.model.ConnectedDeviceInfo
import com.billion_dollor_company.securutysystem.other.ConnectedAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ConnectedDevicesActivity : AppCompatActivity(), ConnectedAdapter.OnNoteClickListener {




    private lateinit var binding: ActivityConnectedDevicesBinding
    private lateinit var user: FirebaseUser
    private lateinit var list: ArrayList<ConnectedDeviceInfo>
    private lateinit var adapter: ConnectedAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConnectedDevicesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initialState()
        init()
        getTheDevices()

    }

    private fun init() {
        list = ArrayList()
        initFirebase()
    }

    private fun initFirebase() {
        user = FirebaseAuth.getInstance().currentUser!!
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
        FirebaseDatabase.getInstance().reference.child("DeviceDatabase").child("Devices")
            .child(user.uid).child("Connected").addValueEventListener(object : ValueEventListener {
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
            adapter = ConnectedAdapter(this, list, this)
            binding.connectedRecycler.adapter = adapter
            binding.connectedRecycler.layoutManager = LinearLayoutManager(this)
        }else{
            noReqState()
        }

    }

    override fun onDelete(pos: Int) {

        val userName = list[pos].Name
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure you want to remove\n$userName")
            .setCancelable(false)
            .setPositiveButton("Yes") { _: DialogInterface?, _: Int ->
                removeAtPos(pos)
            }
            .setNegativeButton("No") { dialog: DialogInterface, _: Int -> dialog.cancel() }
        val alert = builder.create()
        alert.show()
    }

    override fun onNoteClick(pos: Int) {
        //kuch nahi hoga
    }

    private fun removeAtPos(pos:Int){
        val current = list[pos]
        FirebaseDatabase.getInstance().reference.child("DeviceDatabase").child("Devices")
            .child(user.uid).child("Connected").child(current.UID).removeValue()

        FirebaseDatabase.getInstance().reference.child("UserDatabase").child(current.UID)
            .child("Connected").child(user.uid).removeValue()

        Log.d("THE_LIST","After changing: $list")
    }
}