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

//		if (isRecording) {
//			initFile();
//		} else {
//			try {
//				raf.close();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
	};

	public void putData(long currentTimeMillis, byte[] imputData, int length) {
		Log.d(TAG, "length:" + length);
//		try {
//			raf.write(imputData,0,length);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

	};

	

}
