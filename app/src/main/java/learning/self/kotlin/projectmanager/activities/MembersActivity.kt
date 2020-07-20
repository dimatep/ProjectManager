package learning.self.kotlin.projectmanager.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_members.*
import kotlinx.android.synthetic.main.activity_task_list.*
import learning.self.kotlin.projectmanager.R
import learning.self.kotlin.projectmanager.adapters.MemberItemAdapter
import learning.self.kotlin.projectmanager.adapters.TaskItemAdapter
import learning.self.kotlin.projectmanager.firebase.FireStoreHandler
import learning.self.kotlin.projectmanager.models.Board
import learning.self.kotlin.projectmanager.models.User
import learning.self.kotlin.projectmanager.utils.Constants

class MembersActivity : BaseActivity() {

    private lateinit var mBoardDetails : Board
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

    fun setUpMembersList(list : ArrayList<User>){
        hideProgressDialog()

        members_list_rv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false)
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
}