package com.isakgustafsson.geofencingapp.app;

import android.app.LoaderManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity {


    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;
    private mapFragmentClass mMapsFragment;
    private geofenceListClass mMapsListFragment;
    public static final int MAPS_PAGE = 0;
    public static final int MAPS_LIST_PAGE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMapsFragment = new mapFragmentClass();
        mMapsListFragment = new geofenceListClass();
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);


    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class mapFragmentClass extends Fragment implements GoogleMap.OnMapLongClickListener, GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {
        public GoogleMap mMap;
        public LocationClient mLocationClient;
        private CircleOptions circleOptions;
        private static final float geofenceRadius = 10.0f;


        public mapFragmentClass() {


        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);


        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.map_fragment, container, false);
            // Deklararer Kartan
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            mMap.setOnMapLongClickListener(this);
            mMap.setMyLocationEnabled(true);
            mLocationClient = new LocationClient(getActivity(), this, this);
            return rootView;
        }

        @Override
        public void onStart() {
            super.onStart();
            mLocationClient.connect();
        }

        @Override
        public void onStop() {
            super.onStop();
            mLocationClient.disconnect();
        }


        @Override
        public void onMapLongClick(LatLng latLng) {
            Geofence.Builder builder = new Geofence.Builder();
            Geofence geofence = builder
                    .setRequestId("MyGeofence")
                    .setCircularRegion(latLng.latitude,
                            latLng.longitude, geofenceRadius)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER
                            | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .setExpirationDuration(1000 * 60 * 5)
                    .build();


            List<Geofence> geofenceList = new ArrayList<Geofence>();
            geofenceList.add(geofence);


            Intent showGeofenceToast =
                    new Intent(GeofenceReceiver.ACTION_GEOFENCE_TOAST);
            PendingIntent pendingIntent
                    = PendingIntent
                    .getBroadcast(getActivity(), 0, showGeofenceToast, 0);

            mLocationClient.addGeofences(geofenceList, pendingIntent, new LocationClient.OnAddGeofencesResultListener() {
                @Override
                public void onAddGeofencesResult(int i, String[] strings) {
                    // TODO Possible error handling...
                    Log.e("GeofenceDemo", "Geofences added!");
                }
            });

            circleOptions = new CircleOptions();
            circleOptions.center(latLng).radius(geofenceRadius);
            circleOptions.fillColor(0x7F00FF00);

            insertGeofenceInDatabase(latLng, geofenceRadius);

            mMap.addCircle(circleOptions);
        }

        @Override
        public void onConnected(Bundle bundle) {

        }

        @Override
        public void onDisconnected() {

        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {

        }


        private void insertGeofenceInDatabase(LatLng latLng, float radius) {
            ContentValues values = new ContentValues();
            values.put(MyGeofenceStore.Contract.LATITUDE, latLng.latitude);
            values.put(MyGeofenceStore.Contract.LONGITUDE, latLng.longitude);
            values.put(MyGeofenceStore.Contract.RADIUS, radius);
            values.put(MyGeofenceStore.Contract.CREATED, System.currentTimeMillis());

            Uri newMessage = getContentResolver().insert(MyGeofenceStore.Contract.GEOFENCES, values);
            if (newMessage == null) {

            } else {

            }


        }


    }

    public class geofenceListClass extends Fragment
            implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {

        private SimpleCursorAdapter mListAdapter;
        private LoaderManager.LoaderCallbacks mCallBack;

        public geofenceListClass() {

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.maps_list_fragment, container, false);
            ListView listView = (ListView) rootView.findViewById(R.id.geofenceList);

            mListAdapter = new SimpleCursorAdapter(getActivity(), R.layout.geolist_cell, null,
                    new String[]{
                            MyGeofenceStore.Contract.LONGITUDE,MyGeofenceStore.Contract.LATITUDE},
                    new int[]{
                            R.id.list_text_longitude,R.id.list_text_latitud}, 0);

            mListAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {

                @Override
                public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                    return false;
                }
            });

            listView.setAdapter(mListAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //Todo : Remove "id" from database
                    getContentResolver().delete(MyGeofenceStore.Contract.GEOFENCES,MyGeofenceStore.Contract.ID + "=" +id,null);
                    getContentResolver().notifyChange(MyGeofenceStore.Contract.GEOFENCES, null);

                }
            });

            getSupportLoaderManager().initLoader(0,null,this); //TODO : Erik, behövs denna? Den köper inte "this" iallfall.


            return rootView;
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);


        }




        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(getActivity(),
                    MyGeofenceStore.Contract.GEOFENCES,
                    new String[]{MyGeofenceStore.Contract.ID,
                            MyGeofenceStore.Contract.LONGITUDE,
                            MyGeofenceStore.Contract.LATITUDE},
                    null, null, null);

        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

            mListAdapter.swapCursor(cursor);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_save_geofence) {
            getContentResolver().notifyChange(MyGeofenceStore.Contract.GEOFENCES, null);
        } else if (id == R.id.action_change_view) {
            mViewPager.setCurrentItem(MAPS_LIST_PAGE);
        }
        return super.onOptionsItemSelected(item);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case MAPS_PAGE:
                    return mMapsFragment;
                case MAPS_LIST_PAGE:
                    return mMapsListFragment;
                default:
                    return null;
            }
        }


        @Override
        public int getCount() {

            return 2;
        }


    }

}
