package arnold.example.com.watchface.application;

import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.wearable.provider.WearableCalendarContract;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.view.SurfaceHolder;

import java.util.TimeZone;

import arnold.example.com.watchface.managers.FontManager;
import arnold.example.com.watchface.ui.canvas.BatteryObject;
import arnold.example.com.watchface.ui.canvas.DateObject;
import arnold.example.com.watchface.ui.canvas.TimeObject;
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
        /* a time object */
        Time mTime;

        /* device features */
        boolean mBurnInProtection;
        boolean mLowBitAmbient;

        Paint secondPaint;
        TimeObject timeObject;
        DateObject dateObject;
        BatteryObject batteryObject;
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

            accentColor = Color.RED;
            backgroundColor = Color.BLACK;
    /* create graphic styles */

            //second pointer
            secondPaint = new Paint();
            secondPaint.setColor(accentColor);
            secondPaint.setStrokeWidth(5.0f);
            secondPaint.setAntiAlias(true);
            secondPaint.setStrokeCap(Paint.Cap.ROUND);

            //digital clock
            Paint digitalPaint = new Paint();
            digitalPaint.setColor(accentColor);
            digitalPaint.setStrokeWidth(20);
            digitalPaint.setTypeface(FontManager.getInstance().getFontTypeface(FontManager.getInstance().getCurrentFont(), AnalogWatchFaceService.this));
            digitalPaint.setTextSize(100);
            timeObject = new TimeObject(digitalPaint, backgroundColor, accentColor);

            // littlecircle
            circlePaint = new Paint();
            circlePaint.setColor(backgroundColor);
            circlePaint.setStrokeWidth(5.0f);
            circlePaint.setAntiAlias(true);
            circlePaint.setStrokeCap(Paint.Cap.ROUND);

            //digital date
            Paint datePaint = new Paint();
            datePaint.setColor(accentColor);
            datePaint.setStrokeWidth(20);
            datePaint.setTypeface(FontManager.getInstance().getFontTypeface(FontManager.getInstance().getCurrentFont(), AnalogWatchFaceService.this));
            datePaint.setTextSize(50);
            dateObject = new DateObject(datePaint, backgroundColor, accentColor);

            //digital date
            Paint batteryPaint = new Paint();
            batteryPaint.setColor(accentColor);
            batteryPaint.setStrokeWidth(20);
            batteryPaint.setTypeface(FontManager.getInstance().getFontTypeface(FontManager.getInstance().getCurrentFont(), AnalogWatchFaceService.this));
            batteryPaint.setTextSize(50);
            batteryObject = new BatteryObject(getApplicationContext(), batteryPaint, backgroundColor, accentColor);


    /* allocate an object to hold the time */
            mTime = new Time();
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
            mBurnInProtection = properties.getBoolean(PROPERTY_BURN_IN_PROTECTION,
                    false);
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
            int centerX = canvas.getWidth() / 2;
            int centerY = canvas.getHeight() / 2;
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

        }

        private void drawSecondTicker(Canvas canvas) {
            float centerX = canvas.getWidth() / 2f;
            float centerY = canvas.getHeight() / 2f;

//            // Compute rotations and lengths for the clock hands.
            float secRot = mTime.second / 30f * (float) Math.PI;
            float secLength = centerX * (float) Math.sqrt(2d);
            float minX = (float) Math.sin(secRot) * secLength;
            float minY = (float) -Math.cos(secRot) * secLength;

            canvas.drawLine(centerX, centerY, centerX + minX, centerY + minY,
                    secondPaint);

            canvas.drawCircle(centerX, centerY, centerX - (centerX / 5), circlePaint);
        }

        public class CalendarAsyncTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... params) {
                long begin = System.currentTimeMillis();
                Uri.Builder builder =
                        WearableCalendarContract.Instances.CONTENT_URI.buildUpon();
                ContentUris.appendId(builder, begin);
                ContentUris.appendId(builder, begin + DateUtils.DAY_IN_MILLIS);
                final Cursor cursor = getContentResolver().query(builder.build(),
                        null, null, null, null);
                return null;
            }
        }
    }


}
