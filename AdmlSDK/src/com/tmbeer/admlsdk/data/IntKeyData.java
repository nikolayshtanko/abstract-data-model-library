package com.tmbeer.admlsdk.data;

/**
 * Base abstract data model for any instance with Integer ID
 * @author kova_ski
 *
 */
public abstract class IntKeyData extends AbstractData<Integer> {
	
	/**
	 * id = 0;
	 */
	public IntKeyData() {
		id = 0;
	}

}
