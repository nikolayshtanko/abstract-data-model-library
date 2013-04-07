package com.tmbeer.admlsdk.data;

/**
 * Base abstract data model for any instance with Long ID
 * @author kova_ski
 *
 */
public abstract class LongKeyData extends AbstractData<Long> {
	
	/**
	 * id = 0l;
	 */
	public LongKeyData() {
		id = 0l;
	}

}
