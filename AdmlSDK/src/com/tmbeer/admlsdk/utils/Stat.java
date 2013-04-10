package com.tmbeer.admlsdk.utils;

import com.tmbeer.admlsdk.storage.IStorage;

/**
 * Class for compile information about read row amount
 * @author kova_ski
 *
 */
public class Stat {
	
	private static final String MESSAGE = "Storage \"%s\" loading with %d rows";
	private int rowCount;
	private String storageName = "";
	
	public Stat(final IStorage storage) {
		storageName = storage.getClass().getSimpleName();
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
	public void complete() {
		Log.msg(String.format(MESSAGE, storageName, rowCount));
	}

}
