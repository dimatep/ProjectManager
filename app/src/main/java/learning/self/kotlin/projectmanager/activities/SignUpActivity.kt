package learning.self.kotlin.projectmanager.activities

import android.graphics.Typeface
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_sign_up.*
import learning.self.kotlin.projectmanager.R
import learning.self.kotlin.projectmanager.firebase.FireStoreHandler
import learning.self.kotlin.projectmanager.models.User

class SignUpActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setActionBar()
        setFonts()

        sign_up_page_btn.setOnClickListener {
            registerUser()
        }
    }

    private fun setFonts() {
        val regularFont: Typeface = Typeface.createFromAsset(assets, "Raleway-Regular.ttf")
        val mediumFont: Typeface = Typeface.createFromAsset(assets, "Raleway-Medium.ttf")
        name_et.typeface = regularFont
        email_et.typeface = regularFont
        sign_up_description_tv.typeface = mediumFont
        sign_up_page_btn.typeface = regularFont
    }

    private fun setActionBar() {
        setSupportActionBar(toolbar_sign_up_activity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_black)
        }
        toolbar_sign_up_activity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun registerUser() {
        val name = name_et.text.toString().trim { it <= ' ' } //remove spaces
        val email = email_et.text.toString().trim { it <= ' ' }
        val password = password_et.text.toString().trim { it <= ' ' }

        if (validateForm(name, email, password)) {
            showProgressDialog()
            FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        //send user details for user creation in firebase
                        val firebaseUser: FirebaseUser = task.result!!.user!!
                        val registeredEmail = firebaseUser.email!!
                        val user = User(firebaseUser.uid, name, registeredEmail)
                        FireStoreHandler().registerUser(this,user)
                    } else {
                        Toast.makeText(
                            this,
                            "Registration Failed. Try Again",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

        }
    }

    fun userRegisteredSuccess(){
        Toast.makeText(this,
            "You have successfully registered",
            Toast.LENGTH_SHORT).show()
        hideProgressDialog()
        FirebaseAuth.getInstance().signOut()
        finish()
    }

    private fun validateForm(name: String, email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(name) -> {
                showErrorSnackBar("Please enter a name")
                false
            }
            TextUtils.isEmpty(email) -> {
                showErrorSnackBar("Please enter an email address")
                false
            }
            TextUtils.isEmpty(password) -> {
                showErrorSnackBar("Please enter a password")
                false
            }
            else -> {
                true
            }
        }
    }
}