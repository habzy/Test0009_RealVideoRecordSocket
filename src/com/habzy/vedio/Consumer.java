package com.habzy.vedio;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import android.os.Environment;
import android.util.Log;

public abstract class Consumer implements Runnable {
	private static final String TAG = "Consumer";
	protected boolean mIsRecording = false;

	public void setRecording(boolean isRecording) {
		Log.d(TAG, "isRecording:" + isRecording);
		if (mIsRecording == isRecording) {
			return;
		}
		mIsRecording = isRecording;

		if (isRecording) {
			initFile();
		} else {
			try {
				raf.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	public void putData(long currentTimeMillis, byte[] imputData, int length) {
		Log.d(TAG, "length:" + length);
		try {
			raf.write(imputData);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	};

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

}
