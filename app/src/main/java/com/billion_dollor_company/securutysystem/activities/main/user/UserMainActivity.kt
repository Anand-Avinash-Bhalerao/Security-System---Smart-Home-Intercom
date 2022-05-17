package com.billion_dollor_company.securutysystem.activities.main.user

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.billion_dollor_company.securutysystem.activities.login.LoginTypeSelect
import com.billion_dollor_company.securutysystem.activities.splash.LOGIN_TYPE
import com.billion_dollor_company.securutysystem.activities.splash.SHARED_PREF_NAME
import com.billion_dollor_company.securutysystem.databinding.ActivityUserMainBinding
import com.billion_dollor_company.securutysystem.model.UserPersonalInfo
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import android.R

import android.os.Messenger

import android.content.pm.PackageManager




class UserMainActivity : AppCompatActivity() {

    private lateinit var databaseReference: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var currentUser: UserPersonalInfo

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityUserMainBinding

    private lateinit var headerView: View
    private lateinit var profileImage:ImageView


    //view
    private lateinit var userName: TextView
    private lateinit var userEmail: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUserMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarCamMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        headerView = navView.getHeaderView(0)
        profileImage = headerView.findViewById(com.billion_dollor_company.securutysystem.R.id.imageView)
        val navController = findNavController(com.billion_dollor_company.securutysystem.R.id.nav_host_fragment_content_cam_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                com.billion_dollor_company.securutysystem.R.id.nav_home, com.billion_dollor_company.securutysystem.R.id.nav_add_device,com.billion_dollor_company.securutysystem.R.id.nav_connected
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        initView()
        initFirebase()
        setProfile()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(com.billion_dollor_company.securutysystem.R.menu.user_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            com.billion_dollor_company.securutysystem.R.id.action_logout -> {
                makeSignOutDialog()
                return true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(com.billion_dollor_company.securutysystem.R.id.nav_host_fragment_content_cam_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun initFirebase() {
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!
        databaseReference = FirebaseDatabase.getInstance()
    }

    private fun initView() {
        userName = headerView.findViewById(com.billion_dollor_company.securutysystem.R.id.header_user_name)
        userEmail = headerView.findViewById(com.billion_dollor_company.securutysystem.R.id.header_email)
    }

    private fun setProfile() {

        Log.d("VALUES_PROFILE","Name:${user.displayName}\nPhoto:${user.photoUrl}")
//        profileImage.setImageURI(user.photoUrl)
        // it gets the user object from firebase
        FirebaseDatabase.getInstance().reference.child("UserDatabase").child(auth.currentUser!!.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    currentUser = snapshot.getValue(UserPersonalInfo::class.java)!!
                    userName.text = currentUser.Name
                    userEmail.text = currentUser.Email
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        applicationContext,
                        "Something went wrong while fetching",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
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

    override fun onBackPressed() {
        makeSignOutDialog()
    }


    fun makeSignOutDialog(title: String = "Are you sure you want to Sign out?") {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(title)
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog: DialogInterface?, id: Int ->
                signout()
                startActivity(Intent(applicationContext,LoginTypeSelect::class.java))
                finish()
            }
            .setNegativeButton("No") { dialog: DialogInterface, id: Int -> dialog.cancel() }
        val alert = builder.create()
        alert.show()
    }


}