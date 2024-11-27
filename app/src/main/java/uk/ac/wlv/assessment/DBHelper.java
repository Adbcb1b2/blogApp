package uk.ac.wlv.assessment;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "userdata.db";

    // User table constants
    private static final String TABLE_NAME = "userdata";
    private static final String COL_1 = "ID";
    private static final String COL_2 = "USERNAME";
    private static final String COL_3 = "PASSWORD";

    // Messages table constants
    private static final String BLOG_TABLE_NAME = "usermsgs";
    private static final String COL_MSG_1 = "ID";
    private static final String COL_MSG_2 = "USER_UD";
    private static final String COL_MSG_3 = "TITLE";
    private static final String COL_MSG_4 = "MESSAGE";
    private static final String COL_MSG_5 = "IMAGE";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create userdata table
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, USERNAME TEXT UNIQUE, PASSWORD TEXT)");

        // Create usermsgs table
        db.execSQL("CREATE TABLE " + BLOG_TABLE_NAME + " (" +
                COL_MSG_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_MSG_2 + " INTEGER, " +
                COL_MSG_3 + " TEXT, " +
                COL_MSG_4 + " TEXT, " +
                COL_MSG_5 + " TEXT, " +
                "FOREIGN KEY(" + COL_MSG_2 + ") REFERENCES " + TABLE_NAME + "(ID))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("CREATE TABLE " + BLOG_TABLE_NAME + " (" +
                    COL_MSG_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_MSG_2 + " INTEGER, " +
                    COL_MSG_3 + " TEXT, " +
                    COL_MSG_4 + " TEXT, " +
                    COL_MSG_5 + " TEXT, " +
                    "FOREIGN KEY(" + COL_MSG_2 + ") REFERENCES " + TABLE_NAME + "(ID))");
        }
        Log.d("DBHelper", "Created table " + BLOG_TABLE_NAME);
    }

    public boolean insertData(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, username);
        contentValues.put(COL_3, password);

        long result = db.insert(TABLE_NAME, null, contentValues);
        return result != -1; // returns false if insert fails (e.g., user already exists)
    }

    public boolean checkUserExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE USERNAME=?", new String[]{username})) {
            return cursor.getCount() > 0;
        }
    }

    public boolean checkPassword(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE USERNAME=? AND PASSWORD=?", new String[]{username, password})) {
            return cursor.getCount() > 0;
        }

    }

    public int getUserID(String username, String password){
        SQLiteDatabase db = this.getReadableDatabase();
        int userID = -1; // To store returned userID, -1 will be returned if fails.

        try (Cursor cursor = db.rawQuery("SELECT ID FROM " + TABLE_NAME + " WHERE USERNAME = ? AND PASSWORD = ?", new String[]{username, password})){
            if (cursor.moveToFirst()) {
                userID = cursor.getInt(0);
            }
        }
        return userID;
    }

    public boolean insertMessage(int userId, String title, String message, String imagePath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        // Add values to the ContentValues
        contentValues.put(COL_MSG_2, userId);  // User ID
        contentValues.put(COL_MSG_3, title);   // Title
        contentValues.put(COL_MSG_4, message); // Message
        contentValues.put(COL_MSG_5, imagePath); // Image Path (may be null)

        long result = db.insert(BLOG_TABLE_NAME, null, contentValues);
        return result != -1; // Returns false if insert failed
    }
}
