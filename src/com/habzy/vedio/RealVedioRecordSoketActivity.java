package com.habzy.vedio;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
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

public class RealVedioRecordSoketActivity extends Activity implements Callback,
		MediaRecorder.OnErrorListener, MediaRecorder.OnInfoListener {
	private static final String TAG = "RealVedioRecordSoketActivity";
	private static final int MENU_START = 1;
	private static final int MENU_STOP = 2;
	private static final int MENU_PLAY = 3;

	private RelativeLayout mCameraLayout;
	// private Camera mCameraDevice;
	private MySurface mPreview;
	// private SurfaceView mShownSurfaceView;

	private MediaRecorder mMediaRecorder;
	private boolean mMediaRecorderRecording = false;
	private MediaPlayer mPlayer;
	private LocalSocket receiver, sender;
	private LocalServerSocket lss;

	// private File myRecFile;
	// private File dir;
	// private String mPath;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mCameraLayout = (RelativeLayout) findViewById(R.id.cameraLayout);
		mPreview = (MySurface) findViewById(R.id.preview);
		// mShownSurfaceView = (SurfaceView) findViewById(R.id.shown);

		SurfaceHolder holder = mPreview.getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		initSocket();

	}

	/**
	 * 
	 */
	private void initSocket() {
		// Habzy: Copy the code from
		// "http://blog.csdn.net/peijiangping1989/article/details/6942585"
		receiver = new LocalSocket();
		try {
			lss = new LocalServerSocket("VideoCamera");
			receiver.connect(new LocalSocketAddress("VideoCamera"));
			receiver.setReceiveBufferSize(500000);
			receiver.setSendBufferSize(500000);
			sender = lss.accept();
			sender.setReceiveBufferSize(500000);
			sender.setSendBufferSize(500000);
		} catch (IOException e) {
			finish();
			return;
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
			// mCameraLayout.setVisibility(View.VISIBLE);
			initializeVideo();
			startVideoRecording();
			break;
		}
		case MENU_STOP: {
			Log.d(TAG, "MENU_STOP!");
			// mCameraLayout.setVisibility(View.GONE);
			stopMediaRecorder();
			break;
		}
		case MENU_PLAY: {
			// playMedia();
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
				// mPlayer.setDisplay(mShownSurfaceView.getHolder());
				// TODO add data source.

				// Play with file which record.
				// mPlayer.setDataSource(myRecFile.getAbsolutePath());

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

	public void stopMediaRecorder() {
		releaseMediaRecorder();
		// if (null != mCameraDevice) {
		// mCameraDevice.stopPreview();
		// mCameraDevice.release();
		// mCameraDevice = null;
		// }
//		consumer.setRecording(false);
	}

	private void releaseMediaRecorder() {
		Log.d(TAG, "Releasing media recorder.");
		if (mMediaRecorder != null) {
			if (mMediaRecorderRecording) {
				if (null != mThread) {
					mThread.interrupt();
				}

				try {
					mMediaRecorder.setOnErrorListener(null);
					mMediaRecorder.setOnInfoListener(null);
					mMediaRecorder.stop();
				} catch (RuntimeException e) {
					Log.e(TAG, "stop fail: " + e.getMessage());
				}
				mMediaRecorderRecording = false;
			}
			mMediaRecorder.reset();
			mMediaRecorder.release();
			mMediaRecorder = null;
		}
		//
		// if (null != mCameraDevice) {
		// mCameraDevice.lock();
		// // mCameraDevice.stopPreview();
		// mCameraDevice.release();
		// mCameraDevice = null;
		// }
	}

	private boolean initializeVideo() {
		try {
			// if (mCameraDevice == null) {
			// int defaultCameraId = 1;
			// int numberOfCameras = Camera.getNumberOfCameras();
			// CameraInfo cameraInfo = new CameraInfo();
			// for (int i = 0; i < numberOfCameras; i++) {
			// Camera.getCameraInfo(i, cameraInfo);
			// if (cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT) {
			// defaultCameraId = i;
			// }
			// }
			// mCameraDevice = Camera.open(defaultCameraId);
			// try {
			// mCameraDevice.setDisplayOrientation(90);
			// } catch (Exception e) {
			// e.printStackTrace();
			// }
			//
			// }

			if (mMediaRecorder == null) {
				mMediaRecorder = new MediaRecorder();
			} else {
				mMediaRecorder.reset();
			}

			// mCameraDevice.unlock();
			// mMediaRecorder.setCamera(mCameraDevice);
			mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
			mMediaRecorder
					.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H263);

			mMediaRecorder.setVideoFrameRate(15);

			mMediaRecorder.setVideoSize(320, 240);
			mMediaRecorder.setOrientationHint(180);
			mMediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());

			mMediaRecorder.setMaxDuration(5000);
			mMediaRecorder.setMaxFileSize(500);
			mMediaRecorder.setOutputFile(sender.getFileDescriptor());

			// Play with record file
			// myRecFile = File.createTempFile("video", ".3gp", dir);
			// if (myRecFile != null) {
			// Log.w(TAG, "file" + myRecFile.getAbsolutePath());
			// } else {
			// Log.w(TAG, "file create failure");
			// }
			// mMediaRecorder.setOutputFile(myRecFile.getAbsolutePath());

			mMediaRecorderRecording = true;
			mMediaRecorder.setOnInfoListener(this);
			mMediaRecorder.setOnErrorListener(this);
			mMediaRecorder.prepare();
			mMediaRecorder.start();
		} catch (IOException exception) {
			releaseMediaRecorder();
			finish();
			return false;
		}
		return true;
	}

	private Consumer consumer;
	private Thread mThread;

	private void startVideoRecording() {
		(mThread = new Thread() {

			public void run() {
				int frame_size = 1024;
				byte[] buffer = new byte[1024 * 64];
				int num, number = 0;
				InputStream fis = null;

				try {
					fis = receiver.getInputStream();
				} catch (IOException e1) {
					return;
				}

				try {
					sleep(50);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}

				// TODO what these code for???
				number = 0;
				releaseMediaRecorder();
				while (true) {
					Log.d(TAG, "ok#########");
					try {
						num = fis.read(buffer, number, frame_size);
						number += num;
						if (num < frame_size) {
							Log.d(TAG, "recoend break");
							break;
						}
					} catch (IOException e) {
						Log.d(TAG, "exception break");
						break;
					}
				}

				initializeVideo();
				number = 0;
				// What for end.
				DataInputStream dis = new DataInputStream(fis);
				initFile();

				byte[] h264sps = { 0x67, 0x42, 0x00, 0x0C, (byte) 0x96, 0x54,
						0x0B, 0x04, (byte) 0xA2 };
				byte[] h264pps = { 0x68, (byte) 0xCE, 0x38, (byte) 0x80 };
				byte[] h264head = { 0, 0, 0, 1 };
				try {
					raf.write(h264head);
					raf.write(h264sps);
					raf.write(h264head);
					raf.write(h264pps);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				while (true) {
					try {
						// 读取每场的长度
						int h264length = dis.readInt();
						number = 0;
						raf.write(h264head);
						while (number < h264length) {
							int lost = h264length - number;
							num = fis.read(buffer, 0,
									frame_size < lost ? frame_size : lost);
							Log.d(TAG, String.format("H264 %d,%d,%d",
									h264length, number, num));
							number += num;
							raf.write(buffer, 0, num);
						}
					} catch (IOException e) {
						break;
					}
				}

//				// Begin to publish data. consumer = new Publisher();
//				consumer.setRecording(true);
//				Thread consumerThread = new Thread((Runnable) consumer);
//				consumerThread.start();
//
//				// TODO what these code for??
//				Log.d(TAG, "dis.read(buffer, 0, 32)");
//				try {
//					dis.read(buffer, 0, 32);
//				} catch (IOException e1) {
//					e1.printStackTrace();
//				} //
//				consumer.putData(System.currentTimeMillis(), buffer, 33); //
//				Log.d(TAG, "init aa[]");
//				byte[] aa = { 0x01, 0x42, (byte) 0x80, 0x0A, (byte) 0xFF,
//						(byte) 0xE1, 0x00, 0x12, 0x67, 0x42, (byte) 0x80, 0x0A,
//						(byte) 0xE9, 0x02, (byte) 0xC1, 0x29, 0x08, 0x00, 0x00,
//						0x1F, 0x40, 0x00, 0x04, (byte) 0xE2, 0x00, 0x20, 0x01,
//						0x00, 0x04, 0x68, (byte) 0xCE, 0x3C, (byte) 0x80 };
//				consumer.putData(System.currentTimeMillis(), aa, 33);
//				// What for end.
//
//				while (mMediaRecorderRecording) {
//					try {
//						int h264length = dis.readInt();
//						number = 0;
//						while (number < h264length) {
//							int lost = h264length - number;
//							num = fis.read(buffer, 0,
//									frame_size < lost ? frame_size : lost);
//							number += num;
//							consumer.putData(System.currentTimeMillis(),
//									buffer, num);
//						}
//					} catch (IOException e) {
//						break;
//					}
//				}
//				consumer.setRecording(false);

			}
		}).start();
	}

	private String mPath;

	private File dir;

	private File myRecFile;

	private RandomAccessFile raf = null;

	private void initFile() {
		File defaultDir = Environment.getExternalStorageDirectory();
		mPath = defaultDir.getAbsolutePath() + File.separator + "V"
				+ File.separator;

		Log.d(TAG, "path" + mPath);
		// create temporary file
		dir = new File(mPath);
		if (!dir.exists()) {
			dir.mkdir();
		}

		// Play with record file
		try {
			myRecFile = File.createTempFile("video", ".3gp", dir);
			raf = new RandomAccessFile(myRecFile, "rw");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (myRecFile != null) {
			Log.w(TAG, "file" + myRecFile.getAbsolutePath());
		} else {
			Log.w(TAG, "file create failure");
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

	@Override
	public void onInfo(MediaRecorder mr, int what, int extra) {
		switch (what) {
		case MediaRecorder.MEDIA_RECORDER_INFO_UNKNOWN: {
			Log.d(TAG, "MEDIA_RECORDER_INFO_UNKNOWN");
			break;
		}
		case MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED: {
			Log.d(TAG, "MEDIA_RECORDER_INFO_MAX_DURATION_REACHED");
			break;
		}
		case MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED: {
			Log.d(TAG, "MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED");
			break;
		}
		}

	}

	@Override
	public void onError(MediaRecorder mr, int what, int extra) {
		if (what == MediaRecorder.MEDIA_RECORDER_ERROR_UNKNOWN) {
			Log.e(TAG, "MEDIA_RECORDER_ERROR_UNKNOWN");
//			consumer.setRecording(false);
			finish();
		}
	}
}