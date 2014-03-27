package com.isakgustafsson.facebookapplication.app;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;

/**
 * ALAMMANAGER  setinexactrepeeting funktionen för att uppdatera 15e min (lägg intent för att uppdatera facebook)
 * <p/>
 * An {@link android.app.IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * helper methods.
 */
public class MyFacebookSyncService extends IntentService {
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_UPDATE_FROM_WALL = "se.hellsoft.facebooklogindemo.action.UPDATE_FROM_WALL";
    private static final String ACTION_USER_LOGOUT = "se.hellsoft.facebooklogindemo.action.USER_LOGOUT";
    private static final String ACTION_POST_PHOTO = "se.hellsoft.facebooklogindemo.action.POST_PHOTO";
    private static final String ACTION_POST_TEXT = "se.hellsoft.facebooklogindemo.action.POST_TEXT";
    public static final String EXTRA_MESSAGE = "message";
    private static final String FACEBOOK_PREFS = "facebook_settings";
    private static final String NEXT_SINCE_VALUE = "nextSinceValue";
    private static final String TAG = "FacebookService";
    private boolean msgChecker = true;
    private boolean placeChecker = true;

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see android.app.IntentService
     */
    public static void startActionUpdateFromWall(Context context) {
        Intent intent = new Intent(context, MyFacebookSyncService.class);
        intent.setAction(ACTION_UPDATE_FROM_WALL);

        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, 0,
                AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);


        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see android.app.IntentService
     */
    public static void startActionUserLogout(Context context) {
        Intent intent = new Intent(context, MyFacebookSyncService.class);
        intent.setAction(ACTION_USER_LOGOUT);
        context.startService(intent);
    }

    public static void startPostFacebookPhoto(Context context, Uri photoUri) {
        Intent intent = new Intent(context, MyFacebookSyncService.class);
        intent.setAction(ACTION_POST_PHOTO);
        intent.setData(photoUri);
        context.startService(intent);
    }

    public static void startPostFacebookMessage(Context context, String message) {
        Intent intent = new Intent(context, MyFacebookSyncService.class);
        intent.setAction(ACTION_POST_TEXT);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.startService(intent);
    }


