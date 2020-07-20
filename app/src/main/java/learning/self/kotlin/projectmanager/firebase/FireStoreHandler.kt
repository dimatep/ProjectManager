package learning.self.kotlin.projectmanager.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import learning.self.kotlin.projectmanager.activities.*
import learning.self.kotlin.projectmanager.models.Board
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

    fun getBoardDetails(activity : TaskListActivity, documentID : String){
        mFireStore.collection(Constants.BOARDS)
            .document(documentID)
            .get()
            .addOnSuccessListener {
                    document ->
                Log.e("GetBoardList", document.toString())
                val board = document.toObject(Board::class.java)!!
                board.documentID = document.id
                activity.boardDetails(board)

            }.addOnFailureListener {
                activity.hideProgressDialog()
            }
    }

    fun createBoard(activity: CreateBoardActivity, boardInfo : Board){
        mFireStore.collection(Constants.BOARDS)
            .document()
            .set(boardInfo, SetOptions.merge())
            .addOnSuccessListener {
                Toast.makeText(activity,"Board created successfully", Toast.LENGTH_SHORT).show()
                activity.boardCreatedSuccessfully() //show toast to the user
            }.addOnFailureListener {
                activity.hideProgressDialog()
            }
    }

    fun addUpdateTaskList (activity: TaskListActivity, board : Board){

        val taskListHashMap = HashMap<String, Any>()
        taskListHashMap[Constants.TASK_LIST] = board.taskList

        mFireStore.collection(Constants.BOARDS)
            .document(board.documentID)
            .update(taskListHashMap)
            .addOnSuccessListener {
                Toast.makeText(activity,"Task List updated successfully!",Toast.LENGTH_SHORT).show()
                activity.addUpdateTaskListSuccess()
            }.addOnFailureListener {
                activity.hideProgressDialog()
                Toast.makeText(activity,"Failed updating Task List!",Toast.LENGTH_SHORT).show()
            }

    }

    fun updateUserProfileData(activity: MyProfileActivity, userHashMap : HashMap<String, Any>){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .update(userHashMap)
            .addOnSuccessListener {
                Toast.makeText(activity,"Profile updated successfully!",Toast.LENGTH_SHORT).show()
                activity.profileUpdateSuccess()
            }.addOnFailureListener {
                activity.hideProgressDialog()
                Toast.makeText(activity,"Failed updating profile!",Toast.LENGTH_SHORT).show()

            }
    }

    fun getBoardsList(activity: MainActivity){
        mFireStore.collection(Constants.BOARDS)
            .whereArrayContains(Constants.ASSIGNED_TO, getCurrentUserId())
            .get()
            .addOnSuccessListener {
                document ->
                Log.e("GetBoardList", document.documents.toString())
                val boardsList : ArrayList<Board> = ArrayList()
                for (i in  document.documents){
                    val board = i.toObject(Board::class.java)!!
                    board.documentID = i.id
                    boardsList.add(board);
                }
                //pass the result to base activity
                activity.populateBoardsToUI(boardsList)
            }.addOnFailureListener {
                activity.hideProgressDialog()
            }
    }

    fun loadUserData(activity: Activity, readBoardsList : Boolean = false){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                val loggedInUser = document.toObject(User::class.java)!!
                when (activity) {
                    is SignInActivity -> {
                        activity.signInSuccess(loggedInUser)
                    }
                    is MainActivity -> {
                        activity.updateNavigationUserDetails(loggedInUser, readBoardsList)
                    }
                    is MyProfileActivity -> {
                        activity.setUserDataInUI(loggedInUser)
                    }
                }

            }.addOnFailureListener {
                when(activity) {
                    is SignInActivity -> {
                        activity.hideProgressDialog()
                    }
                    is MainActivity -> {
                        activity.hideProgressDialog()
                    }

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

    fun getAssignedMembersListDetails(activity: MembersActivity, assignedTo : ArrayList<String>){
        mFireStore.collection(Constants.USERS)
            .whereIn(Constants.ID, assignedTo)
            .get()
            .addOnSuccessListener {
                document ->
                Log.e(activity.javaClass.simpleName, document.documents.toString())

                val usersList : ArrayList<User> = ArrayList()

                for(i in document.documents){
                    val user = i.toObject(User::class.java)!!
                    usersList.add(user)
                }

                activity.setUpMembersList(usersList)
            }
            .addOnFailureListener {
                e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while creating a List of members", e)

            }
    }
}