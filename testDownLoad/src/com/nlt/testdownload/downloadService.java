package com.nlt.testdownload;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpStatus;

import com.nlt.baseObject.downloadThreadInfo;
import com.nlt.db.dataBaseHandler;
import com.nlt.db.dataBaseHelper;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class downloadService extends Service
{
	public static String startIntent = "startIntent";
	public static String stopIntent = "stopIntent";
	private final String TAG = "test";
	private downloadThread thread;
	private dataBaseHandler dHandler;
	
	private downloadThreadInfo threadInfo;

	private Handler mHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			if (msg.what == 0x101)
			{ 
				thread = new downloadThread(dHandler, threadInfo, downloadService.this);
				thread.start();
			}
		}
	};
	
	@Override
	public IBinder onBind(Intent intent)
	{
		// TODO Auto-generated method stub
		Log.e(TAG, "onBind");
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		// TODO Auto-generated method stub
		if(intent.getAction().equals(startIntent))
		{
			Log.e(TAG, "start service");
			threadInfo = (downloadThreadInfo) intent.getSerializableExtra("fileInfo");

			List<downloadThreadInfo> mList = new ArrayList<downloadThreadInfo>();			
			mList = dHandler.queryThreadInfo(threadInfo.getFileName(), threadInfo.getFileUri());
			if(mList.size() > 0)
			{			
				threadInfo = mList.get(0);
				mHandler.sendEmptyMessage(0x101);
			}
			else
			{			
				dHandler.insertThreadInfo(threadInfo);
				new Thread(new getFileLength()).start();
			}
			
		}
		else if(intent.getAction().equals(stopIntent))
		{
			Log.e(TAG, "stop service");
			thread.stopDownload();	
		}		
		return START_NOT_STICKY;
	}

	class getFileLength implements Runnable
	{
		
		public getFileLength()
		{
			
		}

		@Override
		public void run()
		{
			// TODO Auto-generated method stub
			int length = 0;
			URL url;
			HttpURLConnection conn = null;
			
			try
			{
				url = new URL(threadInfo.getFileUri());	
				conn = (HttpURLConnection) url.openConnection();		
				conn.setConnectTimeout(3000);		
				conn.setRequestMethod("GET");		
				if(conn.getResponseCode() == HttpStatus.SC_OK)
				{			
					length = conn.getContentLength();
					if(length < 0)
					{
						Log.e(TAG, "length < 0");
						return;
					}
					threadInfo.setEndPotition(length);
					dHandler.updateThreadInfo(threadInfo);	
					mHandler.sendEmptyMessage(0x101);
				}
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			finally
			{
				if(conn != null)
					conn.disconnect();
			}
		}	
	}
	
	@Override
	public void onCreate()
	{
		// TODO Auto-generated method stub
		super.onCreate();
		Log.e(TAG, "service create");
		
		if(dHandler == null)
		{
			dHandler = new dataBaseHandler(this, "test.db");
		}
	}

	@Override
	public void onDestroy()
	{
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.e(TAG, "service destroy");
		if(dHandler != null)
			dHandler.close();
	}
	
	
}
