package com.isakgustafsson.stopwatchandtimer.app;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Home on 14/03/14.
 */
public class StopWatchService extends Service implements Handler.Callback {

    private static final int UPDATE_STOPWATCH_VALUE = 1001;
    private static final long FIVEHUNDRED_MILLIS = 200;
    private static final int NOTIFICATION_ID = 1002;
    private final LocalBinder mLocalbinder = new LocalBinder();
    private StopWatchCallback mStopWatchCallback;
    private Handler mstopwatchHandler;
    private boolean mStopwatchIsRunning;
    private int checker = 0;
    private long mStartTimeStopwatch;
    private long mLastTime;
    private long elapsed;
    private Notification mNotification;

    public StopWatchService() {
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == UPDATE_STOPWATCH_VALUE) {

            updateStopwatchValue();

            if (mStopwatchIsRunning) {
                mstopwatchHandler.sendEmptyMessageDelayed(UPDATE_STOPWATCH_VALUE, FIVEHUNDRED_MILLIS);
            }
        }
        return false;
    }

    @Override
    public IBinder onBind(Intent intent) {

        mstopwatchHandler = new Handler(getMainLooper(), this);
        return mLocalbinder;
    }

    public void updateStopwatchValue() {
        // System.out.println("Inne i updateStopWatchValue");
        elapsed = SystemClock.elapsedRealtime() - mStartTimeStopwatch;
        createNotification();
        notifyTimerCallback();


    }

    public void startStopWatch() {
        // System.out.println("Inne i startStopwatch");

        mStopwatchIsRunning = true;
        if (checker == 0) {
            checker = 1;
            mStartTimeStopwatch = SystemClock.elapsedRealtime();

        } else {
            mStartTimeStopwatch = SystemClock.elapsedRealtime() - mLastTime;
        }
        updateStopwatchValue();

        mstopwatchHandler.sendEmptyMessageDelayed(UPDATE_STOPWATCH_VALUE, FIVEHUNDRED_MILLIS);
        startService(new Intent(this, getClass()));

    }

    public void pauseStopWatch() {
        // System.out.println("Inne i pauseStopWatch");

        mStopwatchIsRunning = false;
        mLastTime = SystemClock.elapsedRealtime() - mStartTimeStopwatch;

    }

    public void resetStopWatch() {
        //  System.out.println("Inne i resetStopWatch");

        checker = 0;
        mStartTimeStopwatch = SystemClock.elapsedRealtime();
        updateStopwatchValue();

    }

    private void createNotification() {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss:SSS", Locale.ENGLISH);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        String notificationValue = simpleDateFormat.format(new Date(elapsed));


        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("Stopwatch Noti");

        builder.setContentText(String.valueOf(notificationValue));
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setAutoCancel(false);
        builder.setOngoing(true);
        builder.setPriority(2);

        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("sectionNumber", 0);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent intent = PendingIntent.getActivity(this, NOTIFICATION_ID, i,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(intent);

        mNotification = builder.getNotification();
        startForeground(NOTIFICATION_ID, mNotification);


    }

    public long getTimerValue() {

        return elapsed; // TODO Returnera befintliga värdet i stopWatchen
    }

    // Skickar ut värdet till vår MainActivity så vi kan ändra i UI..uppdateras som ett tick typ.
    private void notifyTimerCallback() {

        if (mStopWatchCallback != null) {
            mStopWatchCallback.onStopWatchValueChanged(getTimerValue());
            //  createNotification(); funkar här

        }
    }

    public void setmStopWatchCallback(StopWatchCallback StopWatchCallback) {

        mStopWatchCallback = StopWatchCallback;
    }

    public interface StopWatchCallback {

        void onStopWatchValueChanged(long timerValue);
    }

    public class LocalBinder extends Binder {

        public StopWatchService getService() {
            return StopWatchService.this;
        }
    }


}
