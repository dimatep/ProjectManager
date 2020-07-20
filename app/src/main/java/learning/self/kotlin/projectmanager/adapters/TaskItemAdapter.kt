package learning.self.kotlin.projectmanager.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_task.view.*
import learning.self.kotlin.projectmanager.R
import learning.self.kotlin.projectmanager.activities.TaskListActivity
import learning.self.kotlin.projectmanager.models.Task

open class TaskItemAdapter(private val context: Context, private var list : ArrayList<Task>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false)
        //view holder will be 70% of the screen
        val layoutParams = LinearLayout.LayoutParams((parent.width * 0.7).toInt(), LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins((15.toDP()).toPX(), 0, (40.toDP()).toPX(), 0)
        view.layoutParams = layoutParams
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if(holder is MyViewHolder){
            if(position == list.size-1){
                holder.itemView.add_task_list_tv.visibility = View.VISIBLE
                holder.itemView.task_item_ll.visibility = View.GONE
            }else{
                holder.itemView.add_task_list_tv.visibility = View.GONE
                holder.itemView.task_item_ll.visibility = View.VISIBLE
            }

            holder.itemView.task_list_title_tv.text = model.title
            holder.itemView.add_task_list_tv.setOnClickListener {
                holder.itemView.add_task_list_tv.visibility = View.GONE
                holder.itemView.add_task_list_name_cv.visibility = View.VISIBLE
            }

            holder.itemView.close_list_name_ib.setOnClickListener {
                holder.itemView.add_task_list_tv.visibility = View.VISIBLE
                holder.itemView.add_task_list_name_cv.visibility = View.GONE
            }

            holder.itemView.done_list_name_ib.setOnClickListener {
                val listName = holder.itemView.task_list_name_et.text.toString()

                if(listName.isNotEmpty()){
                    if(context is TaskListActivity){
                        context.createTaskList(listName)
                    }
                }else{
                    Toast.makeText(context,"Please enter a list name", Toast.LENGTH_SHORT).show()
                }
            }

            holder.itemView.edit_list_name_ib.setOnClickListener {
                holder.itemView.edit_task_list_name_et.setText(model.title)
                holder.itemView.title_view_ll.visibility = View.GONE
                holder.itemView.edit_task_list_name_cv.visibility = View.VISIBLE
            }

            holder.itemView.close_editable_view_ib.setOnClickListener {
                holder.itemView.title_view_ll.visibility = View.VISIBLE
                holder.itemView.edit_task_list_name_cv.visibility = View.GONE
            }

            holder.itemView.done_edit_list_name_ib.setOnClickListener {
                val listName = holder.itemView.edit_task_list_name_et.text.toString()
                if(listName.isNotEmpty()){
                    if(context is TaskListActivity){
                        context.updateTaskList(position,listName, model)
                    }
                }else{
                    Toast.makeText(context,"Please enter a list name", Toast.LENGTH_SHORT).show()
                }
            }

            holder.itemView.delete_list_ib.setOnClickListener {
                alertDialogForDeleteList(position, model.title)
            }

            holder.itemView.add_card_tv.setOnClickListener {
                holder.itemView.add_card_tv.visibility = View.GONE
                holder.itemView.add_card_cv.visibility = View.VISIBLE
            }

            holder.itemView.close_card_name_ib.setOnClickListener {
                holder.itemView.add_card_tv.visibility = View.VISIBLE
                holder.itemView.add_card_cv.visibility = View.GONE
            }

            holder.itemView.done_card_name_ib.setOnClickListener {
                val cardName = holder.itemView.card_name_et.text.toString()
                if(cardName.isNotEmpty()){
                    if(context is TaskListActivity){
                       context.addCardToTask(position,cardName)
                    }
                }else{
                    Toast.makeText(context,"Please enter a card name", Toast.LENGTH_SHORT).show()
                }
            }

            holder.itemView.card_list_rv.layoutManager = LinearLayoutManager(context)
            holder.itemView.card_list_rv.setHasFixedSize(true)

            val adapter = CardListItemAdapter(context, model.cards)
            holder.itemView.card_list_rv.adapter = adapter

        }
    }

    private fun alertDialogForDeleteList(position : Int, title : String){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Alert!")
        builder.setMessage("Are you sure you want to delete $title?")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        builder.setPositiveButton("Yes") {
            dialog, which ->
            dialog.dismiss()

            if(context is TaskListActivity){
                context.deleteTaskList(position)
            }
        }

        builder.setNegativeButton("No") {
                dialog, which ->
                dialog.dismiss()
        }

        val alertDialog : AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun Int.toDP(): Int = (this/Resources.getSystem().displayMetrics.density).toInt()

    private fun Int.toPX(): Int = (this*Resources.getSystem().displayMetrics.density).toInt()

    private class MyViewHolder(view : View): RecyclerView.ViewHolder(view){

    }

}