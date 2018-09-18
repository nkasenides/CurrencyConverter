package uk.ac.uclan.nkasenides.currencyconverter.DBUtils;

/**
 * Created by Nicos on 08-Jan-17.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseOpenHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "currencyConverter.db";

    private static final String SQL_CREATE_FAVORITES_ENTRIES =
            "CREATE TABLE favorites (" +
                    "currencyID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL" +
                    ")";

    private static final String SQL_DELETE_FAVORITES_ENTRIES = "DROP TABLE IF EXISTS favorites";

    public DatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }//end DatabaseOpenHelper()

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_FAVORITES_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_FAVORITES_ENTRIES);
        onCreate(db);
    }//end onUpgrade()

}//end class DatabaseOpenHelper
