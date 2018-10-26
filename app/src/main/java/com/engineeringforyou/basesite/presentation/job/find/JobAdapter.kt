package com.engineeringforyou.basesite.presentation.job.find

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.engineeringforyou.basesite.R
import com.engineeringforyou.basesite.models.Job

    class JobAdapter : RecyclerView.Adapter<JobAdapter.ViewHolder>() {

        private var mItems = ArrayList<Job>()

        fun showList(list: List<Job>) {
            mItems.clear()
            mItems.addAll(list)
            notifyDataSetChanged()
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            viewHolder.bind(position)
        }

        override fun getItemCount(): Int {
            return mItems.size
        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
            val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_job, viewGroup, false)
            return ViewHolder(view)
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(position: Int) {
                val job = mItems[position]
//                itemView.Job_name.text = if(!TextUtils.isEmpty(Job.userName)) Job.userName else "(неизвестный)"
//                itemView.Job_text.text = Job.Job
//                itemView.Job_date.text = DateUtils.parseDate(Job.timestamp)
            }
        }
    }
