package com.nlt.testdownload;

import java.io.File;

import com.nlt.baseObject.downloadThreadInfo;
import com.nlt.db.dataBaseHandler;
import com.nlt.db.dataBaseHelper;

import android.os.Bundle;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.RemoteViews.RemoteView;

public class MainActivity extends Activity
{
	private ProgressBar pg;
	private Button play;
	private Button delete;
	private boolean isPlay = false;
	private Context context;
	
	public static String broadcastReceiverIntent = "broadcastReceiverIntent";
	private BroadcastReceiver br;
	
	private final String TAG = "test";
	private NotificationManager mNotificationManager;
	private Notification.Builder nfBuilder;
	private downloadThreadInfo downloadFile;
	
	private Button test;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Log.i(TAG, "Activity Oncreate");
		
		if(downloadFile == null)
		{
			downloadFile = new downloadThreadInfo(0, "imooc.apk", "http://www.imooc.com/mobile/imooc.apk ", 0, 0, 0);
		}
		
		findView();
		if(br == null)
		{
			br = new BroadcastReceiver()
			{	
				@Override
				public void onReceive(Context context, Intent intent)
				{
					// TODO Auto-generated method stub
					int progress = intent.getIntExtra("progress", 0);
					pg.setProgress(progress);
					if(nfBuilder == null)
					{
						createNotification(0);
					}
					nfBuilder.setProgress(100, progress, false);
					mNotificationManager.notify(0, nfBuilder.build());
					if(progress == 100)
					{
						isPlay = false;
						play.setText(R.string.start);
					}
				}
			};
			IntentFilter filter = new IntentFilter();  
			filter.addAction(broadcastReceiverIntent);
			registerReceiver(br, filter);		
		}
	
	}

	private void findView()
	{
		context = this;
		play = (Button) findViewById(R.id.start_stop_button);
		play.setOnClickListener(new OnClickListener()
		{	
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				Log.e(TAG, "isPlay: " + isPlay);
				if(isPlay)
				{
					isPlay = false;
					play.setText(R.string.start);				
					// stop download
					Intent in = new Intent(MainActivity.this, downloadService.class);
					in.setAction(downloadService.stopIntent);
					in.putExtra("fileInfo", downloadFile);
					context.startService(in);	
				}
				else
				{
					isPlay = true;
					play.setText(R.string.stop);
					// start download
					Intent in = new Intent(MainActivity.this, downloadService.class);
					in.setAction(downloadService.startIntent);
					in.putExtra("fileInfo", downloadFile);
					context.startService(in);
					createNotification(0);			
				}
			}
		});

		pg = (ProgressBar) findViewById(R.id.downloadProgress);
		
	}
	
	
	
	private void createNotification(int id)
	{
		if(nfBuilder == null)
		{
			nfBuilder = new Notification.Builder(this);
		}
		Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
		
		Intent in = new Intent(this, MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 0, in, PendingIntent.FLAG_UPDATE_CURRENT);
		
		nfBuilder
		.setTicker("New message")
		.setContentTitle("Download Task")
		.setContentText("imooc.apk")
		.setLargeIcon(bm)
		.setSmallIcon(R.drawable.ic_launcher)
		.setAutoCancel(true)
		.setSmallIcon(R.drawable.ic_launcher)
		.setContentIntent(pendingIntent)
		.setProgress(100, 0, false);

		
		if(mNotificationManager == null)
			mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(id, nfBuilder.build());
	}
	
	private void cancelNotification(int id)
	{
//		Log.e(TAG, "cancel Notification");
		if(mNotificationManager == null)
			return;
		
		if(id < 0)
			mNotificationManager.cancelAll();
		else
			mNotificationManager.cancel(id);
	}
	
	@Override
	protected void onStart()
	{
		// TODO Auto-generated method stub
		super.onStart();
		Log.i(TAG, "Activity onStart");
	}

	@Override
	protected void onRestart()
	{
		// TODO Auto-generated method stub
		super.onRestart();
		Log.i(TAG, "Activity onRestart");
	}

	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		Log.i(TAG, "Activity onResume");
	}

	@Override
	protected void onPause()
	{
		// TODO Auto-generated method stub
		super.onPause();
		Log.i(TAG, "Activity onPause");
	}

	@Override
	protected void onStop()
	{
		// TODO Auto-generated method stub
		super.onStop();
		Log.i(TAG, "Activity onStop");
	}

	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.i(TAG, "Activity onDestroy");
		
		Intent in = new Intent(MainActivity.this, downloadService.class);
		in.setAction(downloadService.stopIntent);
		in.putExtra("fileInfo", downloadFile);
		context.startService(in);
		
		unregisterReceiver(br);
		cancelNotification(-1);
	}

	
}
