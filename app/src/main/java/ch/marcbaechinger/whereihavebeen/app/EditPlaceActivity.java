package ch.marcbaechinger.whereihavebeen.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toolbar;

import com.google.android.gms.maps.GoogleMap;

import app.ch.marcbaechinger.whereihavebeen.R;
import ch.marcbaechinger.whereihavebeen.app.tracking.TrackerManager;
import ch.marcbaechinger.whereihavebeen.fragments.EditPlaceFragment;
import ch.marcbaechinger.whereihavebeen.fragments.ImageUtility;


public class EditPlaceActivity extends Activity {

    private static final String TAG = ImageUtility.class.getName();
    private GoogleMap mMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_place);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new EditPlaceFragment())
                    .commit();
        }

        getWindow().setAllowEnterTransitionOverlap(true);
        setActionBar((Toolbar) findViewById(R.id.toolbar));

        TrackerManager manager = new TrackerManager();
        manager.trackActivity(this, "Edit place");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_place, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
