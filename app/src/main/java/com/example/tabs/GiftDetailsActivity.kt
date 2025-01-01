package com.example.tabs

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class GiftDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContentView(R.layout.activity_gift_details)

        val giftName = intent.getStringExtra("giftName")
        val giftPrice = intent.getStringExtra("giftPrice")
        val giftImage = intent.getStringExtra("giftImage")
        val giftRecommendations = intent.getStringArrayListExtra("giftRecommendations")

        findViewById<TextView>(R.id.giftNameTextView).text = giftName
        findViewById<TextView>(R.id.giftPriceTextView).text = giftPrice
        val imageView = findViewById<ImageView>(R.id.giftImageView)
        imageView.setImageResource(resources.getIdentifier(giftImage, "drawable", packageName))

        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }

        // Display recommendations or any other data as needed
    }
}
