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
        archishaLinks()
        abhishekLinks()
        amritLinks()
        openWebsite()
    }

    fun openWebsite() {
        val website = findViewById<TextView>(R.id.websiteLink)
        website.setOnClickListener { view: View? ->
//            String url = "securitysystems.ezyro.com/security_system.html";
//            val url = "https://www.google.com"
            val url = "http://www.securitysystems.ezyro.com/security_system.html"
            val uriWeb = Uri.parse(url)
            startActivity(Intent(Intent.ACTION_VIEW, uriWeb))
        }
    }
    fun init() {
        anandLI = findViewById<ImageView>(R.id.linkedANAND)
        anandIN = findViewById<ImageView>(R.id.instaANAND)
        anandGIT = findViewById<ImageView>(R.id.gitANAND)
        anandEMAIL = findViewById<TextView>(R.id.anandEMAIL)
        archishaLI = findViewById<ImageView>(R.id.linkedarchisha)
        archishaIN = findViewById<ImageView>(R.id.instaarchisha)
        archishaGIT = findViewById<ImageView>(R.id.gitarchisha)
        archishaEMAIL = findViewById<TextView>(R.id.archishaEMAIL)
        abhiIN = findViewById<ImageView>(R.id.instaABHI)
        abhiLI = findViewById<ImageView>(R.id.linkedABHI)
        abhiGIT = findViewById<ImageView>(R.id.gitABHI)
        abhiEMAIL = findViewById<TextView>(R.id.abhiEMAIL)
        amritIN = findViewById<ImageView>(R.id.instaAMRIT)
        amritLI = findViewById<ImageView>(R.id.linkedAMRIT)
        amritGIT = findViewById<ImageView>(R.id.gitAMRIT)
        amritEMAIL = findViewById<TextView>(R.id.amritEMAIL)
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

    fun archishaLinks() {
        openTheLink(archishaIN, "https://instagram.com/archisha_02")
        openTheLink(archishaLI, "https://www.linkedin.com/in/archisha-mulmulay-a167b6218")
        openTheLink(archishaGIT, "https://github.com/archisha-02")
    }

    fun abhishekLinks() {
        openTheLink(abhiIN, "https://Instagram.com/abhishek.m_s")
        openTheLink(abhiLI, "")
        openTheLink(abhiGIT, "https://github.com/ABHISHEKSOUNDALGEKAR")
    }

    fun amritLinks() {
        openTheLink(amritLI, "https://www.linkedin.com/in/amrit-singh-365310204")
        openTheLink(amritGIT, "https://github.com/iAmritSingh")
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