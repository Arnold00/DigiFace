package arnold.example.com.watchface.managers;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by arnold on 23.02.2015.
 */
public class FontManager {

    private static FontManager instance;
    private Font currentFont;

    private FontManager() {
        currentFont = Font.DIGITAL;
    }

    public static FontManager getInstance() {
        if (instance == null) {
            instance = new FontManager();
        }
        return instance;
    }

    public Typeface getFontTypeface(Font font, Context context) {
        return Typeface.createFromAsset(context.getAssets(), font.getPath());
    }

    public Font getCurrentFont() {
        return currentFont;
    }

    public enum Font {

        DIGITAL("Digital", "fonts/digital_7.ttf");

        private String name;
        private String path;

        Font(String name, String path) {
            this.name = name;
            this.path = path;
        }

        public String getName() {
            return name;
        }

        public String getPath() {
            return path;
        }
    }
}
