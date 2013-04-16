package com.tmbeer.admlsdk.storage;

import com.tmbeer.admlsdk.utils.Log;
import com.tmbeer.admlsdk.utils.Stat;


/**
 * Base abstract storage with template Key and DataType
 * @author kova_ski
 *
 * @param <K> - Key
 * @param <T> - DataType
 */
public abstract class AbstractStorage<K,T> implements IStorage {
	
	/** statistic object for the storage */
	protected Stat mStat = new Stat(this);
	
	public AbstractStorage() {
		onCreate();
		onCreateMessage();
	}
	
	/** log message on creating storage */
	private void onCreateMessage() {
		Log.msg("Storage \"" + mStat.getStorageName() + "\" was created");
	}
	
	@Override
	public void onCreate() {
		Log.msg(mStat.getStorageName() + "|" + "onCreate()");
	}

}
