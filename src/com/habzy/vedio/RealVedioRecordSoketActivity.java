package com.habzy.vedio;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.LocalSocket;
import android.os.Bundle;
import android.os.Environment;
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
	private static final int MENU_PLAY = 3;

	private RelativeLayout mCameraLayout;
	private Camera mCameraDevice;
	private MediaRecorder mRecorder;
	private MySurface mPreview;
	private SurfaceView mShownSurfaceView;
	private File myRecFile;

	private MediaPlayer mPlayer;
	private LocalSocket mSokectBuffer;

	private File dir;
	private String mPath;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mCameraLayout = (RelativeLayout) findViewById(R.id.cameraLayout);
		mPreview = (MySurface) findViewById(R.id.preview);
		mShownSurfaceView = (SurfaceView) findViewById(R.id.shown);

		SurfaceHolder holder = mShownSurfaceView.getHolder();
		holder.addCallback(this);
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
		
		mSokectBuffer = new LocalSocket();
		
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
		menu.add(Menu.NONE, MENU_PLAY, Menu.NONE, R.string.menu_play);
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
			mCameraLayout.setVisibility(View.VISIBLE);
			initMediaRecorder();
			startMediaRecorder();
			break;
		}
		case MENU_STOP: {
			Log.d(TAG, "MENU_STOP!");
			mCameraLayout.setVisibility(View.GONE);
			stopMediaRecorder();
			break;
		}
		case MENU_PLAY: {
			playMedia();
		}
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void playMedia() {
		mCameraLayout.setVisibility(View.GONE);
		if (null == mPlayer) {
			try {
				mPlayer = new MediaPlayer();
				mPlayer.setDisplay(mShownSurfaceView.getHolder());
//				mPlayer.setDataSource(mSokectBuffer.getFileDescriptor());
				mPlayer.setDataSource(myRecFile.getAbsolutePath());
				mPlayer.prepare();

				mPlayer.start();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	public void startMediaRecorder() {
		// begin to write
		try {
			mRecorder.prepare();
			mRecorder.start();
		} catch (IOException exception) {
			mCameraDevice.lock();
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
			mCameraDevice.stopPreview();
			mCameraDevice.release();
			mCameraDevice = null;
		}

	}

	public void initMediaRecorder() {
		mCameraDevice = mPreview.getCamera();
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

			myRecFile = File.createTempFile("video", ".3gp", dir);
			if (myRecFile != null) {
				Log.w(TAG, "file" + myRecFile.getAbsolutePath());
			} else {
				Log.w(TAG, "file create failure");
			}
			mRecorder.setVideoFrameRate(15);

			mRecorder.setVideoSize(320, 240);
			mRecorder.setOrientationHint(180);
			mRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());

			// fout = new FileOutputStream(myRecAudioFile.getAbsolutePath());

//			 mRecorder.setOutputFile(mSokectBuffer.getFileDescriptor());
			mRecorder.setOutputFile(myRecFile.getAbsolutePath());
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