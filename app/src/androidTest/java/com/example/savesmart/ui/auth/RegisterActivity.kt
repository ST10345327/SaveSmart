package com.savesmart.ui.auth

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.savesmart.R
import com.savesmart.data.database.SaveSmartDatabase
import com.savesmart.data.repository.SaveSmartRepository
import com.savesmart.util.SecurityUtils

class RegisterActivity : AppCompatActivity() {

    private lateinit var repository: SaveSmartRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_register)

        val db = SaveSmartDatabase.getDatabase(this)
        repository = SaveSmartRepository(db)

        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister)

        btnRegister.setOnClickListener {

            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // Validate input using SecurityUtils
            if (!SecurityUtils.isValidUsername(username)) {
                Toast.makeText(this, "Invalid username", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!SecurityUtils.isValidPassword(password)) {
                Toast.makeText(this, "Weak password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val hashedPassword = SecurityUtils.hashPassword(password)

            Thread {
                val success = repository.registerUser(username, hashedPassword)

                runOnUiThread {
                    if (success) {
                        Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                        finish() // go back to login
                    } else {
                        Toast.makeText(this, "User already exists", Toast.LENGTH_SHORT).show()
                    }
                }
            }.start()
        }
    }
}