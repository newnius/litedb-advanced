package com.litedbAdvanced.transaction;

import com.litedbAdvanced.util.LiteLogger;

public class SLock {
	private Integer cnt = 0;

	public void lock() {
		synchronized (cnt) {
			cnt++;
		}
		LiteLogger.info(Main.TAG, "SLocked. Cnt = " + cnt);
	}

	public void unlock() {
		synchronized (cnt) {
			cnt--;
		}
		LiteLogger.info(Main.TAG, "unSLocked. Cnt = " + cnt);
	}

	public boolean isLocked() {
		synchronized (cnt) {
			return cnt > 0;
		}
	}

}
