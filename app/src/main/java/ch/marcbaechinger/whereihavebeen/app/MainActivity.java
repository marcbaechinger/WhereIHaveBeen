package ch.marcbaechinger.whereihavebeen.app;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
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
import ch.marcbaechinger.whereihavebeen.model.UIModel;


public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private CharSequence mTitle;
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

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        leftDrawer = findViewById(R.id.left_drawer);
        mDrawerList = (ListView) findViewById(R.id.categoryList);

        mTitle = getString(R.string.all_categories);
        setTitle(mTitle);

        setupToolbar();
        setupDrawer();


        selectItem(-1);
        getLoaderManager().initLoader(0, null, new CategoryAdapterCallbacks(this, categoryAdapter));
    }

    private void setupDrawer() {
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
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                mToolbar.setTitle(mTitle);
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void setupToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setActionBar(mToolbar);
        getActionBar().setHomeButtonEnabled(true);
        mToolbar.setTitle(mTitle);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "clicked on nav icon");
                mDrawerLayout.openDrawer(leftDrawer);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_show_map) {
            Intent intent = new Intent(this, MapActivity.class);
            intent.setAction(MapActivity.LIST_VIEW);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


    private void selectItem(int position) {
        if (position > -1) {
            Cursor cursor = (Cursor)mDrawerList.getItemAtPosition(position);
            int col = cursor.getColumnIndex(DataContract.CATEGORY.FIELD_TITLE);
            mTitle = cursor.getString(col);
            mDrawerList.setSelection(position);
            mDrawerList.setItemChecked(position, true);
            allLocationsItem.setSelected(false);
            UIModel.instance(getApplicationContext()).setSelectedCategoryByTitle(mTitle.toString());
        } else {
            mTitle = getString(R.string.all_categories);
            UIModel.instance(getApplicationContext()).setSelectedCategory(null);
            allLocationsItem.setSelected(true);
            mDrawerList.setItemChecked(mDrawerList.getCheckedItemPosition(), false);
            mDrawerList.setSelection(-1);
        }
        mDrawerLayout.closeDrawer(leftDrawer);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }
}
