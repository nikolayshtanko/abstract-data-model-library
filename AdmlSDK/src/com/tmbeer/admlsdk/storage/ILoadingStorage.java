package com.tmbeer.admlsdk.storage;

/**
 * Basic interface for any loading storage
 * @author kova_ski
 *
 */
public interface ILoadingStorage extends IStorage {
	
	/** loading the storage */
	public void loading();

	/** Invoke on complete loading storage */
	public void onLoadingComplete();
	
	/** Invoke on failed loading storage (on getting error) */
	public void onLoadingFailed();

}
