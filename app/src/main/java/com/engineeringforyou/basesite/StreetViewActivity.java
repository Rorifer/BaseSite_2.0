package com.engineeringforyou.basesite;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup.LayoutParams;

import com.google.android.gms.maps.StreetViewPanoramaOptions;
import com.google.android.gms.maps.StreetViewPanoramaView;
import com.google.android.gms.maps.model.LatLng;


public class StreetViewActivity extends AppCompatActivity {

//Define the LatLng value we’ll be using for the paranorma’s initial camera position//

    private static final LatLng LONDON = new LatLng(51.503324, -0.119543);
    private StreetViewPanoramaView mStreetViewPanoramaView;
    private static final String STREETVIEW_BUNDLE_KEY = "StreetViewBundleKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//Configure the panorama by passing in a StreetViewPanoramaOptions object//

        StreetViewPanoramaOptions options = new StreetViewPanoramaOptions();
        if (savedInstanceState == null) {

//Set the panorama’s location//

            options.position(LONDON);
        }

        mStreetViewPanoramaView = new StreetViewPanoramaView(this, options);
        addContentView(mStreetViewPanoramaView,
                new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        Bundle mStreetViewBundle = null;
        if (savedInstanceState != null) {
            mStreetViewBundle = savedInstanceState.getBundle(STREETVIEW_BUNDLE_KEY);
        }
        mStreetViewPanoramaView.onCreate(mStreetViewBundle);
    }

}
