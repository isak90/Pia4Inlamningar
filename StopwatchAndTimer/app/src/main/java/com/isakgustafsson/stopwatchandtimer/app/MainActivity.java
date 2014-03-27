package com.isakgustafsson.stopwatchandtimer.app;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


public class MainActivity extends ActionBarActivity implements
        TimerService.TimerCallback,
        StopWatchService.StopWatchCallback {

    public static final int TIMER_POSITION = 1;
    public static final int STOPWATCH_POSITION = 0;
    public CountDownTimer autoStop;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;
    /**
     * The {@link ViewPager} that will host the section contents.
     */

    ViewPager mViewPager;
    private TimerService timerService;
    private StopWatchService mStopWatchService;
    private TimerServiceConnection mTimerServiceConnection;
    private StopwatchServiceConnection mStopWatchServiceConnection;
    private CharSequence timeForStopwatch;
    private long valueToTable;
    private timerFragment mTimerFragment;
    private stopWatchFragment mStopwatchFragment;
    private long currentLap = 0;
    private int sectionNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStopwatchFragment = new stopWatchFragment();
        mTimerFragment = new timerFragment();
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);


    }

    @Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        Intent setIntent = new Intent(Intent.ACTION_MAIN);
        setIntent.addCategory(Intent.CATEGORY_HOME);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        sectionNumber = intent.getIntExtra("sectionNumber", 0);
    }


    @Override
    protected void onResume() {
        super.onResume();
        mViewPager.setCurrentItem(sectionNumber);
        mTimerServiceConnection = new TimerServiceConnection();
        bindService(new Intent(this, TimerService.class), mTimerServiceConnection, BIND_AUTO_CREATE);

        mStopWatchServiceConnection = new StopwatchServiceConnection();
        bindService(new Intent(this, StopWatchService.class), mStopWatchServiceConnection, BIND_AUTO_CREATE);


    }

    @Override
    protected void onPause() {
        super.onPause();

        timerService.setmTimerCallback(null);
        unbindService(mTimerServiceConnection);

        mStopWatchService.setmStopWatchCallback(null);
        unbindService(mStopWatchServiceConnection);

    }

    // TIMER METHORDERNA
    @Override
    public void onTimerValueChanged(long timerValue) {


        // System.out.println("Timer value i MainActivity" + timerValue);
        TextView timerText = (TextView) findViewById(R.id.timerTextView);
        timerText.setText("" + (String.valueOf(timerValue / 1000)));
        if (timerValue == 0) {
            alarmAutoStop();
            timerText.setText(getString(R.string.doneText));
        }
     /*   if (mTimerServiceConnection != null && timerService.r.isPlaying()) {
            Toast.makeText(getApplicationContext(), "Your toast message.",
                    Toast.LENGTH_SHORT).show();
            Button cancelAlarm = (Button) findViewById(R.id.cancel_alarm_button);
            cancelAlarm.setVisibility(View.VISIBLE);



        }
*/
    }

    public void startTimer(View view) {
        EditText ownTime = (EditText) findViewById(R.id.ownTime);
        String ownTimeText = ownTime.getText().toString();
        if (!ownTimeText.isEmpty()) {
            int time = Integer.parseInt(ownTime.getText().toString());
            timerService.startTimer(time);
        }
    }

    public void stopTimer(View view) {
        timerService.stopTimer();

    }

    public void resetTimer(View view) {
        TextView timerView = (TextView) findViewById(R.id.timerTextView);
        timerView.setText(getString(R.string.timerReset));
        timerService.resetTimer();
    }

    public void startStopwatch(View view) {
        Button buttonText = (Button) findViewById(R.id.reset_stopwatch_button);
        buttonText.setText("Lap");
        mStopWatchService.startStopWatch();
    }

    public void stopStopwatch(View view) {

        mStopWatchService.pauseStopWatch();
        Button buttonText = (Button) findViewById(R.id.reset_stopwatch_button);
        buttonText.setText("Reset");


    }

    // STOPWATCH METHODERNA

    public void resetStopwatch(View view) {

        Button buttonText = (Button) findViewById(R.id.reset_stopwatch_button);
        String buttonString = new String(buttonText.getText().toString());

        if (buttonString.equals("Lap")) {
            updateTableView();


        } else if (buttonString.equals("Reset")) {
            mStopwatchFragment.resetLap();
            currentLap = 0;
            mStopWatchService.resetStopWatch();
        }
    }

    private void updateTableView() {
        currentLap++;
        mStopwatchFragment.addLap(new LapList(currentLap, "Lapnumber:" + currentLap, "Laptime: " + timeForStopwatch));


        //Todo Fixa så att valuetoTable skickas in i table.
    }

    @Override
    public void onStopWatchValueChanged(long timerValue) {
        TextView stopwatchValue = (TextView) findViewById(R.id.stopwatch_value);
        valueToTable = timerValue;
        // timerValue = SystemClock.elapsedRealtime() - mStartTimeStopwatch;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss:SSS", Locale.ENGLISH);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        timeForStopwatch = simpleDateFormat.format(new Date(timerValue));
        stopwatchValue.setText(timeForStopwatch);
        //stopwatchValue.setText(String.valueOf(timerValue));
        //System.out.println("Stopwatchen " + timerValue);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent mActivity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void alarmAutoStop() {

        timerService.r.play();
        Button cancelAlarm = (Button) findViewById(R.id.cancel_alarm_button);
        cancelAlarm.setVisibility(View.VISIBLE);


        autoStop = new CountDownTimer(30000, 500) {

            public void onTick(long millis) {

            }

            public void onFinish() {
                timerService.stopAlarm();
            }

        }.start();

    }

    public void stopAlarm(View view) {
        view.setVisibility(View.INVISIBLE);
        timerService.stopAlarm();

    }

    public static class timerFragment extends Fragment {


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


            View rootView = inflater.inflate(R.layout.timer_layout, container, false);
            return rootView;


        }


    }

    public static class stopWatchFragment extends Fragment {
        public List<LapList> mLapnumber;
        public LapListAdapter mLapAdapter;

        public stopWatchFragment() {
            mLapnumber = new LinkedList<LapList>();


        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {


            View rootView = inflater.inflate(R.layout.stopwatch_layout, container, false);

            ListView listView = (ListView) rootView.findViewById(R.id.lapListView);
            listView.setAdapter(mLapAdapter);


            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            mLapAdapter = new LapListAdapter(activity, mLapnumber);
        }

        public void addLap(LapList laps) {
            mLapnumber.add(laps);
            mLapAdapter.notifyDataSetChanged();
        }

        public void resetLap() {
            mLapnumber.clear();
            mLapAdapter.notifyDataSetChanged();
        }

    }

    public static class LapListAdapter extends BaseAdapter {


        public List<LapList> mLapnumber;
        private Activity mActivity;

        public LapListAdapter(Activity mActivity, List<LapList> mLapnumber) {
            this.mActivity = mActivity;

            this.mLapnumber = mLapnumber;


        }

        @Override
        public int getCount() {
            return mLapnumber.size();
        }

        @Override
        public Object getItem(int position) {
            return mLapnumber.get(position);
        }

        @Override
        public long getItemId(int position) {
            return mLapnumber.get(position).getmId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = View.inflate(mActivity, R.layout.lap_list_layout, null);
            }

            LapList laps = mLapnumber.get(position);
            ((TextView) convertView.findViewById(R.id.lap_number)).setText(laps.getmLapNumber());

            ((TextView) convertView.findViewById(R.id.lap_time)).setText(laps.getmLapTime());


            return convertView;
        }
    }

    class StopwatchServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mStopWatchService = ((StopWatchService.LocalBinder) service).getService();
            mStopWatchService.setmStopWatchCallback(MainActivity.this);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

            mStopWatchService = null;

        }
    }

    class TimerServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            timerService = ((TimerService.LocalBinder) service).getService();
            timerService.setmTimerCallback(MainActivity.this);

            if (timerService.r.isPlaying()) {
                Button cancelAlarm = (Button) findViewById(R.id.cancel_alarm_button);
                cancelAlarm.setVisibility(View.VISIBLE);
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            timerService = null;

        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            switch (position) {
                case TIMER_POSITION:
                    return mTimerFragment;
                case STOPWATCH_POSITION:
                    return mStopwatchFragment;
                default:
                    return null;
            }


        }

        @Override
        public int getCount() {

            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case STOPWATCH_POSITION:

                    return getString(R.string.title_section_stopwatch).toUpperCase(l);

                case TIMER_POSITION:

                    return getString(R.string.title_section_timer).toUpperCase(l);

            }
            return null;
        }


    }

}
