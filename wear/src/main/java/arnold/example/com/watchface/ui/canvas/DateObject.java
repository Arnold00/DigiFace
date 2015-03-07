package arnold.example.com.watchface.ui.canvas;

import android.graphics.Canvas;
import android.graphics.Paint;

import arnold.example.com.watchface.ui.AbstractCanvasObject;
import arnold.example.com.watchface.utils.DigitalFaceHelper;

/**
 * Created by arnold on 28.02.2015.
 */
public class DateObject extends AbstractCanvasObject {

    private int x;
    private int y;

    public DateObject(Paint paint, int background, int accent) {
        super(paint, background, accent);
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public void onDraw(Canvas canvas) {
        String dateString = DigitalFaceHelper.DateTimeHelper.getDateFormat(time);
        updateText(dateString);
        if (y != 0) {
            y = y + (getTextHeight());
        }
        int textWidth = getTextWidth();
        int textHeight = getTextHeight();

        int x = (this.x == 0 ? canvas.getWidth() / 2 : this.x) - (textWidth / 2);
        int y = (this.y == 0 ? canvas.getHeight() / 2 : this.y) + (textHeight / 2);
        canvas.drawText(dateString, x, y, paint);
    }
}
