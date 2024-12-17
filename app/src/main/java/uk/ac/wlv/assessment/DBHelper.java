package uk.ac.wlv.assessment;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

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
        contentValues.put(COL_MSG_2, userId);
        contentValues.put(COL_MSG_3, title);
        contentValues.put(COL_MSG_4, message);
        contentValues.put(COL_MSG_5, imagePath);

        long result = db.insert(BLOG_TABLE_NAME, null, contentValues);
        return result != -1; // Returns false if insert failed
    }

    public Cursor getMessagesByUserId(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Query to get the message ID, title, message, and image where USER_UD matches the given userId
        return db.rawQuery("SELECT " + COL_MSG_1 + ", " + COL_MSG_3 + ", " + COL_MSG_4 + ", " + COL_MSG_5 +
                " FROM " + BLOG_TABLE_NAME +
                " WHERE " + COL_MSG_2 + " = ?", new String[]{String.valueOf(userId)});
    }

    public boolean updateMessage(String row_id, String title, String message){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_MSG_3, title);
        contentValues.put(COL_MSG_4, message);

        long result = db.update(BLOG_TABLE_NAME, contentValues, "ID=?", new String[]{row_id});
        if(result == -1){
            Log.d("ViewMessage", "Failed to update DB");
        }
        return result != -1; // Returns false if insert fails
    }

    public Cursor searchMessages(int userId, String query) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = "user_id = ? AND (message_title LIKE ? OR message LIKE ?)";
        String[] selectionArgs = { String.valueOf(userId), "%" + query + "%", "%" + query + "%" };
        return db.query("messages", null, selection, selectionArgs, null, null, null);
    }

    // Method to delete multiple messages by IDs
    public void deleteMessagesByIds(List<String> ids) {
        if (ids == null || ids.isEmpty()) return;

        SQLiteDatabase db = this.getWritableDatabase();
        String args = new String(new char[ids.size()]).replace("\0", "?,").replaceAll(",$", ""); // Create placeholders like "?, ?, ?"
        String whereClause = COL_MSG_1 + " IN (" + args + ")";

        try {
            db.delete(BLOG_TABLE_NAME, whereClause, ids.toArray(new String[0]));
        } catch (Exception e) {
            Log.e("DBHelper", "Error deleting messages: " + e.getMessage());
        } finally {
            db.close();
        }
    }



}
