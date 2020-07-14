package learning.self.kotlin.projectmanager.activities

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_in.*
import learning.self.kotlin.projectmanager.R
import learning.self.kotlin.projectmanager.firebase.FireStoreHandler
import learning.self.kotlin.projectmanager.models.User

private lateinit var auth: FirebaseAuth

class SignInActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setActionBar()
        setFonts()

        auth = FirebaseAuth.getInstance();

        sign_in_page_btn.setOnClickListener {
            signInUser()
        }
    }

    fun signInSuccess(user : User){
        hideProgressDialog()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun signInUser(){
        val email = sign_in_email_et.text.toString().trim { it <= ' '}
        val password = sign_in_password_et.text.toString().trim { it <= ' '}

        if(validateForm(email, password)) {
            showProgressDialog()
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    hideProgressDialog()
                    if (task.isSuccessful) {
                        FireStoreHandler().loadUserData(this)
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(baseContext,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                    }
                }

        }
    }

    private fun validateForm(email:String, password:String) : Boolean{
        return when {
                TextUtils.isEmpty(email) -> {
                showErrorSnackBar("Please enter an email address")
                false
            }
            TextUtils.isEmpty(password) -> {
                showErrorSnackBar("Please enter a password")
                false
            }else->{
                true
            }
        }
    }

    private fun setFonts(){
        val regularFont: Typeface = Typeface.createFromAsset(assets, "Raleway-Regular.ttf")
        val mediumFont: Typeface = Typeface.createFromAsset(assets, "Raleway-Medium.ttf")
        sign_in_email_et.typeface = regularFont
        sign_in_description_tv.typeface = mediumFont
        sign_in_page_btn.typeface = regularFont
    }

    private fun setActionBar(){
        setSupportActionBar(toolbar_sign_in_activity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_black)
        }
        toolbar_sign_in_activity.setNavigationOnClickListener{onBackPressed()}
    }
}