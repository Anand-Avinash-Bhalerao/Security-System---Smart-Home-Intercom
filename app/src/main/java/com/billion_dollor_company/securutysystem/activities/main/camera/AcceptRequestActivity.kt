package com.billion_dollor_company.securutysystem.activities.main.camera

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.billion_dollor_company.securutysystem.databinding.ActivityAcceptRequestBinding
import com.billion_dollor_company.securutysystem.model.RequestInfo
import com.billion_dollor_company.securutysystem.other.RequestAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class AcceptRequestActivity : AppCompatActivity(), RequestAdapter.OnNoteClickListener {

    private lateinit var binding: ActivityAcceptRequestBinding
    private lateinit var list: ArrayList<RequestInfo>

    private lateinit var adapter: RequestAdapter

    private lateinit var user: FirebaseUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAcceptRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initialState()
        initFirebase()
        getTheRequests()


    }

    private fun initialState() {
        binding.reqRecycler.visibility = View.INVISIBLE
        binding.noFound.visibility = View.INVISIBLE

    }

    private fun noReqState() {
        binding.reqRecycler.visibility = View.INVISIBLE
        binding.contentLoading.pauseAnimation()
        binding.contentLoading.visibility = View.INVISIBLE
        binding.noFound.visibility = View.VISIBLE
    }

    private fun finalState() {
        binding.reqRecycler.visibility = View.VISIBLE
        binding.contentLoading.pauseAnimation()
        binding.noFound.visibility = View.INVISIBLE
        binding.contentLoading.visibility = View.INVISIBLE
    }


    private fun initFirebase() {
        user = FirebaseAuth.getInstance().currentUser!!
    }

    private fun getTheRequests() {

        list = arrayListOf()
        FirebaseDatabase.getInstance().reference.child("DeviceDatabase").child("Devices")
            .child(user.uid).child("Requests").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    list.clear()
                    if (snapshot.exists()) {
                        for (userSnapshot in snapshot.children) {
                            val current = userSnapshot.getValue(RequestInfo::class.java)
                            list.add(current!!)
                        }
                    }
                    setTheRecycler()

                }

                override fun onCancelled(error: DatabaseError) {
                }
            }
            )
    }

    private fun setTheRecycler() {

        if (list.size != 0) {
            finalState()
            Log.d("THE_LIST", "Before changing: $list")
            adapter = RequestAdapter(this, list, this)
            binding.reqRecycler.adapter = adapter
            binding.reqRecycler.layoutManager = LinearLayoutManager(this)
        }else{
            noReqState()
        }


    }

    override fun onAccept(pos: Int) {
        val current = list[pos]
        val map: HashMap<String, String> = HashMap()
        map["Name"] = current.Name
        map["UID"] = current.UID
        FirebaseDatabase.getInstance().reference.child("DeviceDatabase").child("Devices")
            .child(user.uid).child("Connected").child(current.UID).setValue(map)
        FirebaseDatabase.getInstance().reference.child("DeviceDatabase").child("Devices")
            .child(user.uid).child("Requests").child(current.UID).removeValue()

        val map2:HashMap<String,String> = HashMap()
        map2["Name"] = user.displayName.toString()
        map2["UID"] = user.uid

        FirebaseDatabase.getInstance().reference.child("UserDatabase").child(current.UID)
            .child("Connected").child(user.uid).setValue(map2)

        adapter.notifyItemRemoved(pos)

    }

    override fun onDecline(pos: Int) {
        val current = list[pos]
        FirebaseDatabase.getInstance().reference.child("DeviceDatabase").child("Devices")
            .child(user.uid).child("Requests").child(current.UID).removeValue()

        list.removeAt(pos)
        adapter.notifyItemRemoved(pos)
    }
}