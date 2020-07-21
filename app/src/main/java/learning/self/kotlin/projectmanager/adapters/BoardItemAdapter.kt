package learning.self.kotlin.projectmanager.adapters

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_board.view.*
import learning.self.kotlin.projectmanager.R
import learning.self.kotlin.projectmanager.models.Board

open class BoardItemAdapter (private val context:Context, private var list : ArrayList<Board>)
    :RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var onClickListener : OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(LayoutInflater.from(context)
            .inflate(R.layout.item_board, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if(holder is MyViewHolder){
            val regularFont: Typeface = Typeface.createFromAsset(holder.itemView.getContext().assets, "Raleway-Regular.ttf")
            val boldFont: Typeface = Typeface.createFromAsset(holder.itemView.getContext().assets, "Raleway-Bold.ttf")

            Glide
                .with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.ic_board_place_holder)
                .into(holder.itemView.item_board_iv)
            //set fonts
            holder.itemView.item_board_name_tv.typeface = boldFont
            holder.itemView.item_board_created_by_tv.typeface = regularFont
            //set text
            holder.itemView.item_board_name_tv.text = model.name
            holder.itemView.item_board_created_by_tv.text = "Created by: " + model.createdBy

            holder.itemView.setOnClickListener {
                if(onClickListener != null){
                    onClickListener!!.onClick(position,model)
                }
            }
        }
    }

    fun setOnClickListener(onClickListener : OnClickListener){
        this.onClickListener = onClickListener

    }

    interface OnClickListener{
        fun onClick(position : Int, model : Board)
    }

    private class MyViewHolder(view : View): RecyclerView.ViewHolder(view){

    }
}