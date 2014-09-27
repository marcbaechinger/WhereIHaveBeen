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
import ch.marcbaechinger.whereihavebeen.app.PlaceDetailActivity;
import ch.marcbaechinger.whereihavebeen.app.data.DataContract;
import ch.marcbaechinger.whereihavebeen.swipe.SwipeDismissListViewTouchListener;

public class PlacesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>  {

    private static final String[] PROJECTION = new String[]{
            DataContract.PLACE.FIELD_ID,
            DataContract.PLACE.FIELD_TITLE,
            DataContract.PLACE.FIELD_DESCRIPTION,
            DataContract.PLACE.FIELD_PICTURE
    };

    private SimpleCursorAdapter listViewAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        String[] fields = {DataContract.PLACE.FIELD_PICTURE, DataContract.PLACE.FIELD_TITLE};
        int[] views = {R.id.placeListViewItemImage, R.id.placeListViewItemTitle};

        listViewAdapter = new PlaceAdapter(getActivity(),
                R.layout.place_list_item, null,
                fields,
                views, 0);

        ListView placesListView = (ListView) rootView.findViewById(R.id.placesListView);
        placesListView.setAdapter(listViewAdapter);
        placesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long index) {
                Intent detailIntent = new Intent(getActivity(), PlaceDetailActivity.class);
                detailIntent.putExtra(Intent.EXTRA_UID, adapterView.getItemIdAtPosition(pos));
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
                                //®listViewAdapter.notifyDataSetChanged();
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
        return new CursorLoader(getActivity(), DataContract.PLACE.CONTENT_URI,
                PROJECTION, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        listViewAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        listViewAdapter.swapCursor(null);
    }
}
