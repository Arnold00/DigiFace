package arnold.example.com.watchface.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.text.format.Time;
import android.view.SurfaceHolder;

import java.util.TimeZone;

import arnold.example.com.watchface.managers.FontManager;
import arnold.example.com.watchface.ui.canvas.BatteryObject;
import arnold.example.com.watchface.ui.canvas.DateObject;
import arnold.example.com.watchface.ui.canvas.TimeObject;
import arnold.example.com.watchface.utils.DigiFaceSettings;
import arnold.example.com.watchface.utils.DigitalFaceHelper;

/**
 * Created by arnold on 22.02.2015.
 */
public class AnalogWatchFaceService extends CanvasWatchFaceService {

    private Engine engineInstance;

    public Engine getEngineInstance() {
        return engineInstance;
    }

    @Override
    public Engine onCreateEngine() {
        /* provide your watch face implementation */
        if (engineInstance == null) {
            engineInstance = new Engine();
        }
        return engineInstance;
    }

    private class Engine extends CanvasWatchFaceService.Engine {
        static final int MSG_UPDATE_TIME = 0;
        static final int INTERACTIVE_UPDATE_RATE_MS = 1000;
        private int accentColor;
        private int backgroundColor;
        private boolean mRegisteredTimeZoneReceiver;
        private boolean isMoto360;
        /* a time object */
        Time mTime;

        /* device features */
        boolean mBurnInProtection;
        boolean mLowBitAmbient;

        Paint secondPaint;
        Paint digitalPaint;
        TimeObject timeObject;
        DateObject dateObject;
        BatteryObject batteryObject;
        Paint datePaint;
        Paint batteryPaint;
        Paint circlePaint;
        //...

