package learning.self.kotlin.projectmanager

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // This is used to hide the status bar and make the splash screen as a full screen activity.
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        // This is used to get the font from the assets folder and set it to the title textView.
        val myFont: Typeface = Typeface.createFromAsset(assets, "Raleway-Bold.ttf")
        app_name_tv.typeface = myFont

        // Adding the handler to after the a task after some delay.
        Handler().postDelayed({
            // Start the Login Activity
            startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            finish() // Call this when your activity is done and should be closed.
        }, 2500) // Here we pass the delay time in milliSeconds after which the splash activity will disappear.

    }
}