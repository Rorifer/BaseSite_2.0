package com.engineeringforyou.basesite;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.engineeringforyou.basesite.data.orm.ORMHelperFactory;
import com.facebook.stetho.Stetho;
import com.google.firebase.analytics.FirebaseAnalytics;

import io.fabric.sdk.android.Fabric;

public class App extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        ORMHelperFactory.setHelper(getApplicationContext());
        FirebaseAnalytics.getInstance(this);

        if (BuildConfig.DEBUG) {

//            LeakCanary.install(this);

            // Create an InitializerBuilder
            Stetho.InitializerBuilder initializerBuilder =
                    Stetho.newInitializerBuilder(this);

            // Enable Chrome DevTools
            initializerBuilder.enableWebKitInspector(
                    Stetho.defaultInspectorModulesProvider(this)
            );

            // Enable command line interface
            initializerBuilder.enableDumpapp(
                    Stetho.defaultDumperPluginsProvider(this)
            );

            // Use the InitializerBuilder to generate an Initializer
            Stetho.Initializer initializer = initializerBuilder.build();

            // Initialize Stetho with the Initializer
            Stetho.initialize(initializer);
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onTerminate() {
        ORMHelperFactory.releaseHelper();
        super.onTerminate();
    }
}