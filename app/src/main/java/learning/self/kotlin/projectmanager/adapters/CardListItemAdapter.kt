package learning.self.kotlin.projectmanager.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_card.view.*
import learning.self.kotlin.projectmanager.R
import learning.self.kotlin.projectmanager.models.Card

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
            holder.itemView.card_name_tv.text = model.name
        }
    }

    fun setOnClickListener(onClickListener : OnClickListener){
        this.onClickListener = onClickListener

    }

    interface OnClickListener{
        fun onClick(position : Int, model : Card)
    }

    private class MyViewHolder(view : View): RecyclerView.ViewHolder(view){

    }
}