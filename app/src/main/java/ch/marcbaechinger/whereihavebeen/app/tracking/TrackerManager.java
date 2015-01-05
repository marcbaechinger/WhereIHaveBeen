package ch.marcbaechinger.whereihavebeen.app.tracking;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.HashMap;
import java.util.Map;

import app.ch.marcbaechinger.whereihavebeen.R;
import ch.marcbaechinger.whereihavebeen.model.Place;
import ch.marcbaechinger.whereihavebeen.model.UIModel;

public class TrackerManager {
    private static final String TAG = TrackerManager.class.getSimpleName();

    private final Map<TrackerName, Tracker> trackers = new HashMap<>();

    public void trackActivity(Activity activity, String screenName) {
        this.trackActivity(activity, screenName, null);
    }

    public void trackActivity(Activity activity, String screenName, Map<String, String> params) {
        Tracker tracker = getTracker(TrackerName.APP_TRACKER, activity);

        if (screenName != null) {
            tracker.setScreenName(screenName);
        }

        if (params == null) {
            params = new HashMap<>();
        }

        Place selectedPlace = UIModel.instance(activity).getSelectedPlace();
        if (selectedPlace != null) {
            params.put("selected-id", String.valueOf(selectedPlace.getId()));
            params.put("selected-title", selectedPlace.getTitle());
        }
        HitBuilders.AppViewBuilder builder = new HitBuilders.AppViewBuilder();

        for (Map.Entry<String, String> entry : params.entrySet()) {
            builder.set(entry.getKey(), entry.getValue());
        }

        tracker.send(builder.build());
        Log.d(TAG, "sent activity " + screenName);
    }

    private synchronized Tracker getTracker(TrackerName trackerId, Context context) {
        if (!trackers.containsKey(trackerId)) {
            switch (trackerId) {
                case APP_TRACKER:
                    GoogleAnalytics analytics = GoogleAnalytics.getInstance(context);
                    Tracker t = analytics.newTracker(R.xml.global_tracker);
                    t.enableAdvertisingIdCollection(true);
                    trackers.put(trackerId, t);
                    break;
            }
        }
        return trackers.get(trackerId);
    }



}
