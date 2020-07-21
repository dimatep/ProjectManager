package learning.self.kotlin.projectmanager.activities

import android.app.Activity
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_members.*
import kotlinx.android.synthetic.main.activity_task_list.*
import kotlinx.android.synthetic.main.dialog_serach_member.*
import learning.self.kotlin.projectmanager.R
import learning.self.kotlin.projectmanager.adapters.MemberItemAdapter
import learning.self.kotlin.projectmanager.adapters.TaskItemAdapter
import learning.self.kotlin.projectmanager.firebase.FireStoreHandler
import learning.self.kotlin.projectmanager.models.Board
import learning.self.kotlin.projectmanager.models.User
import learning.self.kotlin.projectmanager.utils.Constants

class MembersActivity : BaseActivity() {

    private lateinit var mBoardDetails : Board
    private lateinit var mAssignedMembersList : ArrayList<User>
    private var anyChangesMade : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_members)

        if(intent.hasExtra(Constants.BOARD_DETAIL)){
            mBoardDetails = intent.getParcelableExtra<Board>(Constants.BOARD_DETAIL)
            showProgressDialog()
            FireStoreHandler().getAssignedMembersListDetails(this,mBoardDetails.assignedTo)
        }
        setActionBar()
    }

    fun memberDetails(user : User){
        mBoardDetails.assignedTo.add(user.id)
        FireStoreHandler().assignMemberToBoard(this, mBoardDetails, user)
    }

    fun setUpMembersList(list : ArrayList<User>){
        mAssignedMembersList = list
        hideProgressDialog()

        members_list_rv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
        members_list_rv.setHasFixedSize(true)

        val adapter = MemberItemAdapter(this,list)
        members_list_rv.adapter = adapter
    }

    private fun setActionBar(){
        setSupportActionBar(members_activity_toolbar)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_white)
            actionBar.title = mBoardDetails.name + " Members"
        }
        members_activity_toolbar.setNavigationOnClickListener{onBackPressed()}
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_member, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
       when(item.itemId){
           R.id.action_add_member ->{
               dialogSearchMember()
               return true
           }
       }
        return super.onOptionsItemSelected(item)
    }

    private fun dialogSearchMember(){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_serach_member)

        dialog.add_tv.setOnClickListener {
            val email = dialog.email_search_member_et.text.toString()
            if(email.isNotEmpty()){
                showProgressDialog()
                FireStoreHandler().getMemberDetails(this, email)
            }else{
                Toast.makeText(this,"Please enter email address",Toast.LENGTH_SHORT).show()
            }
        }

        dialog.cancel_tv.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onBackPressed() {
        if(anyChangesMade){
            setResult(Activity.RESULT_OK)
        }
        super.onBackPressed()
    }

    fun memberAssignSuccess(user : User){
        hideProgressDialog()
        mAssignedMembersList.add(user)
        anyChangesMade = true
        setUpMembersList(mAssignedMembersList)
    }
}