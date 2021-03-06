/*
 * @author Newnius
 * 
 * take control of all the output
 * output log by tag
 * easy to debug
 * */
package com.litedbAdvanced.util;

import java.util.HashSet;
import java.util.Set;

public class LiteLogger {
	private static Set<String> tags = new HashSet<>();

	public static void info(String tag, String str) {
		if (tags.contains(tag)) {
			System.out.println(tag + ": " + str);
		}
	}

	public static void error(String tag, Exception ex) {
		if (tags.contains(tag)) {
			ex.printStackTrace();
		}
	}

	public static void addTag(String tag) {
		synchronized (tags) {
			if (!tags.contains(tag)) {
				tags.add(tag);
			}
		}
		showTags();
	}

	public static void removeTag(String tag) {
		synchronized (tags) {
			if (tags.contains(tag)) {
				tags.remove(tag);
			}
		}
		showTags();
	}

	public static void showTags() {
		String str = "Tags: [";
		boolean isFirstTag = true;
		for (String tag : tags) {
			if (!isFirstTag) {
				str += ",";
			}
			isFirstTag = false;
			str = str + tag;
		}
		str += "]";
		info("LOGGER", str);
	}

}
