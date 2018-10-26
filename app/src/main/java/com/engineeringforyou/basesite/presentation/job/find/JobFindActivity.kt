package com.engineeringforyou.basesite.presentation.job.find

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
import kotlinx.android.synthetic.main.activity_job_find.*

interface JobFindView {
    fun showRefresh()
    fun hideRefresh()
    fun showJobList(list: List<Job>)
    fun showMessage(@StringRes message: Int)
}

class JobFindActivity : AppCompatActivity(), JobFindView {

    companion object {
        fun start(activity: Activity) {
            val intent = Intent(activity, JobMainActivity::class.java)
            activity.startActivity(intent)
            activity.overridePendingTransition(R.anim.slide_left_in, R.anim.alpha_out)
        }
    }

    private lateinit var presenter: JobFindPresenter
    private lateinit var adapter: JobAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job_find)
        presenter = JobFindPresenterImpl(this)
        initAdapter()

        swipe_layout.setOnRefreshListener { presenter.loadJobList() }
    }

    private fun initAdapter() {
        adapter = JobAdapter()
        job_list.layoutManager = LinearLayoutManager(this)
        job_list.adapter = adapter
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
