package learning.self.kotlin.projectmanager.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_label_color.view.*
import learning.self.kotlin.projectmanager.R

class LabelColorListItemAdapter(private val context: Context, private var list: ArrayList<String>, private val mSelectedColor : String)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    var onItemClickListener : OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.item_label_color, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]

        if(holder is MyViewHolder){
            holder.itemView.view_main.setBackgroundColor(Color.parseColor(item))
            if(item == mSelectedColor){
                holder.itemView.selected_color_iv.visibility = View.VISIBLE
            }else{
                holder.itemView.selected_color_iv.visibility = View.GONE
            }

            holder.itemView.setOnClickListener {
                if(onItemClickListener != null){
                    onItemClickListener!!.onClick(position, item)
                }
            }
        }
    }


    interface OnItemClickListener{
        fun onClick(position : Int, color : String)
    }

    private class MyViewHolder(view : View): RecyclerView.ViewHolder(view)
}