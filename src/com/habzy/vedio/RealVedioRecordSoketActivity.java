package com.habzy.vedio;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
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

	private RelativeLayout mCameraLayout;
	private Camera mCameraDevice;
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

		mCameraLayout = (RelativeLayout) findViewById(R.id.cameraLayout);
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
		if (null != mCameraDevice) {
			mCameraDevice.lock();
			mCameraDevice.release();
			mCameraDevice = null;
		}
	}

	public void initMediaRecorder() {
		try {
			if (mCameraDevice == null) {
				int defaultCameraId = 1;
				int numberOfCameras = Camera.getNumberOfCameras();
				CameraInfo cameraInfo = new CameraInfo();
				for (int i = 0; i < numberOfCameras; i++) {
					Camera.getCameraInfo(i, cameraInfo);
					if (cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT) {
						defaultCameraId = i;
					}
				}

				mCameraDevice = Camera.open(defaultCameraId);
				// try {
				// mCameraDevice.setPreviewDisplay(mSurfaceView.getHolder());
				// } catch (IOException e) {
				// e.printStackTrace();
				// }
				//
				try {
					mCameraDevice.setDisplayOrientation(90);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

			mRecorder = new MediaRecorder();
			mCameraDevice.unlock();
			mRecorder.setCamera(mCameraDevice);
			mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
			mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			mRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H263);

			myRecAudioFile = File.createTempFile("video", ".3gp", dir);
			if (myRecAudioFile != null) {
				Log.w(TAG, "file" + myRecAudioFile.getAbsolutePath());
			} else {
				Log.w(TAG, "file create failure");
			}
			mRecorder.setVideoFrameRate(15);

			// fout = new FileOutputStream(myRecAudioFile.getAbsolutePath());

			// mRecorder.setOutputFile(sender.getFileDescriptor());
			mRecorder.setOutputFile(myRecAudioFile.getAbsolutePath());
			mRecorder.setVideoSize(320, 240);
			mRecorder.setPreviewDisplay(mSurfaceView.getHolder().getSurface());
			mRecorder.setOrientationHint(180);
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