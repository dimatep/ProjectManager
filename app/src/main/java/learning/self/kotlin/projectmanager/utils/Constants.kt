package learning.self.kotlin.projectmanager.utils

import android.app.Activity
import android.content.Intent
import android.content.res.AssetManager
import android.graphics.Typeface
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import learning.self.kotlin.projectmanager.R
import learning.self.kotlin.projectmanager.activities.CreateBoardActivity
import java.security.AccessController.getContext

object Constants{
    const val USERS : String = "users"
    const val BOARDS : String = "boards"
    const val IMAGE : String = "image"
    const val NAME : String = "name"
    const val MOBILE : String = "mobile"
    const val ASSIGNED_TO : String = "assignedTo"

    const val READ_STORAGE_PERMISSION_CODE = 1
    const val PICK_IMAGE_REQUEST_CODE = 2
    const val DOCUMENT_ID : String = "documentID"
    const val TASK_LIST : String = "taskList"
    const val BOARD_DETAIL : String = "board_detail"
    const val ID : String = "id"
    const val EMAIL : String = "email"
    const val BOARD_MEMBERS_LIST :String = "board_members_list"
    const val SELECT : String = "select"
    const val UN_SELECT : String = "unselect"

    const val TASK_LIST_ITEM_POSITION : String = "task_list_item_position"
    const val CARD_LIST_ITEM_POSITION : String = "card_list_item_position"

    const val POJECTMANAGER_PREFERENCES = "Project_manager_preferences"
    const val FCM_TOKEN_UPDATED = "fcm_token_updated"
    const val FCM_TOKEN = "fcm_token"

    const val FCM_BASE_URL:String = "https://fcm.googleapis.com/fcm/send"
    const val FCM_AUTHORIZATION:String = "authorization"
    const val FCM_KEY:String = "key"
    const val FCM_SERVER_KEY:String = R.string.fcm_key.toString()
    const val FCM_KEY_TITLE:String = "title"
    const val FCM_KEY_MESSAGE:String = "message"
    const val FCM_KEY_DATA:String = "data"
    const val FCM_KEY_TO:String = "to"

    fun getFileExtension(activity : Activity, uri : Uri?) : String?{
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }

    fun showImageChooser(activity : Activity){
        var galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }
}