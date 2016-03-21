package com.dionlan.uaibuy;

import android.app.Application;

import com.parse.Parse;

import timber.log.Timber;

/**
 * Created by froger_mcs on 05.11.14.
 */
public class InstaMaterialApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, getString(R.string.parse_app_id), getString(R.string.parse_client_key));
        Timber.plant(new Timber.DebugTree());
    }
}
