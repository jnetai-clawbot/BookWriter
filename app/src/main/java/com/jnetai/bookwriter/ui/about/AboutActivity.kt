package com.jnetai.bookwriter.ui.about

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.jnetai.bookwriter.BuildConfig
import com.jnetai.bookwriter.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

class AboutActivity : AppCompatActivity() {

    private lateinit var tvVersion: TextView
    private lateinit var tvUpdateStatus: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var btnCheckUpdate: Button
    private lateinit var btnShare: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "About"

        tvVersion = findViewById(R.id.tvVersion)
        tvUpdateStatus = findViewById(R.id.tvUpdateStatus)
        progressBar = findViewById(R.id.progressBar)
        btnCheckUpdate = findViewById(R.id.btnCheckUpdate)
        btnShare = findViewById(R.id.btnShare)

        tvVersion.text = "BookWriter v${BuildConfig.VERSION_NAME}"

        btnCheckUpdate.setOnClickListener { checkForUpdates() }

        btnShare.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "BookWriter")
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out BookWriter - a book writing assistant! https://github.com/jnetai-clawbot/BookWriter")
            startActivity(Intent.createChooser(shareIntent, "Share via"))
        }
    }

    private fun checkForUpdates() {
        progressBar.visibility = android.view.View.VISIBLE
        tvUpdateStatus.text = "Checking..."
        btnCheckUpdate.isEnabled = false

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val json = URL("https://api.github.com/repos/jnetai-clawbot/BookWriter/releases/latest")
                    .readText()
                val release = JSONObject(json)
                val latestVersion = release.optString("tag_name", "unknown")
                val htmlUrl = release.optString("html_url", "")

                withContext(Dispatchers.Main) {
                    progressBar.visibility = android.view.View.GONE
                    val currentVersion = BuildConfig.VERSION_NAME
                    if (latestVersion != currentVersion) {
                        tvUpdateStatus.text = "Update available: $latestVersion\n$htmlUrl"
                    } else {
                        tvUpdateStatus.text = "You're up to date (v$currentVersion)"
                    }
                    btnCheckUpdate.isEnabled = true
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = android.view.View.GONE
                    tvUpdateStatus.text = "Error checking updates: ${e.message}"
                    btnCheckUpdate.isEnabled = true
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}