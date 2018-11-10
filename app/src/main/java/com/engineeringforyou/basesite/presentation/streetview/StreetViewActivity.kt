package com.engineeringforyou.basesite.presentation.streetview


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.engineeringforyou.basesite.R
import com.engineeringforyou.basesite.models.Site
import com.engineeringforyou.basesite.utils.EventFactory
import com.google.android.gms.maps.StreetViewPanorama
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.StreetViewPanoramaCamera
import com.google.android.gms.maps.model.StreetViewSource

class StreetViewActivity : AppCompatActivity() {

    companion object {
        private const val POINT = "point"

        fun start(activity: Activity, site: Site) {
            EventFactory.openStreetView(site)
            val intent = Intent(activity, StreetViewActivity::class.java)
            intent.putExtra(POINT, LatLng(site.latitude!!, site.longitude!!))
            activity.startActivity(intent)
            activity.overridePendingTransition(R.anim.slide_left_in, R.anim.alpha_out)
        }
    }

    private var point: LatLng? = null

    private lateinit var streetViewPanorama: StreetViewPanorama

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_street_view)

        val streetViewPanoramaFragment = supportFragmentManager
                .findFragmentById(R.id.streetviewpanorama) as SupportStreetViewPanoramaFragment
        streetViewPanoramaFragment.getStreetViewPanoramaAsync { panorama ->
            streetViewPanorama = panorama
            if (savedInstanceState == null) {
                point = intent.getParcelableExtra(POINT)
                streetViewPanorama.setPosition(point, StreetViewSource.OUTDOOR)
                setOrientation()
            }
        }
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
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setOrientation() {
        if (streetViewPanorama.location == null) {
            Handler().postDelayed({ setOrientation() }, 750L)
            return
        }
        streetViewPanorama.animateTo(StreetViewPanoramaCamera.Builder().apply {
            zoom(zoom)
            tilt(30f)
            bearing(bearing(streetViewPanorama.location.position, point!!))
        }.build(), 300L)
    }

    private fun distance(center: LatLng, point: LatLng): Float {
        val R = 6371000
        val lat1 = Math.toRadians(center.latitude)
        val lon1 = Math.toRadians(center.longitude)
        val lat2 = Math.toRadians(point.latitude)
        val lon2 = Math.toRadians(point.longitude)

        val sin1 = Math.sin((lat1 - lat2) / 2)
        val sin2 = Math.sin((lon1 - lon2) / 2)
        return (2 * R * Math.asin(Math.sqrt(sin1 * sin1 + sin2 * sin2 * Math.cos(lat1) * Math.cos(lat2)))).toFloat()
    }

    private fun bearing(center: LatLng, point: LatLng): Float {
        val lon1 = center.longitude
        val lat1 = center.latitude
        val lon2 = point.longitude
        val lat2 = point.latitude

        val y = Math.sin(lon2 - lon1) * Math.cos(lat2)
        val x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(lon2 - lon1)
        val bearing = Math.toDegrees(Math.atan2(y, x)).toFloat()
        return if (bearing < 0) 360 + bearing else bearing
    }

}