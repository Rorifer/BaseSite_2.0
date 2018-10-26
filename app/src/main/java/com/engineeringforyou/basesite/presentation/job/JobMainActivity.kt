package com.engineeringforyou.basesite.presentation.job

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.engineeringforyou.basesite.R
import com.engineeringforyou.basesite.presentation.job.find.JobFindActivity
import com.engineeringforyou.basesite.presentation.job.post.JobPostActivity
import com.engineeringforyou.basesite.presentation.job.setting.JobSettingsPresenter
import com.engineeringforyou.basesite.presentation.job.setting.JobSettingsPresenterImpl
import com.engineeringforyou.basesite.presentation.job.setting.JobSettingsView
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
        presenter = JobSettingsPresenterImpl(this)

        find_job.setOnClickListener { JobFindActivity.start(this) }
        post_job.setOnClickListener { JobPostActivity.start(this) }
        notification_switch.setOnCheckedChangeListener { _, isChecked ->
            presenter.switchNotification(isChecked)
        }
    }

    override fun onStart() {
        super.onStart()
        presenter.checkState()
    }

    override fun onStop() {
        super.onStop()
        presenter.clear()
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

}
