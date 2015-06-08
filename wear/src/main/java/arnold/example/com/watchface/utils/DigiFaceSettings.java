package arnold.example.com.watchface.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

import arnold.example.com.watchface.application.DigiFaceApplication;

import static arnold.example.com.watchface.utils.DigiFaceSettings.Color.*;

/**
 * Created by arnold on 08.06.2015.
 */
public class DigiFaceSettings {
    private static final String DIGI_FACE_SETTINGS = "digi_face_settings";


    private static void saveToSharedPreferences(String key, Object value) {
        SharedPreferences.Editor editor = getDigiFacePreferences().edit();
        if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof String) {
            editor.putString(key, (String) value);
        }
        editor.apply();
    }

    private static SharedPreferences getDigiFacePreferences() {
        return DigiFaceApplication.getInstance().getApplicationContext().getSharedPreferences(DIGI_FACE_SETTINGS, Context.MODE_PRIVATE);
    }

    public static class Color {
        private static final String BACKGROUND_COLOR = "bakcground_color";
        private static final String ACCENT_COLOR = "accent_color";

        public static void setBackgroundColor(int color) {
            saveToSharedPreferences(BACKGROUND_COLOR, (Integer) color);
        }

        public static int getBackgroundColor() {
            return getDigiFacePreferences().getInt(BACKGROUND_COLOR, android.graphics.Color.BLACK);
        }

        public static void setAccentColorColor(int color) {
            saveToSharedPreferences(ACCENT_COLOR, (Integer) color);
        }

        public static int getAccentColor() {
            return getDigiFacePreferences().getInt(ACCENT_COLOR, android.graphics.Color.RED);
        }
    }
}
