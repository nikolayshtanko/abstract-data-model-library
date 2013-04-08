package com.tmbeer.admlsdk.storage;

import com.tmbeer.admlsdk.utils.Log;


/**
 * Base abstract storage with template Key and DataType
 * @author kova_ski
 *
 * @param <K> - Key
 * @param <T> - DataType
 */
public abstract class AbstractStorage<K,T> implements IStorage {
	
	public AbstractStorage() {
		onCreate();
		onCreateMessage();
	}
	
	/** log message on creating storage */
	private void onCreateMessage() {
		Log.msg("Storage \"" + getClass().getSimpleName() + "\" was created");
	}
	
	@Override
	public void onCreate() {
		Log.msg(getClass().getSimpleName() + "|" + "onCreate()");
	}

}