    public MyFacebookSyncService() {
        super(MainActivity.TAG);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPDATE_FROM_WALL.equals(action)) {
                handleActionUpdateFromWall();
            } else if (ACTION_POST_PHOTO.equals(action)) {
                handlePhotoUpload(intent.getData());
            } else if (ACTION_USER_LOGOUT.equals(action)) {
                handleActionUserLogout();
            } else if (ACTION_POST_TEXT.equals(action)) {
                final String message = intent.getStringExtra(EXTRA_MESSAGE);
                handlePostText(message);

            }
        }
    }

    private void handlePostText(String message) {
        Log.d(TAG, "kör handle");
        Log.d(TAG, "message i handle : " + message);

        Session session = Session.getActiveSession();
        boolean isOpened = session.isOpened();
        Log.d(MainActivity.TAG, "Logged in to facebook: " + isOpened);

        if (session != null && isOpened) {

            Bundle params = new Bundle();
            params.putString("message", message);
            params.putString("caption", message);

            Request request = new Request(session, "me/feed", params, HttpMethod.POST);
            RequestAsyncTask reqTask = new RequestAsyncTask(request);
            reqTask.execute();
        }


    }

    private void handlePhotoUpload(Uri photoUri) {
        Session session = Session.getActiveSession();
        boolean isOpened = session.isOpened();
        Log.d(MainActivity.TAG, "Logged in to facebook: " + isOpened);
        if (session != null && isOpened && photoUri != null) {
            try {
                ContentResolver resolver = getContentResolver();
                Bitmap bitmap = BitmapFactory.decodeStream(resolver.openInputStream(photoUri));
                Request request = Request.newUploadPhotoRequest(session, bitmap, new Request.Callback() {
                    @Override
                    public void onCompleted(Response response) {
                        Log.d(MainActivity.TAG, "Response: " + response.getError());
                    }
                });
                Response response = request.executeAndWait();
                GraphObject graphObject = response.getGraphObject();
                if (graphObject != null) {
                    Log.d(MainActivity.TAG, graphObject.toString());
                } else {
                    Log.d(MainActivity.TAG, "Response: " + response);
                }

            } catch (FileNotFoundException e) {
                Log.e(MainActivity.TAG, "Error uploading photo to Facebook!");
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionUpdateFromWall() {

        // Get active Facebook Session
        Session session = Session.getActiveSession();
        boolean isOpened = session.isOpened();
        Log.d(MainActivity.TAG, "Logged in to facebook: " + isOpened);
        if (session != null && isOpened) {
            SharedPreferences preferences
                    = getSharedPreferences(FACEBOOK_PREFS, MODE_PRIVATE);
            long nextSinceValue = preferences.getLong(NEXT_SINCE_VALUE, -1);
            Bundle params = new Bundle();
            params.putString("fields", "id,from,message,type,place");
            params.putString("limit", "50");
            String graphPath = "me/home";
            if (nextSinceValue > 0) {
                params.putLong("since", nextSinceValue);
            }
            Request request = new Request(session, graphPath, params, HttpMethod.GET);
            Response response = request.executeAndWait();
//            System.out.println("Response: " + response);
//            System.out.println("Error Response: " + response.getError());

            // Fetch current time to use in next request!

            GraphObject graphObject = response.getGraphObject();
//            Log.d(MainActivity.TAG, "Got graphObject: " + graphObject);
            if (graphObject != null) {
                long nowInSeconds = System.currentTimeMillis() / 1000;
                preferences.edit().putLong(NEXT_SINCE_VALUE, nowInSeconds).apply();

                JSONArray dataArray = (JSONArray) graphObject.getProperty("data");
                System.out.println("data array: " + dataArray);

                int length = dataArray.length();
                System.out.println(length);
                for (int i = 0; i < length; i++) {
                    msgChecker = true;
                    placeChecker = true;
                    JSONObject wallMessage = null;
                    try {
                        wallMessage = dataArray.getJSONObject(i);
                        storeWallMessage(wallMessage, msgChecker, placeChecker);
                    } catch (JSONException e) {
                        msgChecker = false;
                        try {
                            storeWallMessage(wallMessage, msgChecker, placeChecker);
                        } catch (JSONException e1) {
                            msgChecker = true;
                            placeChecker = false;
                            try {
                                storeWallMessage(wallMessage, msgChecker, placeChecker);
                            } catch (JSONException e2) {
                                placeChecker = false;
                                msgChecker = false;
                                try {
                                    storeWallMessage(wallMessage, msgChecker, placeChecker);
                                } catch (JSONException e3) {
                                    System.out.println("ARGH NU BLEV D FEL!!");
                                }

                            }
                        }
                        Log.e(MainActivity.TAG, "Invalid message format: " + wallMessage, e);

                    }
                }
            }
        }
    }


    private void storeWallMessage(JSONObject wallMessage, boolean msgChecker, boolean placeChecker) throws JSONException {
        String message = null;
        String name = null;
        if (msgChecker && placeChecker) {
            message = wallMessage.getString("message");
            JSONObject place = wallMessage.getJSONObject("place");
            name = place.getString("name");
        } else if (!msgChecker && placeChecker) {
            message = "No Message";
            JSONObject place = wallMessage.getJSONObject("place");
            name = place.getString("name");
        } else if (msgChecker && !placeChecker) {
            message = wallMessage.getString("message");
            name = "No Place";
        } else if (!msgChecker && !placeChecker) {
            message = "No Message";
            name = "No Place";
        }


        String messageId = wallMessage.getString("id");
        JSONObject from = wallMessage.getJSONObject("from");
        String fromId = from.getString("id");
        String fromName = from.getString("name");
        String type = wallMessage.getString("type");
        String createdTime = wallMessage.getString("created_time");


        ContentValues values = new ContentValues();
        values.put(MyFacebookWall.Contract.PLACE_NAME, name);
        values.put(MyFacebookWall.Contract.MESSAGE_ID, messageId);
        values.put(MyFacebookWall.Contract.FROM_ID, fromId);
        values.put(MyFacebookWall.Contract.FROM_NAME, fromName);
        values.put(MyFacebookWall.Contract.MESSAGE, message);
        values.put(MyFacebookWall.Contract.TYPE, type);
        values.put(MyFacebookWall.Contract.CREATED_TIME, createdTime);


        Uri newMessage = getContentResolver()
                .insert(MyFacebookWall.Contract.FACEBOOK_WALL_URI,
                        values);
        if (newMessage == null) {
            Log.e(MainActivity.TAG, "Invalid message!");
        } else {
            System.out.println("Inserted: " + newMessage);
        }
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionUserLogout() {
        getContentResolver()
                .delete(MyFacebookWall.Contract.FACEBOOK_WALL_URI,
                        null, null);
        SharedPreferences preferences
                = getSharedPreferences(FACEBOOK_PREFS, MODE_PRIVATE);
        preferences.edit().remove(NEXT_SINCE_VALUE).apply();
    }
}
