package com.example.rio.icontools.icontools.model;

import android.app.Application;
import com.facebook.stetho.Stetho;

/**
 * Created by rio on 17-7-4.
 */

public class IconApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(
                                Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(
                                Stetho.defaultInspectorModulesProvider(this))
                        .build());
    }
}
