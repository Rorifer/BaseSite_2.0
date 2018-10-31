package com.engineeringforyou.basesite.presentation.job.list

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View.GONE
import android.view.View.VISIBLE
import com.engineeringforyou.basesite.R
import com.engineeringforyou.basesite.models.Job
import com.engineeringforyou.basesite.presentation.job.JobMainActivity
import com.engineeringforyou.basesite.presentation.job.details.JobDetailsActivity
import kotlinx.android.synthetic.main.activity_job_list.*

interface JobListView {
    fun showRefresh()
    fun hideRefresh()
    fun showJobList(list: List<Job>)
    fun showMessage(@StringRes message: Int)
}

class JobListActivity : AppCompatActivity(), JobListView {

    companion object {
        const val SHOW_USER_LIST = "only_user_list"

        fun start(activity: Activity, onlyUserList: Boolean = false) {
            val intent = Intent(activity, JobMainActivity::class.java)
            intent.putExtra(SHOW_USER_LIST, onlyUserList)
            activity.startActivity(intent)
            activity.overridePendingTransition(R.anim.slide_left_in, R.anim.alpha_out)
        }
    }

    private lateinit var presenter: JobListPresenter
    private lateinit var adapter: JobListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job_list)
        val onlyUserList = intent.getBooleanExtra(SHOW_USER_LIST, false)
        presenter = JobListPresenterImpl(this, this, onlyUserList)
        initAdapter()

        swipe_layout.setOnRefreshListener { presenter.loadJobList() }
    }

    private fun initAdapter() {
        adapter = JobListAdapter(::clickJob)
        job_list.layoutManager = LinearLayoutManager(this)
        job_list.adapter = adapter
    }

    private fun clickJob(job: Job) {
        JobDetailsActivity.start(this, job)
    }

    override fun showJobList(list: List<Job>) {
        message_view.visibility = GONE
        job_list.visibility = VISIBLE
        adapter.showList(list)
    }

    override fun showMessage(@StringRes message: Int) {
        job_list.visibility = GONE
        message_view.visibility = VISIBLE
        message_view.setText(message)
    }

    override fun showRefresh() {
        swipe_layout.isRefreshing = true
    }

    override fun hideRefresh() {
        swipe_layout.isRefreshing = false
    }

    override fun onStop() {
        super.onStop()
        presenter.clear()
    }

}
