package com.gmoonxs.www.orangehome;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 威 on 2016/8/1.
 */
public class LogsDB extends SQLiteOpenHelper {

    private final static String DATABASE_NAME = "OrangeHome.db";
    private final static int DATABASE_VERSION = 1;

    private final static String TABLE_NAME = "logs_table";
    public final static String LOG_ID = "log_id";
    public final static String LOG_TIME = "log_time";
    public final static String LOG_ACTION = "log_action";



    public LogsDB(Context context) {
    // TODO Auto-generated constructor stub
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_NAME + " (" + LOG_ID
                + " INTEGER primary key autoincrement, " + LOG_TIME + "datetime not null DEFAULT (datetime('now','localtime')), "+ LOG_ACTION +" TEXT);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(sql);
        onCreate(db);
    }

    public Cursor select() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db
                .query(TABLE_NAME, null, null, null, null, null, null);
        return cursor;
    }

    //添加操作
    public long insert(String action)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        /* ContentValues */
        ContentValues cv = new ContentValues();
        cv.put(LOG_ACTION, action);
        long row = db.insert(TABLE_NAME, null, cv);
        return row;
    }

    //删除操作
    public void delete(int id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = LOG_ID + " = ?";
        String[] whereValue ={ Integer.toString(id) };
        db.delete(TABLE_NAME, where, whereValue);
    }

    /**
     * 删除数据库
     *
     * @param context
     * @return
     */
    public boolean deleteDatabase(Context context) {
        return context.deleteDatabase(DATABASE_NAME);
    }
}
