package ch.marcbaechinger.whereihavebeen.fragments;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import app.ch.marcbaechinger.whereihavebeen.R;
import ch.marcbaechinger.whereihavebeen.adapter.PlaceAdapter;
import ch.marcbaechinger.whereihavebeen.app.EditPlaceActivity;
import ch.marcbaechinger.whereihavebeen.app.PlaceDetailActivity;
import ch.marcbaechinger.whereihavebeen.app.data.DataContract;
import ch.marcbaechinger.whereihavebeen.swipe.SwipeDismissListViewTouchListener;

public class PlacesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>  {

    private static final String[] PROJECTION = new String[]{
            DataContract.PLACE.FIELD_ID,
            DataContract.PLACE.TABLE + "." + DataContract.PLACE.FIELD_TITLE,
            DataContract.PLACE.FIELD_DESCRIPTION,
            DataContract.PLACE.FIELD_PICTURE,
            DataContract.CATEGORY.TABLE + "." + DataContract.CATEGORY.FIELD_TITLE,
            DataContract.CATEGORY.TABLE + "." + DataContract.CATEGORY.FIELD_COLOR
    };

    private SimpleCursorAdapter listViewAdapter;
    private String selectedCategoryTitle = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        String[] fields = {DataContract.PLACE.FIELD_PICTURE, DataContract.PLACE.FIELD_TITLE, DataContract.CATEGORY.FIELD_TITLE, DataContract.CATEGORY.FIELD_COLOR};
        int[] views = {R.id.placeListViewItemImage, R.id.placeListViewItemTitle, R.id.placeListViewItemCategory};

        listViewAdapter = new PlaceAdapter(getActivity(),
                R.layout.place_list_item, null,
                fields,
                views, 0);

        rootView.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createIntent = new Intent(getActivity(), EditPlaceActivity.class);
                startActivity(createIntent);
            }
        });

        ListView placesListView = (ListView) rootView.findViewById(R.id.placesListView);
        placesListView.setAdapter(listViewAdapter);
        placesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long index) {
                Intent detailIntent = new Intent(getActivity(), PlaceDetailActivity.class);
                detailIntent.putExtra(Intent.EXTRA_UID, adapterView.getItemIdAtPosition(pos));
                detailIntent.putExtra("category", view.getTag(R.id.categoryTitle).toString());
                startActivity(detailIntent);
            }
        });

        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        placesListView,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    Cursor cursor = listViewAdapter.getCursor();
                                    if (cursor.moveToPosition(reverseSortedPositions[0])) {
                                        String id = cursor.getString(cursor.getColumnIndex(DataContract.PLACE.FIELD_ID));
                                        getActivity().getContentResolver().delete(
                                                DataContract.PLACE.CONTENT_URI,
                                                DataContract.PLACE.FIELD_ID + " = ?",
                                                new String[]{id}
                                        );
                                        getLoaderManager().restartLoader(0, null, PlacesFragment.this);
                                    }
                                }
                            }
                        });
        placesListView.setOnTouchListener(touchListener);
        // Setting this scroll listener is required to ensure that during ListView scrolling,
        // we don't look for swipes.
        placesListView.setOnScrollListener(touchListener.makeScrollListener());
        getLoaderManager().initLoader(0, null, this);

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] selectionArgs = null;
        if (selectedCategoryTitle != null) {
            selectionArgs = new String[]{selectedCategoryTitle};
        }
        return new CursorLoader(getActivity(), DataContract.PLACE.CONTENT_URI,
                PROJECTION, null, selectionArgs, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        listViewAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        listViewAdapter.swapCursor(null);
    }

    public void setCategoryTitle(String title) {
        if (selectedCategoryTitle != null && title == null) {
            selectedCategoryTitle = title;
            getLoaderManager().restartLoader(0, null, this);
        } else if (title != null && (selectedCategoryTitle == null || !selectedCategoryTitle.equals(title))) {
            selectedCategoryTitle = title;
            getLoaderManager().restartLoader(0, null, this);
        }
    }
}
