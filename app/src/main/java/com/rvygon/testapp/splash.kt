package com.rvygon.testapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent



class splash : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //  setContentView(R.layout.activity_splash)
        val intent = Intent(this, MainActivity::class.java)
        Thread.sleep(2_000)
        startActivity(intent)
        finish()
    }
}
