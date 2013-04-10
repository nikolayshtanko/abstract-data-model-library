package com.tmbeer.admlsample;

import com.tmbeer.admlsdk.storage.AbstractStorage;
import com.tmbeer.admlsdk.utils.Stat;

public class CarStorage extends AbstractStorage<Integer, CarData> {
	
	public CarStorage() {
		Stat stat = new Stat(this);
		stat.inc();
		stat.inc();
		stat.complete();
	}

}
