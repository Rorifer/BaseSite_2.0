package com.engineeringforyou.basesite.presentation.job.create

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.engineeringforyou.basesite.R
import com.engineeringforyou.basesite.models.Job
import com.engineeringforyou.basesite.models.Operator
import com.engineeringforyou.basesite.models.Site
import com.engineeringforyou.basesite.presentation.job.list.JobListActivity
import com.engineeringforyou.basesite.utils.FirebaseUtils
import kotlinx.android.synthetic.main.activity_job_create.*
import kotlinx.android.synthetic.main.view_progress.*

interface JobCreateView {
    fun showProgress()
    fun hideProgress()
    fun showMessage(@StringRes error: Int)
    fun setField(site: Site)
    fun setContact(contact: String)
    fun openLinkSearch(site: Site?)
}

class JobCreateActivity : AppCompatActivity(), JobCreateView {
    companion object {
        fun start(activity: Activity) {
            val intent = Intent(activity, JobCreateActivity::class.java)
            activity.startActivity(intent)
            activity.overridePendingTransition(R.anim.slide_left_in, R.anim.alpha_out)
        }
    }

    private lateinit var presenter: JobCreatePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job_create)
        presenter = JobCreatePresenterImpl(this)
        initView()
    }

    private fun initView() {
        val operators = arrayOf("Выберите оператора", Operator.MTS.label, Operator.MEGAFON.label, Operator.VIMPELCOM.label, Operator.TELE2.label)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, operators)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        site_operator_spinner.adapter = adapter
        site_operator_spinner.prompt = "Операторы"
        site_operator_spinner.setSelection(-1)

        site_link_button.setOnClickListener { presenter.clickSiteLink() }
        job_create_button.setOnClickListener { presenter.createJob(obtainJob()) }
    }

    private fun obtainJob() = Job(
            this,
            siteOperator = obtainOperator(),
            siteNumber = site_number.text.toString(),
            address = site_address.text.toString(),
            name = job_name.text.toString(),
            description = job_description.text.toString(),
            price = job_price.text.toString(),
            contact = job_contact.text.toString()
    )

    private fun obtainOperator(): Operator? {
        val selectedPosition = site_operator_spinner.selectedItemPosition
        return if (selectedPosition < 0) null else Operator.values()[selectedPosition]
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_job_create, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.menu_job_list -> {
                JobListActivity.start(this, true)
                true
            }
            R.id.menu_logout ->{
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logout() {
        FirebaseUtils.logout()
        finish()
    }

    override fun setField(site: Site) {
        site_operator_spinner.setSelection(site.operator!!.ordinal)
        site_number.setText(site.number)
        site_address.setText(site.address)
    }

    override fun setContact(contact: String) {
        job_contact.setText(contact)
    }

    override fun openLinkSearch(site: Site?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showProgress() {
        progress_bar.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        progress_bar.visibility = View.GONE
    }

    override fun showMessage(error: Int) {
        Toast.makeText(this, getString(error), Toast.LENGTH_SHORT).show()
    }

}
