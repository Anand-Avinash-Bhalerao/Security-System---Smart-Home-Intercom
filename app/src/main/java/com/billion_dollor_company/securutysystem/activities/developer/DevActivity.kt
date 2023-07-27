package com.billion_dollor_company.securutysystem.activities.developer

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.billion_dollor_company.securutysystem.R

class DevActivity : AppCompatActivity() {
    private lateinit var anandLI:ImageView
    private lateinit var anandIN:ImageView
    private lateinit var anandGIT:ImageView
    private lateinit var anandEMAIL:TextView

    private lateinit var archishaLI:ImageView
    private lateinit var archishaIN:ImageView
    private lateinit var archishaGIT:ImageView
    private lateinit var archishaEMAIL:TextView

    private lateinit var abhiLI:ImageView
    private lateinit var abhiIN:ImageView
    private lateinit var abhiGIT:ImageView
    private lateinit var abhiEMAIL:TextView

    private lateinit var amritLI:ImageView
    private lateinit var amritIN:ImageView
    private lateinit var amritGIT:ImageView
    private lateinit var amritEMAIL:TextView


    private lateinit var back:ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dev)

        init()
        links()
        backPressed()
        copyEmailToClipboard()
    }

    private fun links() {
        anandLinks()
    }

    fun init() {
        anandLI = findViewById<ImageView>(R.id.linkedANAND)
        anandIN = findViewById<ImageView>(R.id.instaANAND)
        anandGIT = findViewById<ImageView>(R.id.gitANAND)
        anandEMAIL = findViewById<TextView>(R.id.anandEMAIL)
        back = findViewById<ImageView>(R.id.back)
    }

    fun openTheLink(imageWithLogo: ImageView, link: String?) {
        imageWithLogo.setOnClickListener { view: View? ->
            val uri = Uri.parse(link)
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        }
    }

    fun anandLinks() {
        openTheLink(anandIN, "https://www.instagram.com/anand_bhalerao.a3b/?hl=en")
        openTheLink(anandLI, "https://www.linkedin.com/in/anand-bhalerao-0b69451bb/")
        openTheLink(anandGIT, "https://github.com/Anand-Avinash-Bhalerao")
    }

    private fun backPressed() {
        back.setOnClickListener { view: View? ->
            onBackPressed()
            finish()
        }
    }

    private fun copyEmailToClipboard() {
        copySetOnClickListeners(anandEMAIL, "Anand")
        copySetOnClickListeners(archishaEMAIL, "Archisha")
        copySetOnClickListeners(abhiEMAIL, "Abhishek")
        copySetOnClickListeners(amritEMAIL, "Amrit")
    }

    // copies the text from text view and sends the email and name to the clipboard copy function
    fun copySetOnClickListeners(emailTextView: TextView, name: String) {
        emailTextView.setOnClickListener { view: View? ->
            val email = emailTextView.text.toString()
            copyToClipBoard(name, email)
        }
    }

    //function to copy the email to clipboard
    private fun copyToClipBoard(name: String, email: String) {
        val context = applicationContext
        val clipboard = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Copied Text", email)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "$name's email is copied!", Toast.LENGTH_SHORT).show()
    }
}