package pcloud.task;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;

public class ApplicationClass extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
}
