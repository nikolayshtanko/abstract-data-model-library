package com.tmbeer.admlsdk.data;

/**
 * Base abstract data model for any instance with String ID
 * @author kova_ski
 *
 */
public abstract class StringKeyData extends AbstractData<String> {
	
	/**
	 * id = "";
	 */
	public StringKeyData() {
		id = "";
	}

}
