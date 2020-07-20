package learning.self.kotlin.projectmanager.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_task_list.*
import learning.self.kotlin.projectmanager.R
import learning.self.kotlin.projectmanager.adapters.TaskItemAdapter
import learning.self.kotlin.projectmanager.firebase.FireStoreHandler
import learning.self.kotlin.projectmanager.models.Board
import learning.self.kotlin.projectmanager.models.Card
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_members,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_members -> {
                val intent = Intent(this,MembersActivity::class.java)
                intent.putExtra(Constants.BOARD_DETAIL, mBoardDetails)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
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

    fun updateTaskList(position: Int, listName: String, model : Task){
        val task = Task(listName,model.createdBy)
        mBoardDetails.taskList[position] = task
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)

        showProgressDialog()
        FireStoreHandler().addUpdateTaskList(this,mBoardDetails)
    }

    fun deleteTaskList(position : Int){
        mBoardDetails.taskList.removeAt(position)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)

        showProgressDialog()
        FireStoreHandler().addUpdateTaskList(this,mBoardDetails)
    }

    fun addCardToTask(position : Int , cardName:String){
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)

        val cardAssignedUsersList : ArrayList<String> = ArrayList()
        val currentUserID = FireStoreHandler().getCurrentUserId()
        cardAssignedUsersList.add(currentUserID)
        val card = Card(cardName, currentUserID,cardAssignedUsersList)

        val cardList = mBoardDetails.taskList[position].cards
        cardList.add(card) //add the card to cads list in the task list

        val task = Task(mBoardDetails.taskList[position].title,
            mBoardDetails.taskList[position].createdBy,
            cardList)

        mBoardDetails.taskList[position] = task

        showProgressDialog()
        FireStoreHandler().addUpdateTaskList(this,mBoardDetails)
    }
}