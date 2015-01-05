package ch.marcbaechinger.whereihavebeen.app;

import android.app.Activity;
import android.os.Bundle;

import app.ch.marcbaechinger.whereihavebeen.R;
import ch.marcbaechinger.whereihavebeen.app.tracking.TrackerManager;
import ch.marcbaechinger.whereihavebeen.fragments.GMapFragment;

public class MapActivity extends Activity {

    public static final String LIST_VIEW = "places_list_view";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new GMapFragment())
                    .commit();
        }

        TrackerManager manager = new TrackerManager();
        manager.trackActivity(this, "Map");
    }


}
