package com.engineeringforyou.basesite.presentation.job.list

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.engineeringforyou.basesite.R
import com.engineeringforyou.basesite.models.Job
import com.engineeringforyou.basesite.utils.DateUtils
import kotlinx.android.synthetic.main.item_job.view.*

class JobListAdapter(val jobClick: (job: Job) -> Unit, val isAdminMode: Boolean) : RecyclerView.Adapter<JobListAdapter.ViewHolder>() {

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
            if (isAdminMode) {
                itemView.status_image.visibility = View.VISIBLE
                itemView.status_image.setImageResource(if (job.isPublic) R.drawable.ic_status_ok else R.drawable.ic_status_stop)
            } else itemView.status_image.visibility = View.GONE

            itemView.job_date.text = DateUtils.parseDate(job.timestamp)
            itemView.job_name.text = job.name
            itemView.operator.text = job.siteOperator?.label
            itemView.site_number.text = job.siteNumber
            itemView.setOnClickListener { jobClick(job) }
        }
    }

}
