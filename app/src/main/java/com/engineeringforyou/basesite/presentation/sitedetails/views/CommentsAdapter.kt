package com.engineeringforyou.basesite.presentation.sitedetails.views

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.engineeringforyou.basesite.R
import com.engineeringforyou.basesite.models.Comment
import kotlinx.android.synthetic.main.item_comment.view.*

class CommentsAdapter(items:List<Comment>) : RecyclerView.Adapter<CommentsAdapter.ViewHolder>() {

    private var mItems: List<Comment>? = items

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(position)
    }

    override fun getItemCount(): Int {
        return mItems!!.size
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_comment, viewGroup, false)
        return ViewHolder(view)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {
            val comment = mItems!![position]
            itemView.comment_name.text = comment.userName
            itemView.comment_text.text = comment.comment
        }
    }
}