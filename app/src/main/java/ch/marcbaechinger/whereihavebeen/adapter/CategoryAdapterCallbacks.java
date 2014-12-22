package ch.marcbaechinger.whereihavebeen.adapter;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.os.Bundle;

import ch.marcbaechinger.whereihavebeen.app.data.DataContract;

/**
 * Created by bachinger on 12/21/14.
 */
public class CategoryAdapterCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final String[] CATEGORY_PROJECTION = new String[]{
            DataContract.CATEGORY.FIELD_ID,
            DataContract.CATEGORY.FIELD_TITLE,
            DataContract.CATEGORY.FIELD_COLOR
    };

    private Context context;
    private CategoryAdapter adapter;

    public CategoryAdapterCallbacks(Context context, CategoryAdapter adapter) {
        this.context = context;
        this.adapter = adapter;
    }

    @Override
    public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(context, DataContract.CATEGORY.CONTENT_URI,
                CATEGORY_PROJECTION, null, null, null);
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
