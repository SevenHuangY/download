package com.nlt.db;

import java.util.ArrayList;
import java.util.List;

import com.nlt.baseObject.downloadThreadInfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class dataBaseHelper extends SQLiteOpenHelper
{
	public static final String DB_NAME = "mydata2.db"; //数据库名称
    public static final int version = 1; //数据库版本
    public static final String Table_Name = "threadInfo";

    private final static String CREATE_TABLE_USERS =
            "CREATE TABLE IF NOT EXISTS threadInfo(_id INTEGER PRIMARY KEY AUTOINCREMENT,threadID INTEGER, fileName TEXT,fileUri TEXT,startPosition INTEGER,endPosition INTEGER,progress INTEGER)";
    
    private final static String DROP_TABLE_USERS = "DROP TABLE IF EXIST threadInfo";


	public dataBaseHelper(Context context)
	{
		super(context, DB_NAME, null, version);
	

	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL(CREATE_TABLE_USERS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		// TODO Auto-generated method stub
		db.execSQL(DROP_TABLE_USERS);
        onCreate(db);
	}
		
	public void insertThreadInfo(SQLiteDatabase db, downloadThreadInfo info)
	{
		ContentValues values =  new ContentValues();
		values.put("threadID", info.getTheadID());
		values.put("fileName", info.getFileName());
		values.put("fileUri", info.getFileUri());
		values.put("startPosition", info.getStartPosition());
		values.put("endPosition", info.getEndPotition());
		values.put("progress", info.getProgress());
		db.insert(Table_Name, null, values);
	}
	
	public void deleteThreadInfo(SQLiteDatabase db, String fileName, String fileUri)
	{
		String whereClause = "fileUri =?";
        String[] whereArgs = {fileUri};
		db.delete(Table_Name, whereClause, whereArgs);
	}
	
	public void updateThreadInfo(SQLiteDatabase db, downloadThreadInfo info)
	{	
		ContentValues values = new ContentValues();
		String whereClause = "fileUri =?";
		String[] whereArgs = {info.getFileUri()};
		
		values.put("threadID", info.getTheadID());
		values.put("fileName", info.getFileName());
		values.put("fileUri", info.getFileUri());
		values.put("startPosition", info.getStartPosition());
		values.put("endPosition", info.getEndPotition());
		values.put("progress", info.getProgress());
		db.update(Table_Name, values, whereClause, whereArgs);
	}
	
	public List<downloadThreadInfo> queryThreadInfo(SQLiteDatabase db, String fileName, String fileUri)
	{
		List<downloadThreadInfo> mList = new ArrayList<downloadThreadInfo>();
		Cursor cr;
		String selection = "fileUri =?";
		String[] selectionArgs = {fileUri};
		
		cr = db.query(Table_Name, null, selection, selectionArgs, null, null, null);
		cr.moveToFirst();
		for(int i = 0; i < cr.getCount(); i++)
		{	
			int threadID = cr.getInt(cr.getColumnIndex("threadID"));
			String fName = cr.getString(cr.getColumnIndex("fileName"));
			String fUri = cr.getString(cr.getColumnIndex("fileUri"));
			int sPosition = cr.getInt(cr.getColumnIndex("startPosition"));
			int ePosition = cr.getInt(cr.getColumnIndex("endPosition"));
			int progress = cr.getInt(cr.getColumnIndex("progress"));
			
			downloadThreadInfo threadInfo = new downloadThreadInfo(threadID, fName, fUri, sPosition, ePosition, progress);
			
			mList.add(threadInfo);
			cr.moveToNext();
		}
		
		cr.close();
		return mList;	
	}

}
