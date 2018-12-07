package com.engineeringforyou.basesite.presentation.job

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.engineeringforyou.basesite.R
import com.engineeringforyou.basesite.authorization.PhoneAuthActivity
import com.engineeringforyou.basesite.presentation.job.list.JobListActivity
import com.engineeringforyou.basesite.presentation.job.setting.JobSettingsPresenter
import com.engineeringforyou.basesite.presentation.job.setting.JobSettingsPresenterImpl
import com.engineeringforyou.basesite.presentation.job.setting.JobSettingsView
import com.engineeringforyou.basesite.utils.FirebaseUtils
import kotlinx.android.synthetic.main.activity_job_main.*
import kotlinx.android.synthetic.main.view_progress.*

class JobMainActivity : AppCompatActivity(), JobSettingsView {

    companion object {
        @JvmStatic
        fun start(activity: Activity) {
            val intent = Intent(activity, JobMainActivity::class.java)
            activity.startActivity(intent)
            activity.overridePendingTransition(R.anim.slide_left_in, R.anim.alpha_out)
        }
    }

    private lateinit var presenter: JobSettingsPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job_main)
        presenter = JobSettingsPresenterImpl(this, this)
        initToolbar()

        find_job.setOnClickListener { JobListActivity.start(this) }
        post_job.setOnClickListener { PhoneAuthActivity.start(this) }
        user_job_list.setOnClickListener { openAdminJobList() }
        logout.setOnClickListener { logout() }
    }

    override fun enableCheckListener() {
        notification_switch.setOnCheckedChangeListener { _, isChecked ->
            presenter.switchNotification(isChecked)
        }
    }

    override fun onResume() {
        super.onResume()
        checkAdminStatus()
    }

    private fun checkAdminStatus() {
        admin_layout.visibility = if (FirebaseUtils.getIdCurrentUser() != null) View.VISIBLE else View.GONE
    }

    private fun initToolbar() {
        val actionBar = supportActionBar
        actionBar?.setDisplayShowHomeEnabled(false)
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onStart() {
        super.onStart()
        presenter.checkState()
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

    private fun logout() {
        FirebaseUtils.logout()
        checkAdminStatus()
    }

    private fun openAdminJobList() {
        JobListActivity.start(this, true)
    }

    override fun showProgress() {
        progress_bar.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        progress_bar.visibility = View.GONE
    }

    override fun setCheckedNotificationSwitch(isChecked: Boolean) {
        notification_switch.isChecked = isChecked
    }

    override fun showError(error: Int) {
        Toast.makeText(this, getString(error), Toast.LENGTH_SHORT).show()
    }

    override fun onStop() {
        super.onStop()
        presenter.clear()
    }

}
