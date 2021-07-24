package com.example.memesapp

import android.content.Intent
import kotlinx.android.synthetic.main.activity_main.*
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.core.view.GestureDetectorCompat
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

class MainActivity : AppCompatActivity(), GestureDetector.OnGestureListener {

    private lateinit var mDetector: GestureDetectorCompat
    private val endpoint = "https://meme-api.herokuapp.com/gimme/dankmemes"
    private var currentImageUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mDetector = GestureDetectorCompat(this, this)

        getImage(endpoint)
    }

    private fun getImage(endpoint: String) {
        progress_bar.visibility = View.VISIBLE
        textView.visibility = View.GONE
        imageView2.visibility = View.GONE

        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, endpoint, null,
            { response ->
                currentImageUrl = response.getString("url")
                val title = response.getString("title")
                loadImage(currentImageUrl, title)
            },
            { error ->
                Log.d("error", "That didn't work!")
            }
        )
        // Add the request to the RequestQueue.
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
    }

    private fun loadImage(currentImageUrl: String?, title: String) {
        Glide.with(this).load(currentImageUrl).listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                Toast.makeText(
                    applicationContext,
                    "Sorry, couldn't load this meme,\nTry next",
                    Toast.LENGTH_SHORT
                ).show()
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                progress_bar.visibility = View.GONE
                textView.visibility = View.VISIBLE
                imageView2.visibility = View.VISIBLE
                return false
            }
        }).into(findViewById(R.id.imageView))

        textView.text = title
    }

    fun onShare(view: View) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "image"
        intent.putExtra(Intent.EXTRA_TEXT,"Hey! Checkout this meme from reddit, $currentImageUrl")
        val chooser = Intent.createChooser(intent, "Select any on the below...")
        startActivity(chooser)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (mDetector.onTouchEvent(event)) {
            true
        } else {
            super.onTouchEvent(event)
        }
    }

    override fun onFling(
        event1: MotionEvent,
        event2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        val x1 = event1.x
        val x2 = event2.x
        if (x1 - x2 > 200) {
            getImage(endpoint)
        }
        return true
    }

    override fun onLongPress(event: MotionEvent) {}

    override fun onScroll(
        event1: MotionEvent,
        event2: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        return true
    }

    override fun onDown(e: MotionEvent?): Boolean {
        return true
    }

    override fun onShowPress(event: MotionEvent) {}

    override fun onSingleTapUp(event: MotionEvent): Boolean {
        return true
    }
}