package arnold.example.com.watchface.ui.canvas;

import android.graphics.Canvas;
import android.graphics.Paint;

import arnold.example.com.watchface.ui.AbstractCanvasObject;
import arnold.example.com.watchface.utils.DigitalFaceHelper;

/**
 * Created by arnold on 28.02.2015.
 */
public class TimeObject extends AbstractCanvasObject {

    public TimeObject(Paint paint, int background, int accent) {
        super(paint, background, accent);
    }

    @Override
    public void onDraw(Canvas canvas) {
        String timeString = DigitalFaceHelper.DateTimeHelper.get24HFormat(time);
        updateText(timeString);
        int textWidth = getTextWidth();
        int textHeight = getTextHeight();
        int x = (getCanvasWidth(canvas) / 2) - (textWidth / 2);
        int y = (getCanvasHeight(canvas) / 2) + (textHeight / 2);

        canvas.drawText(timeString, x, y, paint);
    }


}
