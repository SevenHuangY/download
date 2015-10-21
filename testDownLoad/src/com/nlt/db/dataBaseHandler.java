package com.nlt.db;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.nlt.baseObject.downloadThreadInfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

public class dataBaseHandler
{
	private SQLiteDatabase db;
	private File file;
	private String path;
	private final String TAG = "test";
	
	public static final String Table_Name = "threadInfo";

    private final static String CREATE_TABLE_USERS =
            "CREATE TABLE IF NOT EXISTS threadInfo(_id INTEGER PRIMARY KEY AUTOINCREMENT,threadID INTEGER, fileName TEXT,fileUri TEXT,startPosition INTEGER,endPosition INTEGER,progress INTEGER)";
    
    private final static String DROP_TABLE_USERS = "DROP TABLE IF EXIST threadInfo";

	
	public dataBaseHandler(Context context, String fileName)
	{
//		Log.e(TAG, "States: " + Environment.getExternalStorageState());
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
		{
			path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/myDateBase/";
//			Log.e(TAG, "Path: " + path);
			file = new File(path, fileName);
			if(!file.exists())
			{	
				try
				{
					File dir = file.getParentFile();
					dir.mkdir();		
					file.createNewFile();
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}			
		}
		else
		{
			path = context.getDatabasePath(fileName).getAbsolutePath();
//			Log.e(TAG, "Path: " + path);
			file = new File(path);
		}
		db = SQLiteDatabase.openOrCreateDatabase(file, null);	
		createTable();
	}

	public void createTable()
	{
		db.execSQL(CREATE_TABLE_USERS);
	}
		
	public void insertThreadInfo(downloadThreadInfo info)
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
	
	public void deleteThreadInfo(String fileName, String fileUri)
	{
		String whereClause = "fileUri =?";
        String[] whereArgs = {fileUri};
		db.delete(Table_Name, whereClause, whereArgs);
	}
	
	public void updateThreadInfo(downloadThreadInfo info)
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
	
	public List<downloadThreadInfo> queryThreadInfo(String fileName, String fileUri)
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

	public void close()
	{
		// TODO Auto-generated method stub
		if(db != null)
			db.close();
	}
}
