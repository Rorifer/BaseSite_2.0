package com.engineeringforyou.basesite.presentation.sitelist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.engineeringforyou.basesite.R;
import com.engineeringforyou.basesite.models.Site;
import com.engineeringforyou.basesite.presentation.sitedetails.SiteDetailsActivity;
import com.engineeringforyou.basesite.presentation.sitelist.views.SiteListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SiteListActivity extends AppCompatActivity implements SiteListView {

    private static final String KEY_SITE_LIST = "key_site_list";

    @BindView(R.id.list_sites)
    ListView listView;

    public static void start(Activity activity, List<? extends Site> list) {
        Intent intent = new Intent(activity, SiteListActivity.class);
        intent.putParcelableArrayListExtra(KEY_SITE_LIST, (ArrayList<? extends Site>) list);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_left_in, R.anim.alpha_out);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_list);
        ButterKnife.bind(this);
        showList(getIntent().getParcelableArrayListExtra(KEY_SITE_LIST));
    }

    private void showList(List<Site> siteList) {
        String KEY_ADDRESS = "address";
        String KEY_NUMBER = "number";

        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
        HashMap<String, String> map;

        for (Site site : siteList) {
            map = new HashMap<>();
            map.put(KEY_NUMBER, site.getNumber());
            map.put(KEY_ADDRESS, site.getAddress());
            arrayList.add(map);
        }

        SimpleAdapter adapter = new SimpleAdapter(
                this, arrayList,
                android.R.layout.simple_list_item_2,
                new String[]{KEY_NUMBER, KEY_ADDRESS},
                new int[]{android.R.id.text1, android.R.id.text2});
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((adapterView, view, pos, l) -> SiteDetailsActivity.start(this, siteList.get(pos)));
    }
}