package ch.marcbaechinger.whereihavebeen.app;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toolbar;

import app.ch.marcbaechinger.whereihavebeen.R;
import ch.marcbaechinger.whereihavebeen.app.tracking.TrackerManager;
import ch.marcbaechinger.whereihavebeen.fragments.PlaceDetailFragment;


public class PlaceDetailActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail_place);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceDetailFragment())
                    .commit();
        }

        getWindow().setAllowEnterTransitionOverlap(true);
        setupToolbar();

        TrackerManager manager = new TrackerManager();
        manager.trackActivity(this, "Place detail");
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        setActionBar(toolbar);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public boolean onNavigateUp() {
        onBackPressed();
        return true;
    }
}
