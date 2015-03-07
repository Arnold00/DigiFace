package arnold.example.com.watchface.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.text.format.Time;

import arnold.example.com.watchface.application.AnalogWatchFaceService;

/**
 * Created by arnold on 23.02.2015.
 */
public class DigitalFaceHelper {

    public static class DateTimeHelper {
        private static final String TIME_FORMAT_24H="kk:mm";
        private static final String DATE_FORMAT = "EEE. dd";


        private static SimpleDateFormat get24HDateFormatter(){
            return new SimpleDateFormat(TIME_FORMAT_24H);
        }

        private static SimpleDateFormat getDateFormat(){
            return new SimpleDateFormat(DATE_FORMAT);
        }

        public static String get24HFormat(Time time) {
            SimpleDateFormat formatter = get24HDateFormatter();
            return formatter.format(new Date(time.toMillis(false)));
        }

        public static String getDateFormat(Time time) {
            SimpleDateFormat formatter = getDateFormat();
            return formatter.format(new Date(time.toMillis(false)));
        }

    }

    public  static class Battery {

        public static int getBatteryPercentage(Context context){
            Intent batteryIntent = context.getApplicationContext().registerReceiver(null,
                    new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

            float batteryPct = level / (float)scale;
            int finalValue = (int) (batteryPct * 100);
            System.out.println("TEST " + finalValue);
            return  finalValue;
        }
    }
}