        /* handler to update the time once a second in interactive mode */
        final Handler mUpdateTimeHandler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                switch (message.what) {
                    case MSG_UPDATE_TIME:
                        invalidate();
                        if (shouldTimerBeRunning()) {
                            long timeMs = System.currentTimeMillis();
                            long delayMs = INTERACTIVE_UPDATE_RATE_MS
                                    - (timeMs % INTERACTIVE_UPDATE_RATE_MS);
                            mUpdateTimeHandler
                                    .sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs);
                        }
                        break;
                }
            }
        };

        private void updateTimer() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            if (shouldTimerBeRunning()) {
                mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME);
            }
        }

        private boolean shouldTimerBeRunning() {
            return isVisible() && !isInAmbientMode();
        }

        /* receiver to update the time zone */
        final BroadcastReceiver mTimeZoneReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mTime.clear(intent.getStringExtra("time-zone"));
                mTime.setToNow();
            }
        };

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

      /* configure the system UI */
            setWatchFaceStyle(new WatchFaceStyle.Builder(AnalogWatchFaceService.this)
                    .setCardPeekMode(WatchFaceStyle.PEEK_MODE_SHORT)
                    .setBackgroundVisibility(WatchFaceStyle
                            .BACKGROUND_VISIBILITY_INTERRUPTIVE)
                    .setShowSystemUiTime(false)
                    .build());

            accentColor = DigiFaceSettings.Color.getAccentColor();
            backgroundColor = DigiFaceSettings.Color.getBackgroundColor();
    /* create graphic styles */

            //second pointer
            secondPaint = new Paint();
            secondPaint.setStrokeWidth(3.0f);
            secondPaint.setAntiAlias(true);
            secondPaint.setStrokeCap(Paint.Cap.ROUND);

            //digital clock
            digitalPaint = new Paint();
            digitalPaint.setStrokeWidth(20);
            digitalPaint.setTypeface(FontManager.getInstance().getFontTypeface(FontManager.getInstance().getCurrentFont(), AnalogWatchFaceService.this));
            digitalPaint.setTextSize(100);
            timeObject = new TimeObject(digitalPaint, backgroundColor, accentColor);

            // littlecircle
            circlePaint = new Paint();
            circlePaint.setStrokeWidth(5.0f);
            circlePaint.setAntiAlias(true);
            circlePaint.setStrokeCap(Paint.Cap.ROUND);

            //digital date
            datePaint = new Paint();
            datePaint.setStrokeWidth(20);
            datePaint.setTypeface(FontManager.getInstance().getFontTypeface(FontManager.getInstance().getCurrentFont(), AnalogWatchFaceService.this));
            datePaint.setTextSize(50);
            dateObject = new DateObject(datePaint, backgroundColor, accentColor);

            //digital date
            batteryPaint = new Paint();
            batteryPaint.setStrokeWidth(20);
            batteryPaint.setTypeface(FontManager.getInstance().getFontTypeface(FontManager.getInstance().getCurrentFont(), AnalogWatchFaceService.this));
            batteryPaint.setTextSize(50);
            batteryObject = new BatteryObject(getApplicationContext(), batteryPaint, backgroundColor, accentColor);

            updateColors();
    /* allocate an object to hold the time */
            mTime = new Time();
            isMoto360 = DigitalFaceHelper.OSHelper.isMoto360();
        }

    /* service methods (see other sections) */
        //...

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);

            if (visible) {
                registerReceiver();

                // Update time zone in case it changed while we weren't visible.
                mTime.clear(TimeZone.getDefault().getID());
                mTime.setToNow();
            } else {
                unregisterReceiver();
            }

            // Whether the timer should be running depends on whether we're visible and
            // whether we're in ambient mode), so we may need to start or stop the timer
            updateTimer();
        }

        private void registerReceiver() {

            if (mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = true;
            IntentFilter filter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
            AnalogWatchFaceService.this.registerReceiver(mTimeZoneReceiver, filter);
        }

        private void unregisterReceiver() {
            if (!mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = false;
            AnalogWatchFaceService.this.unregisterReceiver(mTimeZoneReceiver);
        }

        @Override
        public void onPropertiesChanged(Bundle properties) {
            super.onPropertiesChanged(properties);
            mLowBitAmbient = properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false);
            mBurnInProtection = properties.getBoolean(PROPERTY_BURN_IN_PROTECTION, false);
        }

        @Override
        public void onTimeTick() {
            super.onTimeTick();

            invalidate();
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {

            super.onAmbientModeChanged(inAmbientMode);

            if (mLowBitAmbient) {
                boolean antiAlias = !inAmbientMode;
                secondPaint.setAntiAlias(antiAlias);
            }
            invalidate();
            updateTimer();
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            // Update the time
            mTime.setToNow();
            //clear
            canvas.drawColor(backgroundColor);
            drawSecondTicker(canvas);
            int centerX = canvas.getWidth() / 2;
            int centerY = getCanvasHeight(canvas) / 2;
            timeObject.updateTime(mTime);
            timeObject.onDraw(canvas);

            if (!isInAmbientMode()) {
                int timeHeight = timeObject.getTextHeight();

                dateObject.updateTime(mTime);
                dateObject.setY(centerY + (timeHeight / 2));
                dateObject.onDraw(canvas);

                batteryObject.updateTime(mTime);
                batteryObject.setY(centerY - (timeHeight / 2));
                batteryObject.onDraw(canvas);
            }

            //drawTime(canvas);
            //drawDate(canvas);
            if (isColorUpdateNeeded()) {
                updateColors();
            }

        }

        private int getCanvasHeight(Canvas canvas) {
            return isMoto360 ? canvas.getHeight() + 30 : canvas.getHeight();
        }

        private void drawSecondTicker(Canvas canvas) {
            float centerX = canvas.getWidth() / 2f;
            float centerY;
            if (isMoto360) {
                centerY = (canvas.getHeight() + 30) / 2f;
            } else {
                centerY = canvas.getHeight() / 2f;
            }
//            // Compute rotations and lengths for the clock hands.
            float secRot = mTime.second / 30f * (float) Math.PI;
            float secLength = centerX * (float) Math.sqrt(2d);
            float minX = (float) Math.sin(secRot) * secLength;
            float minY = (float) -Math.cos(secRot) * secLength;

            canvas.drawLine(centerX, centerY, centerX + minX, centerY + minY,
                    secondPaint);

            canvas.drawCircle(centerX, centerY, centerX - (centerX / 5), circlePaint);
        }

        private void updateColors() {
            backgroundColor = DigiFaceSettings.Color.getBackgroundColor();
            accentColor = DigiFaceSettings.Color.getAccentColor();
            secondPaint.setColor(accentColor);
            digitalPaint.setColor(accentColor);
            circlePaint.setColor(backgroundColor);
            datePaint.setColor(accentColor);
            batteryPaint.setColor(accentColor);
        }

        private boolean isColorUpdateNeeded() {
            return (backgroundColor != DigiFaceSettings.Color.getBackgroundColor()) || (accentColor != DigiFaceSettings.Color.getAccentColor());
        }

    }


}
