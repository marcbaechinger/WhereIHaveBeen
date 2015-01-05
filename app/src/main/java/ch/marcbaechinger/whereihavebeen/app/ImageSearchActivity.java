package ch.marcbaechinger.whereihavebeen.app;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.HashMap;
import java.util.Map;

import app.ch.marcbaechinger.whereihavebeen.R;
import ch.marcbaechinger.whereihavebeen.app.tracking.TrackerManager;
import ch.marcbaechinger.whereihavebeen.fragments.ImageSearchFragment;
import ch.marcbaechinger.whereihavebeen.fragments.ImageSelectionListener;
import ch.marcbaechinger.whereihavebeen.model.UIModel;

public class ImageSearchActivity extends Activity {

    public static final String IMAGE_QUERY = "IMAGE_QUERY_TERM";
    public static final String URI_KEY = "uri_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_search);

        final String uriKey = getIntent().getStringExtra(URI_KEY);

        if (savedInstanceState == null) {
            ImageSearchFragment imageSearchFragment = new ImageSearchFragment();
            imageSearchFragment.setImageSelectionListener(new ImageSelectionListener() {
                @Override
                public void imageSelected(Uri uri) {
                    if (uriKey == null) {
                        if (uri == null) {
                            UIModel.instance(ImageSearchActivity.this).getEditPlace().setPictureUri(null);
                        } else {
                            UIModel.instance(ImageSearchActivity.this).getEditPlace().setPictureUri(uri.toString());
                        }
                    } else {
                        SharedPreferences sharedPref = ImageSearchActivity.this.getSharedPreferences
                                (getString(R.string.preference_file), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString(uriKey, uri.toString());
                        editor.commit();
                    }
                }
            });
            getFragmentManager().beginTransaction()
                    .add(R.id.container, imageSearchFragment)
                    .commit();
        }

        TrackerManager manager = new TrackerManager();
        Map<String, String> map = new HashMap<>();
        map.put("mode", uriKey == null ? "place" : uriKey);
        manager.trackActivity(this, "Image search", map);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_image_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
