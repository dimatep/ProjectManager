package learning.self.kotlin.projectmanager.activities

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_card_details.*
import kotlinx.android.synthetic.main.activity_my_profile.*
import kotlinx.android.synthetic.main.dialog_serach_member.*
import learning.self.kotlin.projectmanager.R
import learning.self.kotlin.projectmanager.firebase.FireStoreHandler
import learning.self.kotlin.projectmanager.models.Board
import learning.self.kotlin.projectmanager.models.Card
import learning.self.kotlin.projectmanager.models.Task
import learning.self.kotlin.projectmanager.utils.Constants

class CardDetailsActivity : BaseActivity() {
    private lateinit var mBoardDetails : Board
    private var mTaskListPosition = -1
    private var mCardListPosition = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_details)
        getIntentData()
        setFonts()
        setActionBar()

        name_card_details_et.setText(mBoardDetails.taskList[mTaskListPosition].cards[mCardListPosition].name)
        name_card_details_et.setSelection(name_card_details_et.text.toString().length) //set focus in the end of the text

        update_card_details_btn.setOnClickListener {
            if(name_card_details_et.text.toString().isNotEmpty()){
                updateCardDetails()
            }else{
                Toast.makeText(this,"Please enter a card name", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setFonts(){
        val regularFont: Typeface = Typeface.createFromAsset(assets, "Raleway-Regular.ttf")
        val boldFont: Typeface = Typeface.createFromAsset(assets, "Raleway-Bold.ttf")

        name_card_details_et.typeface = regularFont
        select_label_color_tv_1.typeface = boldFont
        select_label_color_tv.typeface = regularFont
        select_members_tv_1.typeface = boldFont
        select_members_tv.typeface = regularFont
        select_due_date_tv_1.typeface = boldFont
        select_due_date_tv.typeface = regularFont
        update_card_details_btn.typeface = regularFont

    }

    private fun setActionBar(){
        setSupportActionBar(toolbar_card_details_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_white)
            actionBar.title = mBoardDetails.taskList[mTaskListPosition].cards[mCardListPosition].name
        }

        toolbar_card_details_activity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun getIntentData(){

        if (intent.hasExtra(Constants.TASK_LIST_ITEM_POSITION)) {
            mTaskListPosition = intent.getIntExtra(Constants.TASK_LIST_ITEM_POSITION, -1)
        }
        if (intent.hasExtra(Constants.CARD_LIST_ITEM_POSITION)) {
            mCardListPosition = intent.getIntExtra(Constants.CARD_LIST_ITEM_POSITION, -1)
        }
        if (intent.hasExtra(Constants.BOARD_DETAIL)) {
            mBoardDetails = intent.getParcelableExtra(Constants.BOARD_DETAIL) as Board
        }
    }

    fun addUpdateTaskListSuccess(){
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun updateCardDetails(){
       val card = Card(
           name_card_details_et.text.toString(),
           mBoardDetails.taskList[mTaskListPosition].cards[mCardListPosition].createdBy,
           mBoardDetails.taskList[mTaskListPosition].cards[mCardListPosition].assignedTo
       )

        mBoardDetails.taskList[mTaskListPosition].cards[mCardListPosition] = card

        showProgressDialog()
        FireStoreHandler().addUpdateTaskList(this,mBoardDetails)
    }

    private fun deleteCard(){
        val cardsList : ArrayList<Card> = mBoardDetails.taskList[mTaskListPosition].cards
        cardsList.removeAt(mCardListPosition)

        val tasksList : ArrayList<Task> = mBoardDetails.taskList
        tasksList.removeAt(tasksList.size-1)

        tasksList[mTaskListPosition].cards = cardsList

        showProgressDialog()
        FireStoreHandler().addUpdateTaskList(this,mBoardDetails)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_delete_card, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_delete_card ->{
                val cardTitle = mBoardDetails.taskList[mTaskListPosition].cards[mCardListPosition].name
                alertDialogForDeleteCard(cardTitle)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun alertDialogForDeleteCard(cardName : String){
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Alert!")
        builder.setMessage("Are you sure you want to delete $cardName?")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        builder.setPositiveButton("Yes") {
                dialog, which ->
            dialog.dismiss()
            deleteCard()
        }

        builder.setNegativeButton("No") {
                dialog, which ->
            dialog.dismiss()
        }

        val alertDialog : AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
}