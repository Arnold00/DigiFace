package arnold.example.com.watchface.application;

import android.app.Application;

/**
 * Created by arnold on 08.06.2015.
 */
public class DigiFaceApplication extends Application {

    private static DigiFaceApplication instance;

    public static DigiFaceApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
