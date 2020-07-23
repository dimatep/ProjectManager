package learning.self.kotlin.projectmanager.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.dialog_list.view.*
import learning.self.kotlin.projectmanager.R
import learning.self.kotlin.projectmanager.adapters.LabelColorListItemAdapter

abstract class LabelColorListDialog (
    context : Context,
    private var list : ArrayList<String>,
    private val title : String = "",
    private val mSelectedColor : String = ""
) : Dialog(context){

    private var adapter : LabelColorListItemAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val view = LayoutInflater.from(context).inflate(R.layout.dialog_list, null)
        setContentView(view)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        setUpRecyclerView(view)
    }

    private fun setUpRecyclerView(view : View){
        view.dialog_title_tv.text = title

        view.dialog_list_rv.layoutManager = LinearLayoutManager(context)
        adapter = LabelColorListItemAdapter(context, list, mSelectedColor)
        view.dialog_list_rv.adapter = adapter

        adapter!!.onItemClickListener = object  : LabelColorListItemAdapter.OnItemClickListener{
            override fun onClick(position: Int, color: String) {
                dismiss()
                onItemSelected(color)
            }
        }
    }

    protected abstract fun onItemSelected(color : String)
}