package com.engineeringforyou.basesite.presentation.job.details

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.engineeringforyou.basesite.R
import com.engineeringforyou.basesite.domain.job.JobInteractorImpl
import com.engineeringforyou.basesite.models.Job
import com.engineeringforyou.basesite.presentation.sitedetails.SiteDetailsActivity
import com.engineeringforyou.basesite.utils.EventFactory
import kotlinx.android.synthetic.main.activity_job_details.*

class JobDetailsActivity : AppCompatActivity() {

    companion object {
        private const val JOB = "job"

        fun start(activity: Activity, job: Job) {
            EventFactory.openJob(job)
            val intent = Intent(activity, JobDetailsActivity::class.java)
            intent.putExtra(JOB, job)
            activity.startActivity(intent)
            activity.overridePendingTransition(R.anim.slide_left_in, R.anim.alpha_out)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job_details)
        val job: Job? = intent.getParcelableExtra(JOB)
        setupFields(job)
        initToolbar()
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
            else -> false
        }
    }

    @SuppressLint("CheckResult")
    private fun setupFields(job: Job?) {
        if (job == null) return

        site_operator.text = job.siteOperator?.label
        site_number.text = setValueOrEmpty(job.siteNumber)
        site_address.text = setValueOrEmpty(job.address)
        job_name.text = setValueOrEmpty(job.name)
        job_description.text = setValueOrEmpty(job.description)
        job_price.text = setValueOrEmpty(job.price)
        job_contact.text = setValueOrEmpty(job.contact)

        site_link_button.isEnabled = false
        if (job.linkSiteOperator != null && job.linkSiteUid != null) {
            JobInteractorImpl(this).getLinkSite(job.linkSiteOperator!!, job.linkSiteUid!!)
                    .subscribe({ site ->
                        site_link_button.isEnabled = true
                        site_link_button.setOnClickListener { SiteDetailsActivity.start(this, site) }
                    }, { EventFactory.exception(it) })
        }
    }

    private fun setValueOrEmpty(value : String): String{
        return if (value.isEmpty()) "---" else value
    }
}
