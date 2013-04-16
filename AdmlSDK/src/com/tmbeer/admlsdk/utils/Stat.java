package com.tmbeer.admlsdk.utils;


import com.tmbeer.admlsdk.storage.IStorage;

/**
 * Class for compile information about read row amount for IStorage instance 
 * @author kova_ski
 *
 */
public class Stat {
	
	private static final String MESSAGE = "Storage \"%s\" loaded with %d rows for %d seconds";
	private int rowCount;
	private long time;
	private String storageName = "";
	
	public Stat(final IStorage storage) {
		storageName = storage.getClass().getSimpleName();
	}
	
	/**
	 * the name of this Storage
	 */
	public String getStorageName() {
		return storageName;
	}
	
	/**
	 * Starting time calculation
	 */
	public void start() {
		time = Utils.currentTime();
	}
	
	/**
	 * increment row's amount for the storage 
	 */
	public void inc() {
		rowCount++;
	}
	
	/**
	 * complete calculation and print the result
	 */
	public void stop() {
		Log.msg(String.format(MESSAGE, storageName, rowCount, (Utils.currentTime() - time)));
	}

}
