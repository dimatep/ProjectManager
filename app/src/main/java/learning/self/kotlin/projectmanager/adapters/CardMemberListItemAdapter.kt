package learning.self.kotlin.projectmanager.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_card_selected_member.view.*
import learning.self.kotlin.projectmanager.R
import learning.self.kotlin.projectmanager.models.SelectedMembers

open class CardMemberListItemAdapter(
    private val context: Context,
    private var list: ArrayList<SelectedMembers>,
    private val assignedMembers : Boolean)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var onClickListener : OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(LayoutInflater.from(context)
            .inflate(R.layout.item_card_selected_member,
                parent,
                false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if(holder is MyViewHolder){
            if(position == list.size -1 && assignedMembers){
                holder.itemView.add_member_iv.visibility = View.VISIBLE
                holder.itemView.selected_member_image_iv.visibility = View.GONE
            }else{
                holder.itemView.add_member_iv.visibility = View.GONE
                holder.itemView.selected_member_image_iv.visibility = View.VISIBLE

                //set users image
                Glide
                    .with(context)
                    .load(model.image)
                    .fitCenter()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(holder.itemView.selected_member_image_iv)
            }

            holder.itemView.setOnClickListener {
                if(onClickListener != null){
                    onClickListener!!.onClick()
                }
            }
        }
    }


    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick()
    }

    private class MyViewHolder(view : View): RecyclerView.ViewHolder(view)
}