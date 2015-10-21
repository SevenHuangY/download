package com.nlt.testdownload;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpStatus;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import com.nlt.baseObject.downloadThreadInfo;
import com.nlt.db.dataBaseHandler;
import com.nlt.db.dataBaseHelper;

public class downloadThread extends Thread
{
	private dataBaseHandler dHandler;
	private downloadThreadInfo threadInfo;
	private boolean isStop = false;
	private final String TAG = "test";
	private Context context;
	
	public downloadThread(dataBaseHandler dHandler, downloadThreadInfo threadInfo, Context context)
	{
		this.threadInfo = threadInfo;
		this.context = context;
		this.dHandler = dHandler;
	}

	public void stopDownload()
	{
		isStop = true;
	}

	@Override
	public void run()
	{
		Intent intent;
		HttpURLConnection conn = null;
		URL url;
		RandomAccessFile randomFile = null;
		File sdDir;
		File file;
		int length = 0;
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED))
		{
			Log.e(TAG, "Not found sd card!");
			return;
		}

		try
		{
			
//			Log.e(TAG, "THREAD: " + getId());
			int start = threadInfo.getStartPosition() + threadInfo.getProgress();
			length = threadInfo.getEndPotition();
			

			url = new URL(threadInfo.getFileUri());
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(3000);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Range", "bytes=" + start + "-" + length);
			conn.connect();
			
			if (conn.getResponseCode() == HttpStatus.SC_PARTIAL_CONTENT)
			{

				sdDir = Environment.getExternalStorageDirectory();
				file = new File(
						(sdDir.toString() + "/Download/" + threadInfo.getFileName()));
				if (!file.exists())
				{
					File dir = new File(file.getParent());
					dir.mkdirs();
					file.createNewFile();
				}
				randomFile = new RandomAccessFile(file, "rwd");			
				randomFile.setLength(length - start);

				InputStream in = conn.getInputStream();
				randomFile.seek(start);

				byte[] buf = new byte[1024 * 4];
				int len = in.read(buf);

				Long current = System.currentTimeMillis();
				while (len >= 0 && !isStop)
				{
					randomFile.write(buf, 0, len);
					int tmp = threadInfo.getProgress() + len;
					threadInfo.setProgress(tmp);

					tmp = tmp * 100 / length;
					if (System.currentTimeMillis() - current >= 500)
					{
						// Update UI
						current = System.currentTimeMillis();
						intent = new Intent(
								MainActivity.broadcastReceiverIntent);
						intent.putExtra("progress", tmp);
						context.sendBroadcast(intent);
						
//						Log.e(TAG, "Update ID: " + getId() + "Progress: " + tmp);
						
						// Updata DataBase
						dHandler.updateThreadInfo(threadInfo);
					}
					len = in.read(buf);
				}
			}
			else
			{
				Log.e(TAG, "Response Code: " + conn.getResponseCode());
			}
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (isStop)
				{
					// Stop, save status
//					Log.e(TAG,"download stop! progress: " + (threadInfo.getProgress() * 100 / length));
					dHandler.updateThreadInfo(threadInfo);
				}
				else
				{
					if(threadInfo.getProgress() == length)
					{
						intent = new Intent(MainActivity.broadcastReceiverIntent);
						intent.putExtra("progress", 100);
						context.sendBroadcast(intent);
						dHandler.deleteThreadInfo(threadInfo.getFileName(),
								threadInfo.getFileUri());
						Log.e(TAG, "download finish!");
					}
				}			
				if(randomFile != null)
					randomFile.close();
				if(conn != null)
					conn.disconnect();
//				Log.e(TAG, "PID :" + getId() + "finish!");
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
