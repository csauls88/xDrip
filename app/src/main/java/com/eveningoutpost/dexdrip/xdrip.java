package com.eveningoutpost.dexdrip;

import android.app.Application;
import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.eveningoutpost.dexdrip.Services.PlusSyncService;
import com.eveningoutpost.dexdrip.UtilityModels.CollectionServiceStarter;
import com.eveningoutpost.dexdrip.UtilityModels.IdempotentMigrations;
import com.eveningoutpost.dexdrip.UtilityModels.PlusAsyncExecutor;

import java.util.concurrent.Executor;

import io.fabric.sdk.android.Fabric;

/**
 * Created by stephenblack on 3/21/15.
 */

public class xdrip extends Application {

    private static Context context;
    public static PlusAsyncExecutor executor;

    @Override
    public void onCreate() {
        super.onCreate();
        xdrip.context = getApplicationContext();
     try {
         if (PreferenceManager.getDefaultSharedPreferences(xdrip.context).getBoolean("enable_crashlytics", true)) {
             Crashlytics crashlyticsKit = new Crashlytics.Builder()
                     .core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
                     .build();
             Fabric.with(this, crashlyticsKit);
         }
     } catch (Exception e)
     {
         Log.e("xdrip.java", e.toString());
     }
        executor = new PlusAsyncExecutor();
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
        PreferenceManager.setDefaultValues(this, R.xml.pref_data_sync, false);
        PreferenceManager.setDefaultValues(this, R.xml.pref_notifications, false);
        PreferenceManager.setDefaultValues(this, R.xml.pref_data_source, false);
        PreferenceManager.setDefaultValues(this, R.xml.xdrip_plus_prefs, false);
        new IdempotentMigrations(getApplicationContext()).performAll();
        Home.set_is_follower();
        CollectionServiceStarter collectionServiceStarter = new CollectionServiceStarter(getApplicationContext());
        collectionServiceStarter.start(getApplicationContext());
        PlusSyncService.startSyncService(context, "xdrip.java");
    }

    public static Context getAppContext()
    {
        return xdrip.context;
    }

}
