package ch.marcbaechinger.whereihavebeen.app;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toolbar;

import app.ch.marcbaechinger.whereihavebeen.R;
import ch.marcbaechinger.whereihavebeen.adapter.CategoryAdapter;
import ch.marcbaechinger.whereihavebeen.adapter.CategoryAdapterCallbacks;
import ch.marcbaechinger.whereihavebeen.app.data.DataContract;
import ch.marcbaechinger.whereihavebeen.fragments.PlacesFragment;


public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private CharSequence mTitle;
    private CharSequence mDrawerTitle;
    private String[] drawerItems;
    private ActionBarDrawerToggle mDrawerToggle;
    private CategoryAdapter categoryAdapter;


    private View leftDrawer;
    private View allLocationsItem;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlacesFragment())
                    .commit();
        }


        mTitle = getString(R.string.all_categories);
        mDrawerTitle = getTitle();
        setTitle(mTitle);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(mTitle);
        setActionBar(mToolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        leftDrawer = findViewById(R.id.left_drawer);
        mDrawerList = (ListView) findViewById(R.id.categoryList);

        drawerItems = getResources().getStringArray(R.array.drawer_items);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        String[] fields = new String[]{DataContract.CATEGORY.FIELD_TITLE};
        int[] views = new int[]{ R.id.textLabel};
        categoryAdapter = new CategoryAdapter(this, R.layout.drawer_list_item, null, fields, views, 0);

        allLocationsItem = findViewById(R.id.allCategories);
        allLocationsItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectItem(-1);
            }
        });
        mDrawerList.setAdapter(categoryAdapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                mToolbar.setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                mToolbar.setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        selectItem(-1);
        getLoaderManager().initLoader(0, null, new CategoryAdapterCallbacks(this, categoryAdapter));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add_person) {
            Intent friendsActivityIntent = new Intent(this, AddFriendActivity.class);
            startActivity(friendsActivityIntent);
        }
        return super.onOptionsItemSelected(item);
    }


    private void selectItem(int position) {
        if (position > -1) {
            Cursor cursor = (Cursor)mDrawerList.getItemAtPosition(position);
            int col = cursor.getColumnIndex(DataContract.CATEGORY.FIELD_TITLE);
            mTitle = cursor.getString(col);
            selectPlaces(mTitle.toString());
            mDrawerList.setSelection(position);
            mDrawerList.setItemChecked(position, true);
            allLocationsItem.setSelected(false);
        } else {
            mTitle = getString(R.string.all_categories);
            selectPlaces(null);
            allLocationsItem.setSelected(true);
            mDrawerList.setItemChecked(mDrawerList.getCheckedItemPosition(), false);
            mDrawerList.setSelection(-1);
        }
        mDrawerLayout.closeDrawer(leftDrawer);
    }

    private void selectPlaces(String categoryTitle) {
        PlacesFragment placesFragment = (PlacesFragment) getFragmentManager().findFragmentById(R.id.container);
        if (placesFragment != null) {
            placesFragment.setCategoryTitle(categoryTitle);
        }
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }
}
