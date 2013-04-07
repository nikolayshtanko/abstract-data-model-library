package com.tmbeer.admlsdk.storage;

import android.util.Log;


/**
 * Base abstract storage with template Key and DataType
 * @author kova_ski
 *
 * @param <K> - Key
 * @param <T> - DataType
 */
public abstract class AbstractStorage<K,T> implements IStorage {
	
	// TODO move to utils
	public static final String TAG = "storage";
	
	public AbstractStorage() {
		onCreate();
		onCreateMessage();
	}
	
	/** log message on creating storage */
	private void onCreateMessage() {
		Log.d(TAG, "Storage \"" + getClass().getSimpleName() + "\" was created");
	}
	
	@Override
	public void onCreate() {
		Log.d(TAG, getClass().getSimpleName() + "|" + "onCreate()");
	}

}
