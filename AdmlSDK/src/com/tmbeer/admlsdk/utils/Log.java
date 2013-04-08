package com.tmbeer.admlsdk.utils;


/**
 * Simple logger for message
 * @author kova_ski
 *
 */
public final class Log {
	
	// -----------------------------------------------
	// === anonymous type, static method and field ===
	// -----------------------------------------------
	
	/**
	 * Type of Level Log message
	 * @author kova_ski
	 *
	 */
	public enum LogType {
		DEBUG,
		ERROR;
	}
	
	public static final String TAG = "adml-storage";
	
	public static final void msg(final String message) {
		android.util.Log.d(TAG, message);
	}
	
	// -------------------------------------
	// === the instance method and field ===
	// -------------------------------------
	
	private LogType logType = LogType.DEBUG;
	
	public Log(final LogType logType) {
		this.logType = logType;
	}
	
	public void log(final String msg) {
		switch (logType) {
			case ERROR:
				android.util.Log.e(TAG, msg);
				break;
			default:
				android.util.Log.d(TAG, msg);
		}
		
	}

}
