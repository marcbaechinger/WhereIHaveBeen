package ch.marcbaechinger.whereihavebeen.app.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = DatabaseHelper.class.getName();

    public static final String DATABASE_NAME = "whereihavebeen";
    public static final int VERSION = 29;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    public static final String getCreatePlaceTableQuery() {
        return "CREATE TABLE " + DataContract.PLACE.TABLE + " (" +
                DataContract.PLACE.FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DataContract.PLACE.FIELD_TITLE + " TEXT NOT NULL, " +
                DataContract.PLACE.FIELD_DESCRIPTION + " TEXT, " +
                DataContract.PLACE.FIELD_LAT + " REAL, " +
                DataContract.PLACE.FIELD_LNG + " REAL, " +
                DataContract.PLACE.FIELD_CATEGORY + " INTEGER, " +
                DataContract.PLACE.FIELD_PICTURE + " TEXT, " +
                "FOREIGN KEY(" + DataContract.PLACE.FIELD_CATEGORY + " ) REFERENCES " +
                    DataContract.CATEGORY.TABLE + "(" + DataContract.CATEGORY.FIELD_ID + ")" +
                ");";
    }

    public static final String getSelectPlaceQuery(String category) {
        String query = "SELECT " +
                DataContract.PLACE.TABLE + "." + DataContract.PLACE.FIELD_ID + "," +
                DataContract.PLACE.TABLE + "." + DataContract.PLACE.FIELD_TITLE + "," +
                DataContract.PLACE.FIELD_DESCRIPTION + "," +
                DataContract.PLACE.FIELD_LAT + "," +
                DataContract.PLACE.FIELD_LNG + "," +
                DataContract.CATEGORY.TABLE + "." + DataContract.CATEGORY.FIELD_TITLE + "," +
                DataContract.CATEGORY.TABLE + "." + DataContract.CATEGORY.FIELD_COLOR + "," +
                DataContract.PLACE.FIELD_PICTURE +
                " FROM " + DataContract.PLACE.TABLE + "," + DataContract.CATEGORY.TABLE +
                " WHERE " + DataContract.PLACE.TABLE + "." + DataContract.PLACE.FIELD_CATEGORY + "=" +
                DataContract.CATEGORY.TABLE + "." + DataContract.CATEGORY.FIELD_ID;
        if (category != null) {
            query += " AND " + DataContract.CATEGORY.TABLE + "." + DataContract.CATEGORY.FIELD_TITLE + " = '" + category + "'";
        }
        return query;
    }

    public static final String getCreateCategoryTableQuery() {
        return "CREATE TABLE " + DataContract.CATEGORY.TABLE + " (" +
                DataContract.CATEGORY.FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DataContract.CATEGORY.FIELD_TITLE + " TEXT NOT NULL, " +
                DataContract.CATEGORY.FIELD_COLOR + " TEXT NOT NULL);";
    }

    public static final String getDropPlaceTableStatement() {
        return "DROP TABLE IF EXISTS " + DataContract.PLACE.TABLE;
    }

    public static final String getDropCategoryStatement() {
        return "DROP TABLE IF EXISTS " + DataContract.CATEGORY.TABLE;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(getCreateCategoryTableQuery());
        sqLiteDatabase.execSQL(getCreatePlaceTableQuery());


        sqLiteDatabase.execSQL("INSERT INTO " + DataContract.CATEGORY.TABLE + " (" +
                DataContract.CATEGORY.FIELD_TITLE +
                ", " + DataContract.CATEGORY.FIELD_COLOR + ") VALUES ('Restaurant', '#B71C1C')");
        sqLiteDatabase.execSQL("INSERT INTO " + DataContract.CATEGORY.TABLE + " (" +
                DataContract.CATEGORY.FIELD_TITLE +
                ", " + DataContract.CATEGORY.FIELD_COLOR + ") VALUES ('Cinema', '#1565C0')");
        sqLiteDatabase.execSQL("INSERT INTO " + DataContract.CATEGORY.TABLE + " (" +
                DataContract.CATEGORY.FIELD_TITLE +
                ", " + DataContract.CATEGORY.FIELD_COLOR + ") VALUES ('Bar', '#EF6C00')");
        sqLiteDatabase.execSQL("INSERT INTO " + DataContract.CATEGORY.TABLE + " (" +
                DataContract.CATEGORY.FIELD_TITLE +
                ", " + DataContract.CATEGORY.FIELD_COLOR + ") VALUES ('Point of interest', '#5D4037')");
        sqLiteDatabase.execSQL("INSERT INTO " + DataContract.CATEGORY.TABLE + " (" +
                DataContract.CATEGORY.FIELD_TITLE +
                ", " + DataContract.CATEGORY.FIELD_COLOR + ") VALUES ('Park', '#2E7D32')");

        sqLiteDatabase.execSQL("INSERT INTO " + DataContract.PLACE.TABLE + " (" +
                DataContract.PLACE.FIELD_TITLE + "," +
                DataContract.PLACE.FIELD_DESCRIPTION + "," +
                DataContract.PLACE.FIELD_CATEGORY + "," +
                DataContract.PLACE.FIELD_LAT + "," +
                DataContract.PLACE.FIELD_LNG +
                ") VALUES ('Pub at TowerBridge', 'Nice pub', 1, 41.22, 7.33)");

        sqLiteDatabase.execSQL("INSERT INTO " + DataContract.PLACE.TABLE + " (" +
                DataContract.PLACE.FIELD_TITLE + "," +
                DataContract.PLACE.FIELD_DESCRIPTION + "," +
                DataContract.PLACE.FIELD_CATEGORY + "," +
                DataContract.PLACE.FIELD_LAT + "," +
                DataContract.PLACE.FIELD_LNG +
                ") VALUES ('Bourough market', 'Super cooler Markt', 2, 41.22, 7.33)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL(getDropPlaceTableStatement());
        sqLiteDatabase.execSQL(getDropCategoryStatement());
        onCreate(sqLiteDatabase);
    }
}
