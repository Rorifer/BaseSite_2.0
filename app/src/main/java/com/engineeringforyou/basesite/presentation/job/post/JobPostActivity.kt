package com.engineeringforyou.basesite.presentation.job.post

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import com.engineeringforyou.basesite.R

class JobPostActivity : AppCompatActivity() {

    companion object {
        fun start(activity: Activity) {
            val intent = Intent(activity, JobPostActivity::class.java)
            activity.startActivity(intent)
            activity.overridePendingTransition(R.anim.slide_left_in, R.anim.alpha_out)
        }
    }

}
