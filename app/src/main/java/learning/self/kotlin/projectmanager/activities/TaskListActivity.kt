package learning.self.kotlin.projectmanager.activities

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_task_list.*
import learning.self.kotlin.projectmanager.R
import learning.self.kotlin.projectmanager.adapters.TaskItemAdapter
import learning.self.kotlin.projectmanager.firebase.FireStoreHandler
import learning.self.kotlin.projectmanager.models.Board
import learning.self.kotlin.projectmanager.models.Task
import learning.self.kotlin.projectmanager.utils.Constants

class TaskListActivity : BaseActivity() {

    private lateinit var mBoardDetails: Board

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)

        var boardDocumentID = ""
        if(intent.hasExtra(Constants.DOCUMENT_ID)){
            boardDocumentID = intent.getStringExtra(Constants.DOCUMENT_ID)
        }
        showProgressDialog()
        FireStoreHandler().getBoardDetails(this,boardDocumentID)
    }

    fun boardDetails(board : Board){
        mBoardDetails = board
        hideProgressDialog()
        setActionBar()

        val addTaskList = Task("Add List")
        board.taskList.add(addTaskList)

        task_list_rv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false)
        task_list_rv.setHasFixedSize(true)

        val adapter = TaskItemAdapter(this,board.taskList)
        task_list_rv.adapter = adapter
    }

    private fun setActionBar(){
        setSupportActionBar(task_list_activity_toolbar)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_white)
            actionBar.title = mBoardDetails.name
        }
        task_list_activity_toolbar.setNavigationOnClickListener{onBackPressed()}
    }

    fun addUpdateTaskListSuccess(){
        hideProgressDialog()
        showProgressDialog()
        FireStoreHandler().getBoardDetails(this,mBoardDetails.documentID)
    }

    fun createTaskList(taskName : String){
        val task = Task(taskName,FireStoreHandler().getCurrentUserId())
        mBoardDetails.taskList.add(0,task)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)

        showProgressDialog()
        FireStoreHandler().addUpdateTaskList(this,mBoardDetails)
    }

}