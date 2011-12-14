package com.habzy.vedio;

public abstract class Consumer implements Runnable  {

	abstract void setRecording(boolean b);

	abstract void putData(long currentTimeMillis, byte[] aa, int i);

}
