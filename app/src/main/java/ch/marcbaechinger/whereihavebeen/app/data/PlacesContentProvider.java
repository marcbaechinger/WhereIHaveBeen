package ch.marcbaechinger.whereihavebeen.app.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class PlacesContentProvider extends ContentProvider {

    public static final int PLACE = 100;
    public static final int PLACE_ID = 101;
    public static final int CATEGORY = 200;

    public static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        URI_MATCHER.addURI(DataContract.AUTHORITY, DataContract.PLACE.PATH, PLACE);
        URI_MATCHER.addURI(DataContract.AUTHORITY, DataContract.PLACE.PATH + "/#", PLACE_ID);
        URI_MATCHER.addURI(DataContract.AUTHORITY, DataContract.CATEGORY.PATH, CATEGORY);
    }

    private DatabaseHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new DatabaseHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        int match = URI_MATCHER.match(uri);
        switch (match) {
            case PLACE:
                return queryForAllPlaces(selectionArgs);
            case PLACE_ID:
                return queryForSinglePlace(uri, projection, sortOrder);
            case CATEGORY:
                return queryForAllCategories(projection, sortOrder);
        }
        return null;
    }

    private Cursor queryForAllCategories(String[] projection, String sortOrder) {
        return dbHelper.getWritableDatabase().query(DataContract.CATEGORY.TABLE, projection, null, null, null, null, sortOrder);
    }

    private Cursor queryForSinglePlace(Uri uri, String[] projection, String sortOrder) {
        String id = uri.getPathSegments().get(1);
        String selection = DataContract.PLACE.TABLE + "." + DataContract.PLACE.FIELD_ID + " = ? ";
        String[] selectionArgs = new String[]{ id };
        return dbHelper.getReadableDatabase().query(DataContract.PLACE.TABLE, projection, selection, selectionArgs, null, null, sortOrder);
    }

    private Cursor queryForAllPlaces(String[] selectionArgs) {
        String category = null;
        if (selectionArgs != null && selectionArgs.length == 1) {
            category = selectionArgs[0];
        }
        return dbHelper.getWritableDatabase().rawQuery(DatabaseHelper.getSelectPlaceQuery(category), null);
    }

    @Override
    public String getType(Uri uri) {
        int match = URI_MATCHER.match(uri);
        switch (match) {
            case PLACE:
                return DataContract.PLACE.CONTENT_TYPE_DIR;
            case PLACE_ID:
                return DataContract.PLACE.CONTENT_TYPE_ITEM;
        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        int match = URI_MATCHER.match(uri);
        Uri createUri = null;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (match) {
            case PLACE:
                long id = db.insert(DataContract.PLACE.TABLE, null, contentValues);
                createUri = ContentUris.withAppendedId(DataContract.PLACE.CONTENT_URI, id);
                break;
        }
        getContext().getContentResolver().notifyChange(createUri, null);
        return createUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int match = URI_MATCHER.match(uri);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (match) {
            case PLACE:
                return db.delete(DataContract.PLACE.TABLE, selection, selectionArgs);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        int match = URI_MATCHER.match(uri);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (match) {
            case PLACE:
                int updateCount = db.update(DataContract.PLACE.TABLE, contentValues, DataContract.PLACE.FIELD_ID + "=?", selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                return updateCount;
        }
        return 0;
    }
}
