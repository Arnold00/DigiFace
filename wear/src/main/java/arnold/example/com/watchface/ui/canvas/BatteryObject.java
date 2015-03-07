package arnold.example.com.watchface.ui.canvas;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import arnold.example.com.watchface.ui.AbstractCanvasObject;
import arnold.example.com.watchface.utils.DigitalFaceHelper;

/**
 * Created by arnold on 28.02.2015.
 */
public class BatteryObject extends AbstractCanvasObject {

    private int x;
    private int y;
    private Context context;
    private int percentage;
    private int lastUpdatedMinute;

    public BatteryObject(Context context, Paint paint, int background, int accent) {
        super(paint, background, accent);
        this.context = context;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    private void updatePercentage(){
        this.percentage = DigitalFaceHelper.Battery.getBatteryPercentage(context);
        System.out.println("TEST updated battery");
    }
    @Override
    public void onDraw(Canvas canvas) {
        String batteryString = "Bat. " + percentage + "%";
        updateText(batteryString);
        if (y != 0) {
            y = y - (getTextHeight());
        }
        int textWidth = getTextWidth();
        int textHeight = getTextHeight();

        int x = (this.x == 0 ? canvas.getWidth() / 2 : this.x) - (textWidth / 2);
        int y = (this.y == 0 ? canvas.getHeight() / 2 : this.y) + (textHeight / 2);
        canvas.drawText(batteryString, x, y, paint);
        if (time.minute!= lastUpdatedMinute) {
            updatePercentage();
            this.lastUpdatedMinute = time.minute;
        }
    }
}
