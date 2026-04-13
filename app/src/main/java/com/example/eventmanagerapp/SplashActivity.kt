package com.example.eventmanagerapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    private var handler: Handler? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Prevent surface layer issues by keeping the window visible
        window.setFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )
        
        setContentView(R.layout.activity_splash)

        // Simulate a loading process
        handler = Handler(Looper.getMainLooper())
        handler?.postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }, 3000) // 3 seconds
    }
    
    override fun onPause() {
        super.onPause()
        // Prevent surface cleanup when activity is paused
        if (!isFinishing) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            )
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // Clean up handler to prevent memory leaks
        handler?.removeCallbacksAndMessages(null)
        handler = null
    }
} 