package com.engineeringforyou.basesite.presentation.job.create

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.engineeringforyou.basesite.R
import com.engineeringforyou.basesite.models.Job
import com.engineeringforyou.basesite.models.Operator
import com.engineeringforyou.basesite.models.Site
import com.engineeringforyou.basesite.presentation.mapcoordinates.MapCoordinatesActivity
import com.engineeringforyou.basesite.presentation.mapcoordinates.MapCoordinatesActivity.*
import com.engineeringforyou.basesite.utils.KeyBoardUtils
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_job_create.*
import kotlinx.android.synthetic.main.view_progress.*

interface JobCreateView {
    fun showProgress()
    fun hideProgress()
    fun showMessage(@StringRes error: Int)
    fun setFieldLinkSite(site: Site)
    fun setContact(contact: String)
    fun openLinkSearch(site: Site?)
    fun setLinkBS(site: Site)
    fun showListLinkSearch(list: List<Site>)
    fun close()
    fun openMap(site: Site?)
    fun hideMapButton()
    fun setAddressFromCoordinates(address: String)
}

class JobCreateActivity : AppCompatActivity(), JobCreateView {

    companion object {
        private const val JOB_FOR_EDIT = "job_for_edit"

        fun start(activity: Activity, job: Job? = null) {
            val intent = Intent(activity, JobCreateActivity::class.java)
            intent.putExtra(JOB_FOR_EDIT, job)
            activity.startActivity(intent)
            activity.overridePendingTransition(R.anim.slide_left_in, R.anim.alpha_out)
        }
    }

    private lateinit var presenter: JobCreatePresenter
    private var job: Job? = null
    private var addressLoaded: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job_create)
        presenter = JobCreatePresenterImpl(this, this)
        job = intent.getParcelableExtra(JOB_FOR_EDIT)
        initView()
        initToolbar()
    }

    private fun initToolbar() {
        val actionBar = supportActionBar
        actionBar?.setDisplayShowHomeEnabled(false)
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initView() {
        val operators = arrayOf("Выберите оператора", Operator.MTS.label, Operator.MEGAFON.label, Operator.VIMPELCOM.label, Operator.TELE2.label)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, operators)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        site_operator_spinner.adapter = adapter
        site_operator_spinner.prompt = "Операторы"
        site_operator_spinner.setSelection(-1)

        if (job != null) {
            presenter.setLinkSite(job!!.linkSiteOperator, job!!.linkSiteUid)

            val operatorIndex = if (job!!.siteOperator == null) -1 else job!!.siteOperator!!.ordinal + 1
            site_operator_spinner.setSelection(operatorIndex)
            site_number.setText(job!!.siteNumber)
            site_address.setText(job!!.address)
            job_name.setText(job!!.name)
            job_description.setText(job!!.description)
            job_price.setText(job!!.price)
            job_contact.setText(job!!.contact)
            job_create_button.setText(R.string.edit)

            job_close_button.visibility = View.VISIBLE
            if (job!!.public.not()) job_close_button.setText(R.string.job_public)
            job_close_button.setOnClickListener {
                KeyBoardUtils.hideKeyboard(this, currentFocus)
                if (job!!.public) presenter.closeJob(job!!.id)
                else presenter.publicJob(job!!.id)
            }
        }

        job_create_button.setOnClickListener {
            KeyBoardUtils.hideKeyboard(this, currentFocus)
            if (job == null) presenter.createJob(obtainJob())
            else presenter.editJob(obtainJob().setupForEdit(job!!.id))
        }

        site_link_button.setOnClickListener { presenter.clickSiteLink() }
        coordinates_button.setOnClickListener { presenter.callMap() }
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
        return if (selectedPosition < 1) null else Operator.values()[selectedPosition - 1]
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun openMap(site: Site?) {
        MapCoordinatesActivity.startForResult(this, site?.latitude, site?.longitude, null)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        if (requestCode == CODE_MAP) {
            if (resultCode == Activity.RESULT_OK) {
                val lat = intent!!.getDoubleExtra(LATITUDE, 0.0)
                val lng = intent.getDoubleExtra(LONGITUDE, 0.0)
                if (lat != 0.0 && lng != 0.0) {
                    presenter.setCoordinates(LatLng(lat, lng))
                }
            }
        }
    }

    override fun hideMapButton() {
        coordinates_button.visibility = View.GONE
    }

    override fun setAddressFromCoordinates(address: String) {
        val currentAddress = site_address.text.toString()
        if (currentAddress.isEmpty() || addressLoaded != null && addressLoaded == currentAddress) {
            site_address.setText(address)
            addressLoaded = address
        }
    }

    override fun setFieldLinkSite(site: Site) {
        site_operator_spinner.setSelection(site.operator!!.ordinal + 1)
        site_number.setText(site.number)
        site_address.setText(site.address)
    }

    override fun setContact(contact: String) {
        job_contact.setText(contact)
    }

    override fun openLinkSearch(site: Site?) {
        SiteLinkDialog.getInstance(site).show(fragmentManager, "dialog_link")
    }

    override fun showListLinkSearch(list: List<Site>) {
        SiteLinkListDialog.getInstance(list).show(fragmentManager, "dialog_list_link")
    }

    override fun setLinkBS(site: Site) = presenter.setLinkSite(site)

    override fun showProgress() {
        progress_bar.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        progress_bar.visibility = View.GONE
    }

    override fun showMessage(error: Int) {
        Toast.makeText(this, getString(error), Toast.LENGTH_SHORT).show()
    }

    override fun close() = finish()

}
