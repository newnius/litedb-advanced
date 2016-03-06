package com.litedbAdvanced.transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.litedbAdvanced.util.LiteLogger;

public class LockManager {
	private static List<SLock> SLocks;
	private static List<XLock> XLocks;
	private static Map<Long, SLock> occupiedSLocks;
	private static Map<Long, XLock> occupiedXLocks;

	public static boolean init(int SLockNum, int XLockNum) {
		SLocks = new ArrayList<SLock>();
		XLocks = new ArrayList<XLock>();
		occupiedSLocks = new HashMap<>();
		occupiedXLocks = new HashMap<>();
		for (int i = 0; i < SLockNum; i++)
			SLocks.add(new SLock());
		for (int i = 0; i < XLockNum; i++)
			XLocks.add(new XLock());
		LiteLogger.info(Main.TAG, "Created " + SLocks.size() + " SLock and " + XLocks.size() + " XLock.");
		return true;
	}

	public static void SLockRID(long rid) {
		try {
			while (isXLocked(rid))
				synchronized (occupiedXLocks) {
					occupiedXLocks.wait();
				}

			SLock SLock;
			synchronized (occupiedSLocks) {
				SLock = occupiedSLocks.get(rid);
			}

			if (SLock == null) {
				int sum = 0;
				while (sum == 0) {
					synchronized (SLocks) {
						sum = SLocks.size();
						if (sum != 0)
							SLock = SLocks.remove(0);
						else
							SLocks.wait();
					}
					if (sum != 0)
						synchronized (occupiedSLocks) {
							occupiedSLocks.put(rid, SLock);
						}
				}
			}
			SLock.lock();
		} catch (Exception ex) {
			LiteLogger.info(Main.TAG, "ERROR when SLock " + rid);
			LiteLogger.error(Main.TAG, ex);
		}

	}

	public static void unSLockRID(long rid) {
		try {
			SLock SLock = null;
			synchronized (occupiedSLocks) {
				SLock = occupiedSLocks.get(rid);
				if (SLock != null) {
					SLock.unlock();
					if (!SLock.isLocked()) {
						synchronized (occupiedSLocks) {
							occupiedSLocks.remove(rid);
							occupiedSLocks.notify();
						}
						synchronized (SLocks) {
							SLocks.add(SLock);
							SLocks.notify();
						}
					} else {
						LiteLogger.info(Main.TAG, rid + " is stilled SLocked.");
					}
				} else {
					LiteLogger.info(Main.TAG, rid + " is not SLocked.");
				}
			}

		} catch (Exception ex) {
			LiteLogger.info(Main.TAG, "ERROR when unSLock " + rid);
			LiteLogger.error(Main.TAG, ex);
		}
	}

	public static void XLockRID(long rid) {
		try {
			while (isSLocked(rid))
				synchronized (occupiedSLocks) {
					occupiedSLocks.wait();
				}
			boolean locked = false;
			while (!locked) {
				XLock XLock = null;
				synchronized (occupiedXLocks) {
					XLock = occupiedXLocks.get(rid);
				}

				if (XLock == null) {
					int sum = 0;
					while (sum == 0) {
						synchronized (XLocks) {
							sum = XLocks.size();
							if (sum != 0)
								XLock = XLocks.remove(0);
							else
								XLocks.wait();
						}
						if (sum != 0)
							synchronized (occupiedXLocks) {
								occupiedXLocks.put(rid, XLock);
								XLock.lock();
								locked = true;
							}
					}
				} else {
					synchronized (occupiedXLocks) {
						occupiedXLocks.wait();
					}
				}
			}
			LiteLogger.info(Main.TAG, rid + " is XLocked.");
		} catch (Exception ex) {
			LiteLogger.info(Main.TAG, "ERROR when XLock " + rid);
			LiteLogger.error(Main.TAG, ex);
		}
	}

	public static void unXLockRID(long rid) {
		try {
			XLock XLock = null;
			synchronized (occupiedXLocks) {
				XLock = occupiedXLocks.get(rid);
				if (XLock != null) {
					XLock.unlock();
					LiteLogger.info(Main.TAG, rid + " is unXLocked.");
					if (!XLock.isLocked()) {
						synchronized (occupiedXLocks) {
							occupiedXLocks.remove(rid);
							occupiedXLocks.notify();
						}
						synchronized (XLocks) {
							XLocks.notify();
						}
					}
				}
			}

			if (XLock != null && !XLock.isLocked()) {
				synchronized (XLocks) {
					XLocks.add(XLock);
				}
			}

		} catch (Exception ex) {
			LiteLogger.info(Main.TAG, "ERROR when unXLock " + rid);
			LiteLogger.error(Main.TAG, ex);
		}

	}

	private static boolean isSLocked(long rid) {
		SLock SLock = null;
		synchronized (occupiedSLocks) {
			SLock = occupiedSLocks.get(rid);
		}

		return SLock != null && SLock.isLocked();
	}

	private static boolean isXLocked(long rid) {
		XLock XLock = null;
		synchronized (occupiedXLocks) {
			XLock = occupiedXLocks.get(rid);
		}
		return XLock != null && XLock.isLocked();
	}

}
