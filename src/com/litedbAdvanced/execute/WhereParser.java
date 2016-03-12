package com.litedbAdvanced.execute;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.litedbAdvanced.util.LiteLogger;
import com.litedbAdvanced.util.Row;
import com.litedbAdvanced.util.TableDef;

public class WhereParser {
	private WhereNode headNode;
	private String whereClause;
	private TableDef tableDef;

	private WhereParser(TableDef tableDef, String whereClause) {
		this.whereClause = whereClause;
		this.tableDef = tableDef;
	}

	public static WhereParser parseWhere(TableDef tableDef, String whereClause) {
		WhereParser tree = new WhereParser(tableDef, whereClause);
		return tree;
	}

	public String getString(Row row) {
		try {
			String query = whereClause;
			query = query.replaceAll("AND", "&&").replaceAll("OR", "||").replaceAll("==", "=").replaceAll("=", "==");
			query = replace(query, row);
			return query;
		} catch (Exception ex) {
			LiteLogger.error(Main.TAG, ex);
			return null;
		}
	}

	public String replace(String str, Row row) {
		LiteLogger.info(Main.TAG, str);
		// LiteLogger.info(Main.TAG, row.toString());
		// String regex =
		// "(?:`(?:\\s*(?<key>[a-z][_a-z0-9]+)\\s*`)|(\\s*(?<key1>[a-z][_a-z0-9]+)\\b(?![\"'].*)\\s*))";
		String regex = "(?<key>\\b[a-zA-Z0-9_]+\\b)";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(str);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			if (m.group() != null) {
				// 先将前面的字符串加上，然后加上此次的字符串
				if (!tableDef.getKeyNames().contains(m.group())) {
					m.appendReplacement(sb, m.group());
				} else {
					m.appendReplacement(sb, row.get(m.group()));
				}
			}
		}
		// 这个表示将剩下的字符加到尾部
		m.appendTail(sb);
		LiteLogger.info(Main.TAG, "replaced string: " + sb.toString());
		return sb.toString();
	}

}
