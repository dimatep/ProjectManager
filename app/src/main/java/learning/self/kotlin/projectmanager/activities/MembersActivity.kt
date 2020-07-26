package learning.self.kotlin.projectmanager.activities

import android.app.Activity
import android.app.Dialog
import android.os.AsyncTask
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
import org.json.JSONObject
import java.io.*
import java.lang.Exception
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

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
                dialog.dismiss()
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
        SendNotificationToUserAsyncTask(mBoardDetails.name,user.fcmToken)
    }

    private inner class SendNotificationToUserAsyncTask(val boardName : String, val token : String): AsyncTask<Any, Void, String> (){

        override fun onPreExecute() {
            super.onPreExecute()
            showProgressDialog()
        }

        override fun doInBackground(vararg params: Any?): String {
            var result : String
            var connection : HttpURLConnection? = null
            try{
                val url = URL(Constants.FCM_BASE_URL)
                connection = url.openConnection() as HttpURLConnection
                connection.doOutput = true
                connection.doInput = true
                connection.instanceFollowRedirects = false
                connection.requestMethod = "POST"

                connection.setRequestProperty("Content-Type", "application/json")
                connection.setRequestProperty("charset", "utf-8")
                connection.setRequestProperty("Accept", "application/json")

                connection.setRequestProperty(Constants.FCM_AUTHORIZATION, "${Constants.FCM_KEY}=${Constants.FCM_SERVER_KEY}")
                connection.useCaches = false

                val wr = DataOutputStream(connection.outputStream)
                val jsonRequest = JSONObject()
                val dataObject = JSONObject()
                dataObject.put(Constants.FCM_KEY_TITLE, "Assigned to the board $boardName")
                dataObject.put(Constants.FCM_KEY_MESSAGE, "You have been assigned to the Board by ${mAssignedMembersList[0].name}")
                jsonRequest.put(Constants.FCM_KEY_DATA, dataObject)
                jsonRequest.put(Constants.FCM_KEY_TO, token)

                wr.writeBytes(jsonRequest.toString())
                wr.flush()
                wr.close()

                val httpResult : Int = connection.responseCode
                if(httpResult == HttpURLConnection.HTTP_OK){
                    val inputStream = connection.inputStream
                    val reader = BufferedReader(InputStreamReader(inputStream))

                    val stringBuilder = StringBuilder()
                    var line : String?
                    try{
                        while(reader.readLine().also {line=it} != null){
                            stringBuilder.append(line+"\n")
                        }
                    }catch (e : IOException){
                        e.printStackTrace()
                    }finally {
                        try{
                            inputStream.close()
                        }catch (e : IOException){
                            e.printStackTrace()
                        }
                    }
                    result = stringBuilder.toString()
                }else{
                    result = connection.responseMessage
                }
            }catch (e : SocketTimeoutException){
                result = "Connection Timeout"
            }catch (e : Exception){
                result = "Error : " + e.message
            }finally {
                connection?.disconnect()
            }
            return result
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            hideProgressDialog()
        }
    }
}