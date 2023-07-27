package com.billion_dollor_company.securutysystem.activities.main.user.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.billion_dollor_company.securutysystem.databinding.FragmentHomeBinding
import android.R
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.billion_dollor_company.securutysystem.activities.developer.DevActivity
import com.billion_dollor_company.securutysystem.activities.main.user.ModifyActivity
import com.billion_dollor_company.securutysystem.activities.main.user.ui.add.AddDeviceActivity
import com.billion_dollor_company.securutysystem.activities.main.user.ui.connected_devices.UserConnectedDevicesActivity
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        setTheName()
        handleClicks()
        return root
    }


    private fun setTheName(){
        val name:String = FirebaseAuth.getInstance().currentUser?.displayName.toString()
        binding.userName.text = name
    }

    private fun handleClicks() {

        binding.devices.setOnClickListener{
            startActivity(Intent(context, UserConnectedDevicesActivity::class.java))

        }

        binding.add.setOnClickListener{
            startActivity(Intent(context, AddDeviceActivity::class.java))
        }

        binding.developers.setOnClickListener{
            startActivity(Intent(context,DevActivity::class.java))
        }

        binding.modify.setOnClickListener{
            startActivity(Intent(context,ModifyActivity::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}