package com.engineeringforyou.basesite.presentation.job.create

import android.app.DialogFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SimpleAdapter
import com.engineeringforyou.basesite.R
import com.engineeringforyou.basesite.models.Site
import kotlinx.android.synthetic.main.activity_site_list.view.*
import java.util.*

class SiteLinkListDialog : DialogFragment() {

    companion object {

        fun getInstance(siteList: List<Site>): SiteLinkListDialog {
            val fragment = SiteLinkListDialog()
            val args = Bundle()
            args.putParcelableArrayList("siteList", siteList as ArrayList<Site>)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.activity_site_list, container)
        dialog.setTitle(R.string.dialog_site_list_link_title)

        val siteList = arguments.getParcelableArrayList<Site>("siteList")
        if (siteList != null) showList(v, siteList)

        return v
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    private fun showList(view: View, siteList: List<Site>) {
        val KEY_ADDRESS = "address"
        val KEY_NUMBER = "number"

        val arrayList = ArrayList<HashMap<String, String>>()
        var map: HashMap<String, String>

        for (site in siteList) {
            map = HashMap()
            map[KEY_NUMBER] = site.number!!
            map[KEY_ADDRESS] = site.address
            arrayList.add(map)
        }

        val adapter = SimpleAdapter(
                activity,
                arrayList,
                android.R.layout.simple_list_item_2,
                arrayOf(KEY_NUMBER, KEY_ADDRESS),
                intArrayOf(android.R.id.text1, android.R.id.text2))
        view.list_sites.adapter = adapter
        view.list_sites.setOnItemClickListener { _, _, pos, _ ->
            (activity as JobCreateView).setLinkBS(siteList[pos])
            dismiss()
        }
    }
}
