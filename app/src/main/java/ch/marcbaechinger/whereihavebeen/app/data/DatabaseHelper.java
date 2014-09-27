package ch.marcbaechinger.whereihavebeen.app.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = DatabaseHelper.class.getName();

    public static final String DATABASE_NAME = "whereihavebeen";
    public static final int VERSION = 20;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    public static final String getCreateQuery() {
        return "CREATE TABLE " + DataContract.PLACE.TABLE + " (" +
                DataContract.PLACE.FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DataContract.PLACE.FIELD_TITLE + " TEXT NOT NULL, " +
                DataContract.PLACE.FIELD_DESCRIPTION + " TEXT, " +
                DataContract.PLACE.FIELD_LAT + " REAL, " +
                DataContract.PLACE.FIELD_LNG + " REAL, " +
                DataContract.PLACE.FIELD_PICTURE + " TEXT);";
    }

    public static final String getDropQuery() {
        return "DROP TABLE IF EXISTS " + DataContract.PLACE.TABLE;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(getCreateQuery());
        sqLiteDatabase.execSQL("INSERT INTO " + DataContract.PLACE.TABLE + " (" +
                DataContract.PLACE.FIELD_TITLE + "," +
                DataContract.PLACE.FIELD_DESCRIPTION + "," +
                DataContract.PLACE.FIELD_LAT + "," +
                DataContract.PLACE.FIELD_LNG +
                ") VALUES ('Pub at TowerBridge', 'Nice pub', 41.22, 7.33)");

        sqLiteDatabase.execSQL("INSERT INTO " + DataContract.PLACE.TABLE + " (" +
                DataContract.PLACE.FIELD_TITLE + "," +
                DataContract.PLACE.FIELD_DESCRIPTION + "," +
                DataContract.PLACE.FIELD_LAT + "," +
                DataContract.PLACE.FIELD_LNG +
                ") VALUES ('Bourough market', 'Super cooler Markt', 41.22, 7.33)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL(getDropQuery());
        onCreate(sqLiteDatabase);
    }
}
