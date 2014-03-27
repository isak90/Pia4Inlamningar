package com.isakgustafsson.stopwatchandtimer.app;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;

/**
 * Created by Home on 14/03/14.
 */
public class TimerService extends Service {
    private static final int NOTIFICATION_ID = 1000;
    private static final long CURRENT_TIMER_VALUE = 0;
    private final LocalBinder mLocalbinder = new LocalBinder();
    public Ringtone r;
    private TimerCallback mTimerCallback;
    private Notification mNotification;
    private CountDownTimer mCountDownTimer;
    private long mMillisValue;
    private int checker = 1;
    private Uri notification;

    public TimerService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        r = RingtoneManager.getRingtone(getApplicationContext(), notification);


        return mLocalbinder;
    }

    public void startTimer(int time) {


        if (checker == 1) {
            mCountDownTimer = new CountDownTimer(time * 1000, 300) {
                public void onTick(long millis) {

                    mMillisValue = millis;
                    notifyTimerCallback();
                    startTimeInForeground();
                    //System.out.println(millis);
                }

                public void onFinish() {
                    r.play();
                    mMillisValue = 0;
                    notifyTimerCallback();
                }
            }.start();

        } else if (checker == 2) {
            mCountDownTimer = new CountDownTimer(mMillisValue, 300) {
                public void onTick(long millis) {
                    mMillisValue = millis;
                    notifyTimerCallback();
                    startTimeInForeground();

                }

                public void onFinish() {
                    r.play();
                    mMillisValue = 0;
                    notifyTimerCallback();
                }
            }.start();
        }


        startService(new Intent(this, getClass()));
    }

    private void startTimeInForeground() {


        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("Timer Noti");

        builder.setContentText(String.valueOf(mMillisValue / 1000));
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setAutoCancel(false);
        builder.setOngoing(true);
        builder.setPriority(1);

        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("sectionNumber", 1);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent intent = PendingIntent.getActivity(this, NOTIFICATION_ID, i,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(intent);

        mNotification = builder.getNotification();
        startForeground(NOTIFICATION_ID, mNotification);


    }

    public void stopTimer() {

        if (mCountDownTimer != null) {
            checker = 2;
            mCountDownTimer.cancel();
        }

    }

    public void resetTimer() {
        checker = 1;
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;

        }
    }

    public void stopAlarm() {
        r.stop();
    }

    public long getTimerValue() {

        return mMillisValue;
    }

    private void notifyTimerCallback() {
        if (mTimerCallback != null) {
            mTimerCallback.onTimerValueChanged(getTimerValue());
            // startTimeInForeground();

        }


    }

    public void setmTimerCallback(TimerCallback timercallback) {


        mTimerCallback = timercallback;
    }

    public interface TimerCallback {

        void onTimerValueChanged(long timerValue);
    }

    public class LocalBinder extends Binder {
        public TimerService getService() {
            return TimerService.this;
        }
    }


}
