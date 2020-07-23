package learning.self.kotlin.projectmanager.adapters

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_card.view.*
import learning.self.kotlin.projectmanager.R
import learning.self.kotlin.projectmanager.activities.TaskListActivity
import learning.self.kotlin.projectmanager.models.Card
import learning.self.kotlin.projectmanager.models.SelectedMembers

open class CardListItemAdapter(private val context: Context, private var list: ArrayList<Card>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var onClickListener : CardListItemAdapter.OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context)
            .inflate(R.layout.item_card, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if(holder is MyViewHolder){
            val regularFont: Typeface = Typeface.createFromAsset(holder.itemView.getContext().assets, "Raleway-Regular.ttf")
            holder.itemView.card_name_tv.typeface = regularFont
            holder.itemView.card_name_tv.text = model.name

            if(model.labelColor.isNotEmpty()){
                holder.itemView.view_label_color.visibility = View.VISIBLE
                holder.itemView.view_label_color.setBackgroundColor(
                    Color.parseColor(model.labelColor))
            }else{
                holder.itemView.view_label_color.visibility = View.GONE
            }

            //set the assigned members under the card name as circle images
            if((context as TaskListActivity).mAssignedMembersDetailList.size > 0){

                val selectedMembersList : ArrayList<SelectedMembers> = ArrayList()
                val assignedMembers = context.mAssignedMembersDetailList

                for(i in assignedMembers.indices){
                    for (j in model.assignedTo){
                        if(assignedMembers[i].id == j){
                            val selectedMember = SelectedMembers(assignedMembers[i].id, assignedMembers[i].image)
                            selectedMembersList.add(selectedMember)
                        }
                    }
                }

                if(selectedMembersList.size > 0){
                    //if the only member in the list is the creator - do not show the recycler view
                    if(selectedMembersList.size == 1
                        && selectedMembersList[0].id == model.createdBy){
                        holder.itemView.card_selected_members_list_rv.visibility = View.GONE
                    }else{ //add all the assigned members to the recycler view under the card name create and set the adapter
                        holder.itemView.card_selected_members_list_rv.visibility = View.VISIBLE
                        holder.itemView.card_selected_members_list_rv.layoutManager =
                            GridLayoutManager(context, 4)
                        val adapter = CardMemberListItemAdapter(context, selectedMembersList, false)
                        holder.itemView.card_selected_members_list_rv.adapter = adapter
                        adapter.setOnClickListener(object : CardMemberListItemAdapter.OnClickListener{
                            override fun onClick() {
                                if(onClickListener != null){
                                    onClickListener!!.onClick(position)
                                }
                            }
                        })
                    }
                }else{
                    holder.itemView.card_selected_members_list_rv.visibility = View.GONE
                }
            }

            holder.itemView.setOnClickListener {
                if(onClickListener != null){
                    onClickListener!!.onClick(position)
                }
            }
        }
    }

    fun setOnClickListener(onClickListener : OnClickListener){
        this.onClickListener = onClickListener

    }

    interface OnClickListener{
        fun onClick(position : Int)
    }

    private class MyViewHolder(view : View): RecyclerView.ViewHolder(view){

    }
}