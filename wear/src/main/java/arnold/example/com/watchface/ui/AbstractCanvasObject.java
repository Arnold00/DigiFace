package arnold.example.com.watchface.ui;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.format.Time;

/**
 * Created by arnold on 28.02.2015.
 */
public abstract class AbstractCanvasObject {
    protected Paint paint;
    private Paint pointPaint;
    protected int background;
    protected int accent;
    protected Time time;
    protected String text;

    public AbstractCanvasObject(Paint paint, int background, int accent){
        this.paint = paint;
        this.background = background;
        this.accent = accent;
        pointPaint = new Paint();
        pointPaint.setColor(accent);
        pointPaint.setStrokeWidth(5.0f);
        pointPaint.setAntiAlias(true);
        pointPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    public void updateTime(Time time) {
        this.time = time;
    }

    protected void updateText(String text){
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

    protected void drawPoint(int x, int y, Canvas canvas) {
        canvas.drawCircle(x,y,3, pointPaint);
    }
}
