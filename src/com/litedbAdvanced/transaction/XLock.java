package com.litedbAdvanced.transaction;

public class XLock {
	private Boolean used = false;

	public void lock() {
		synchronized (used) {
			used = true;
		}
	}

	public void unlock() {
		synchronized (used) {
			used = false;
		}
	}

	public boolean isLocked() {
		synchronized (used) {
			return used;
		}
	}
}
