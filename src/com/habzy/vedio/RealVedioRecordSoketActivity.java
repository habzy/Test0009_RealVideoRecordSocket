package com.habzy.vedio;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.media.MediaRecorder;
import android.net.LocalSocket;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.RelativeLayout;

public class RealVedioRecordSoketActivity extends Activity implements Callback {
	private static final String TAG = "RealVedioRecordSoketActivity";
	private static final int MENU_START = 1;
	private static final int MENU_STOP = 2;

	private RelativeLayout camera;
	private MediaRecorder mRecorder;
	private SurfaceView mSurfaceView;
	private File myRecAudioFile;

	private FileOutputStream fout;

	LocalSocket receiver, sender;
	private File dir;
	private String mPath;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		camera = (RelativeLayout) findViewById(R.id.cameraLayout);
		mSurfaceView = (MySurface) findViewById(R.id.Surface);

		SurfaceHolder holder = mSurfaceView.getHolder();
		holder.addCallback(this);
		holder.setFixedSize(400, 300);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		File defaultDir = Environment.getExternalStorageDirectory();
		mPath = defaultDir.getAbsolutePath() + File.separator + "V"
				+ File.separator;

		Log.d(TAG, "path" + mPath);
		// create temporary file
		dir = new File(mPath);
		if (!dir.exists()) {
			dir.mkdir();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onMenuOpened(int, android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d(TAG, "Create menu....");
		menu.add(Menu.NONE, MENU_START, Menu.NONE, R.string.menu_start);
		menu.add(Menu.NONE, MENU_STOP, Menu.NONE, R.string.menu_stop);
		return super.onCreateOptionsMenu(menu);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(TAG, "Click menu....id:" + item.getItemId());
		switch (item.getItemId()) {
		case MENU_START: {
			Log.d(TAG, "MENU_START!");
			// camera.setVisibility(View.VISIBLE);
			initMediaRecorder();
			startMediaRecorder();
			break;
		}
		case MENU_STOP: {
			Log.d(TAG, "MENU_STOP!");
			// camera.setVisibility(View.GONE);
			stopMediaRecorder();
			break;
		}
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public void startMediaRecorder() {
		// begin to write
		try {
			mRecorder.prepare();
			mRecorder.start();
		} catch (IOException exception) {
			// releaseMediaRecorder();
			exception.printStackTrace();
		}
	}

	public void stopMediaRecorder() {
		if (mRecorder != null) {
			mRecorder.stop();
			mRecorder.reset();
			mRecorder.release();
			mRecorder = null;
		}
	}

	public void initMediaRecorder() {
		try {

			mRecorder = new MediaRecorder();
			// mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			// mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
			mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			mRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H263);
			// mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

			// mRecorder.setVideoFrameRate(20);
			{
				mRecorder.setVideoSize(320, 240);
			}
			mRecorder.setPreviewDisplay(mSurfaceView.getHolder().getSurface());

			// myRecAudioFile = File.createTempFile("video", ".mpeg", dir);
			myRecAudioFile = File.createTempFile("video", ".3gp", dir);
			if (myRecAudioFile != null) {
				Log.w(TAG, "file" + myRecAudioFile.getAbsolutePath());
			} else {
				Log.w(TAG, "file create failure");
			}

			// fout = new FileOutputStream(myRecAudioFile.getAbsolutePath());

			// mRecorder.setOutputFile(sender.getFileDescriptor());
			mRecorder.setOutputFile(mPath
					+ SystemClock.currentThreadTimeMillis() + ".3gp");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub

	}
}