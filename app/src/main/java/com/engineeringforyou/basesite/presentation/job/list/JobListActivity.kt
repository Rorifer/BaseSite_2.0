package com.engineeringforyou.basesite.presentation.job.list

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import com.engineeringforyou.basesite.R
import com.engineeringforyou.basesite.models.Job
import com.engineeringforyou.basesite.presentation.job.create.JobCreateActivity
import com.engineeringforyou.basesite.presentation.job.details.JobDetailsActivity
import com.google.android.gms.ads.AdRequest
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

        fun start(activity: Activity, isAdminStatus: Boolean = false) {
            val intent = Intent(activity, JobListActivity::class.java)
            intent.putExtra(SHOW_USER_LIST, isAdminStatus)
            activity.startActivity(intent)
            activity.overridePendingTransition(R.anim.slide_left_in, R.anim.alpha_out)
        }
    }

    private lateinit var presenter: JobListPresenter
    private lateinit var adapter: JobListAdapter
    private var isAdminStatus: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job_list)
        isAdminStatus = intent.getBooleanExtra(SHOW_USER_LIST, false)
        if (isAdminStatus) setTitle(R.string.my_job_list)
        presenter = JobListPresenterImpl(this, this, isAdminStatus)
        initAdapter()
        initAdMob()
        initToolbar()

        swipe_layout.setOnRefreshListener { presenter.loadJobList() }
    }

    override fun onResume() {
        super.onResume()
        presenter.loadJobList()
        ad_mob_job.resume()
    }

    private fun initToolbar() {
        val actionBar = supportActionBar
        actionBar?.setDisplayShowHomeEnabled(false)
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initAdMob() {
        val adRequest = AdRequest.Builder()
                .addTestDevice(getString(R.string.admob_test_device))
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build()
        ad_mob_job.loadAd(adRequest)
    }

    private fun initAdapter() {
        adapter = JobListAdapter(::clickJob, isAdminStatus)
        job_list.layoutManager = LinearLayoutManager(this)
        job_list.adapter = adapter
    }

    private fun clickJob(job: Job) {
        if (isAdminStatus) JobCreateActivity.start(this, job)
        else JobDetailsActivity.start(this, job)
    }

    override fun showJobList(list: List<Job>) {
        adapter.showList(list)
        message_view.visibility = GONE
        job_list.visibility = VISIBLE
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

    override fun onPause() {
        ad_mob_job.pause()
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
        presenter.clear()
    }

    override fun onDestroy() {
        ad_mob_job.destroy()
        super.onDestroy()
    }

}
