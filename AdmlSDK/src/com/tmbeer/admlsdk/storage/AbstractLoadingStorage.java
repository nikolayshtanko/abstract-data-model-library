package com.tmbeer.admlsdk.storage;

/**
 * Base abstract loadable storage with template Key and DataType
 * 
 * @author kova_ski
 *
 * @param <K> - Key
 * @param <T> - DataType
 */
public abstract class AbstractLoadingStorage<K, T> extends AbstractStorage<K, T> implements ILoadingStorage {
	
	public AbstractLoadingStorage() {
		
	}
	
}
