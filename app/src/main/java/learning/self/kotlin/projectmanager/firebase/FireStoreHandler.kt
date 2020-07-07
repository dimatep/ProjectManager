package learning.self.kotlin.projectmanager.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import learning.self.kotlin.projectmanager.activities.SignInActivity
import learning.self.kotlin.projectmanager.activities.SignUpActivity
import learning.self.kotlin.projectmanager.models.User
import learning.self.kotlin.projectmanager.utils.Constants

class FireStoreHandler {
    private val mFireStore = FirebaseFirestore.getInstance()

    //get user details from sign-up activity and create a new user in firebase
    fun registerUser(activity: SignUpActivity, userInfo : User){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisteredSuccess() //show toast to the user
            }
    }

    fun signInUser(activity: SignInActivity){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener { document->
                val loggedInUser = document.toObject(User::class.java)
                if(loggedInUser != null){
                    activity.signInSuccess(loggedInUser)
                }
            }
    }

    fun getCurrentUserId() : String {
        var currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID  = ""
        if(currentUser != null){
            currentUserID = currentUser.uid
        }
        return currentUserID
    }
}