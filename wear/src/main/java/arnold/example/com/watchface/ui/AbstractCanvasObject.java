package arnold.example.com.watchface.ui;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.format.Time;

import arnold.example.com.watchface.utils.DigitalFaceHelper;

/**
 * Created by arnold on 28.02.2015.
 */
public abstract class AbstractCanvasObject {
    private int MOTO_360_EXTRA_Y_VALUE = 30;
    protected Paint paint;
    private Paint pointPaint;
    protected int background;
    protected int accent;
    protected Time time;
    protected String text;
    protected boolean isMoto360;

    public AbstractCanvasObject(Paint paint, int background, int accent) {
        this.paint = paint;
        this.background = background;
        this.accent = accent;
        pointPaint = new Paint();
        pointPaint.setColor(accent);
        pointPaint.setStrokeWidth(5.0f);
        pointPaint.setAntiAlias(true);
        pointPaint.setStrokeCap(Paint.Cap.ROUND);
        isMoto360 = DigitalFaceHelper.OSHelper.isMoto360();
    }

    public void updateTime(Time time) {
        this.time = time;
    }

    protected void updateText(String text) {
        this.text = text;
    }

    public int getTextWidth() {
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        int width = bounds.left + bounds.width();
        return width;
    }

    public int getTextHeight() {
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        int height = bounds.bottom + bounds.height();
        return height;
    }

    public abstract void onDraw(Canvas canvas);

    protected int getCanvasHeight(Canvas canvas) {
        return isMoto360 ? canvas.getHeight() + MOTO_360_EXTRA_Y_VALUE : canvas.getHeight();
    }

    protected int getCanvasWidth(Canvas canvas) {
        return canvas.getWidth();
    }

    protected void drawPoint(int x, int y, Canvas canvas) {
        canvas.drawCircle(x, y, 3, pointPaint);
    }
}
