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

    fun addUpdateTaskList (activity: Activity, board : Board){

        val taskListHashMap = HashMap<String, Any>()
        taskListHashMap[Constants.TASK_LIST] = board.taskList

        mFireStore.collection(Constants.BOARDS)
            .document(board.documentID)
            .update(taskListHashMap)
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName, "TaskList updated successfully.")

                if (activity is TaskListActivity) {
                    activity.addUpdateTaskListSuccess()
                } else if (activity is CardDetailsActivity) {
                    activity.addUpdateTaskListSuccess()
                }
            }
            .addOnFailureListener { e ->
                if (activity is TaskListActivity) {
                    activity.hideProgressDialog()
                } else if (activity is TaskListActivity) {
                    activity.hideProgressDialog()
                }
                Log.e(activity.javaClass.simpleName, "Error while creating a board.", e)
            }
    }

    fun updateUserProfileData(activity: Activity, userHashMap : HashMap<String, Any>){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .update(userHashMap)
            .addOnSuccessListener {
                Toast.makeText(activity,"Profile updated successfully!",Toast.LENGTH_SHORT).show()
                when(activity){
                    is MainActivity -> {
                        activity.tokenUpdateSuccess()
                    }

                    is MyProfileActivity ->{
                        activity.profileUpdateSuccess()
                    }
                }
            }.addOnFailureListener {
                when(activity){
                    is MainActivity -> {
                        activity.hideProgressDialog()
                    }

                    is MyProfileActivity ->{
                        activity.hideProgressDialog()
                    }
                }
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

    fun getAssignedMembersListDetails(activity: Activity, assignedTo : ArrayList<String>){
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

                if(activity is MembersActivity){
                    activity.setUpMembersList(usersList)
                }else if(activity is TaskListActivity){
                    activity.boardMembersDetailList(usersList)
                }
            }
            .addOnFailureListener {
                e ->
                if(activity is MembersActivity){
                    activity.hideProgressDialog()
                }else if(activity is TaskListActivity){
                    activity.hideProgressDialog()
                }
                Log.e(activity.javaClass.simpleName, "Error while creating a List of members", e)
            }
    }

    fun getMemberDetails(activity: MembersActivity, email : String){
        mFireStore.collection(Constants.USERS)
            .whereEqualTo(Constants.EMAIL, email)
            .get()
            .addOnSuccessListener {
                document ->
                if(document.documents.size > 0){
                    val user = document.documents[0].toObject(User::class.java)!!
                    activity.memberDetails(user)
                }else{
                    activity.hideProgressDialog()
                    activity.showErrorSnackBar("No such member found")
                }
            }
            .addOnFailureListener {
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while getting user details")
            }
    }

    fun assignMemberToBoard(activity: MembersActivity, board : Board, user : User){
        val assignedToHashMap = HashMap<String, Any>()
        assignedToHashMap[Constants.ASSIGNED_TO] = board.assignedTo

        mFireStore.collection(Constants.BOARDS)
            .document(board.documentID)
            .update(assignedToHashMap)
            .addOnSuccessListener {
                activity.memberAssignSuccess(user)
            }
            .addOnFailureListener {
                e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while creating a board", e)
            }
    }
}