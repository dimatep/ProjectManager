package learning.self.kotlin.projectmanager.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_create_board.*
import kotlinx.android.synthetic.main.activity_my_profile.*
import learning.self.kotlin.projectmanager.R
import learning.self.kotlin.projectmanager.firebase.FireStoreHandler
import learning.self.kotlin.projectmanager.models.Board
import learning.self.kotlin.projectmanager.utils.Constants
import java.io.IOException

class CreateBoardActivity : BaseActivity() {

    private var mSelectedImageFileUri: Uri? = null
    private lateinit var mUserName: String
    private var mBoardImageURL: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_board)
        setFonts()
        setActionBar()

        if (intent.hasExtra(Constants.NAME)) {
            mUserName = intent.getStringExtra(Constants.NAME)
        }

        create_board_image.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this)
            } else {
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.READ_STORAGE_PERMISSION_CODE)
            }
        }

        create_board_create_btn.setOnClickListener {
            if(mSelectedImageFileUri != null){
                uploadBoardImage()
            }else{
                showProgressDialog()
                createBoard()
            }
        }


    }

    private fun createBoard() {
        val assignedUsers: ArrayList<String> = ArrayList()
        assignedUsers.add(getCurrentUserID())

        var board = Board(
            create_board_name_et.text.toString(),
            mBoardImageURL,
            mUserName,
            assignedUsers
        )

        FireStoreHandler().createBoard(this, board)

    }

    private fun uploadBoardImage() {
        showProgressDialog()

        val sRef: StorageReference = FirebaseStorage.getInstance()
            .reference
            .child(
                "BOARD_IMAGE" + System.currentTimeMillis()
                        + "." + Constants.getFileExtension(this, mSelectedImageFileUri)
            )

        sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener { taskSnapshot ->
            Log.e(
                "Firebase Image URL",
                taskSnapshot.metadata!!.reference!!.downloadUrl!!.toString()
            )

            taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                Log.e("Downloadable Image URL", uri.toString())
                mBoardImageURL = uri.toString()

                createBoard()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
            hideProgressDialog()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this)
            }
        } else {
            Toast.makeText(
                this, "You just denied permission for storage." +
                        "You can allow it from the settings.", Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //check if the user select a photo from gallery
        if (resultCode == Activity.RESULT_OK
            && requestCode == Constants.PICK_IMAGE_REQUEST_CODE
            && data!!.data != null
        ) {
            mSelectedImageFileUri = data.data
            // set the new photo to profile image
            try {
                Glide.with(this)
                    .load(mSelectedImageFileUri)
                    .fitCenter()
                    .placeholder(R.drawable.ic_board_place_holder)
                    .into(create_board_image)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun boardCreatedSuccessfully() {
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun setFonts() {
        val regularFont: Typeface = Typeface.createFromAsset(assets, "Raleway-Regular.ttf")
        create_board_name_et.typeface = regularFont
        create_board_create_btn.typeface = regularFont
    }

    private fun setActionBar() {
        setSupportActionBar(toolbar_create_board_activity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_white)
            actionBar.title = "Create Board"
        }
        toolbar_create_board_activity.setNavigationOnClickListener { onBackPressed() }
    }
}