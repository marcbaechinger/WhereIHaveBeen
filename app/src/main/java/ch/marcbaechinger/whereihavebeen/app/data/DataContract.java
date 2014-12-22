package ch.marcbaechinger.whereihavebeen.app.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class DataContract {

    public static final String AUTHORITY = "ch.marcbaechinger.whereihavebeen.provider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static class PLACE {

        public static final String PATH = "place";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH).build();

        public static final String CONTENT_TYPE_DIR = "vnd.android.cursor.dir/" + AUTHORITY + "/" + PATH;
        public static final String CONTENT_TYPE_ITEM = "vnd.android.cursor.ITEM/" + AUTHORITY + "/" + PATH;

        public static final String TABLE = "PLACE";

        public static final String FIELD_ID = BaseColumns._ID;
        public static final String FIELD_TITLE = "TITLE";
        public static final String FIELD_DESCRIPTION = "DESCRIPTION";
        public static final String FIELD_LNG = "LNG";
        public static final String FIELD_LAT = "LAT";
        public static final String FIELD_PICTURE = "PICTURE";
        public static final String FIELD_CATEGORY = "CATEGORY";

        public static final Uri buildUriWithId(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static class CATEGORY {

        public static final String PATH = "category";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH).build();

        public static final String CONTENT_TYPE_DIR = "vnd.android.cursor.dir/" + AUTHORITY + "/" + PATH;
        public static final String CONTENT_TYPE_ITEM = "vnd.android.cursor.ITEM/" + AUTHORITY + "/" + PATH;

        public static final String TABLE = "CATEGORY";

        public static final String FIELD_ID = BaseColumns._ID;
        public static final String FIELD_TITLE = "CATEGORY_TITLE";
        public static final String FIELD_COLOR = "COLOR";

        public static final Uri buildUriWithId(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
