package learning.self.kotlin.projectmanager

import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_splash.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // This is used to hide the status bar and make the splash screen as a full screen activity.
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        // This is used to get the font from the assets folder and set it to the title textView.
        val regularFont: Typeface = Typeface.createFromAsset(assets, "Raleway-Regular.ttf")
        val mediumFont: Typeface = Typeface.createFromAsset(assets, "Raleway-Medium.ttf")
        val boldFont: Typeface = Typeface.createFromAsset(assets, "Raleway-Bold.ttf")
        // set font to text views and buttons
        app_name_intro_tv.typeface = boldFont
        lets_go_tv.typeface = mediumFont
        app_description_tv.typeface = regularFont
        sign_in_btn.typeface = regularFont
        sign_up_btn.typeface = regularFont
    }
}