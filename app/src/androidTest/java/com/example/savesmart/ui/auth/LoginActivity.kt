package com.savesmart.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.savesmart.R
import com.savesmart.data.database.SaveSmartDatabase
import com.savesmart.data.repository.SaveSmartRepository
import com.savesmart.ui.dashboard.DashboardActivity
import com.savesmart.util.SecurityUtils
import com.savesmart.util.SessionManager

class LoginActivity : AppCompatActivity() {

    // Declare objects
    private lateinit var repository: SaveSmartRepository
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // VERY IMPORTANT: Correct layout must be set
        setContentView(R.layout.activity_login)

        // Initialize database and repository
        val db = SaveSmartDatabase.getDatabase(this)
        repository = SaveSmartRepository(db)

        // Initialize session manager
        sessionManager = SessionManager(this)

        // Link UI elements (IDs must match XML)
        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvRegister = findViewById<TextView>(R.id.tvRegister)

        // LOGIN BUTTON CLICK
        btnLogin.setOnClickListener {

            // Get user input
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // Validate input
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Hash password using SecurityUtils
            val hashedPassword = SecurityUtils.hashPassword(password)

            // Run login in background thread
            Thread {
                val user = repository.loginUser(username, hashedPassword)

                runOnUiThread {
                    if (user != null) {
                        // Save session
                        sessionManager.saveUser(username)

                        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()

                        // Navigate to Dashboard
                        startActivity(Intent(this, DashboardActivity::class.java))
                        finish()

                    } else {
                        Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
                    }
                }
            }.start()
        }

        // REGISTER LINK CLICK
        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}