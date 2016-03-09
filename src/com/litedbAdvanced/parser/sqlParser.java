/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.litedbAdvanced.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Newnius
 */
public class sqlParser {

	static String table = null;
	static String[] keys = null;
	static String[] values = null;
	static String whereClause = null;
	static String orderBy = null;
	static boolean desc = false;
	static int offset = 0;
	static int rows = 1000;

	public static int parse(String query) {
		System.out.println(query);
		table = null;
		keys = null;
		values = null;
		whereClause = null;
		orderBy = null;
		desc = false;
		offset = 0;
		rows = 1000;
		if (query.matches("\\s*(?i)insert\\s*.*")) {
			insert(query);
		} else if (query.matches("\\s*(?i)delete\\s*.*")) {
			delete(query);
		} else if (query.matches("\\s*(?i)update\\s*.*")) {
			update(query);
		} else if (query.matches("\\s*(?i)select\\s*.*")) {
			select(query);
			System.out.println("select");
		} else if (query.matches("\\s*(?i)create\\s*.*")) {
			create(query);
		} else if (query.matches("\\s*(?i)drop\\s*.*")) {
			drop(query);
		} else {

		}
		return 0;
	}

	// inSert inTo test (key_0,key_1,key_2)
	// values("newnius","ah","18844546257");
	public static InsertStatement insert(String query) {
		try {
			String regexInsert = "\\s*insert\\s+into\\s+(?<table>\\w+)\\s+(?:\\((?<keys>[^()]+)\\)\\s*)?values\\s*(?:\\((?<values>[^()]+)\\))\\s*;?\\s*$";
			Pattern p = Pattern.compile(regexInsert, Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(query);
			if (m.find()) {
				if (m.group("table") != null) {
					table = m.group("table").toLowerCase();
				} else {
					return null;
				}

				if (m.group("keys") != null) {
					String str = m.group("keys").replaceAll(" ", "").replaceAll("`", "").toLowerCase();
					keys = str.split(",");
				}

				if (m.group("values") != null) {
					String str = m.group("values").replaceAll(" ", "").replaceAll("\"", "").replaceAll("'", "");
					values = str.split(",");
				} else {
					return null;
				}

				if (keys != null && keys.length != values.length) {
					return null;
				}
				check();
				return null;
			}
		} catch (Exception e) {
			return null;
		}

		return null;

	}

	// delete fRom test where key_0=="yjx";
	public static DeleteStatement delete(String query) {
		try {
			String regexDelete = "\\s*delete\\s+from\\s+(?<table>\\w+)\\s+where\\s+(?<whereClause>[^;]+)\\s*;?\\s*$";
			Pattern p = Pattern.compile(regexDelete, Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(query);
			if (m.find()) {

				if (m.group("table") != null) {
					table = m.group("table").toLowerCase();
				}

				if (m.group("whereClause") != null) {
					whereClause = m.group("whereClause");
				}
				check();
				return null;
			}

		} catch (Exception e) {
			return null;
		}
		return null;

	}

	public static UpdateStatement update(String query) {
		try {
			String regexUpdate = "\\s*update\\s+(?<table>\\w+)\\s+set\\s+(?<update>.+)\\s+where\\s+(?<whereClause>[^;]+)\\s*;?\\s*$";
			Pattern p = Pattern.compile(regexUpdate, Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(query);
			while (m.find()) {
				System.out.println(m.group());

				if (m.group("table") != null) {
					table = m.group("table");
				}

				if (m.group("update") != null) {
					String str = m.group("update").replaceAll(" ", "").replaceAll("`", "").replaceAll("\"", "")
							.replaceAll("'", "");
					System.out.println(str);
					String[] tmp = str.split(",");
					System.out.println(tmp.length);
					keys = new String[tmp.length];
					values = new String[tmp.length];
					for (int i = 0; i < tmp.length; i++) {
						keys[i] = new String(tmp[i].substring(0, tmp[i].indexOf("="))).trim().toLowerCase();
						values[i] = new String(tmp[i].substring(tmp[i].indexOf("=") + 1, tmp[i].length())).trim();
					}
				}

				if (m.group("whereClause") != null) {
					whereClause = m.group("whereClause");
				}

				check();
				return null;
			}
		} catch (Exception e) {
			return null;
		}

		return null;

	}

	// select key_0,key_1,key_3 from test where key_0=="yjx" [order by key_1
	// [desc]] [limit [1,]5]
	// select * from test where 1==1 and key_0!="vivian" order by key_0 limit
	// 5,20;
	public static SelectStatement select(String query) {
		System.out.println("select " + query);
		try {
			String regexSelect = "\\s*select\\s+(?<keys>(\\*|(?:\\w+(?:\\s*,\\s*\\w+)*)))\\s+from\\s+(?<table>\\w+)\\s+where\\s+(?<whereClause>.*)(?:\\s*order\\s+by\\s+(?<orderBy>\\w+)\\s*(?<desc>desc)?)?(?:\\s+limit(?:\\s+(?<offset>\\d+)\\s*,\\s*)?(?<rows>\\d+))?\\s*;?\\s*";
			if (query.contains("order")) {
				regexSelect = "\\s*select\\s+(?<keys>(\\*|(?:\\w+(?:\\s*,\\s*\\w+)*)))\\s+from\\s+(?<table>\\w+)\\s+where\\s+(?<whereClause>.*)(?:\\s+order\\s+by\\s+(?<orderBy>\\w+)\\s*(?<desc>desc)?)(?:\\s+limit(?:\\s+(?<offset>\\d+)\\s*,\\s*)?\\s*(?<rows>\\d+))?\\s*;?\\s*";
			} else if (query.contains("limit")) {
				regexSelect = "\\s*select\\s+(?<keys>(\\*|(?:\\w+(?:\\s*,\\s*\\w+)*)))\\s+from\\s+(?<table>\\w+)\\s+where\\s+(?<whereClause>.*)(?:\\s*order\\s+by\\s+(?<orderBy>\\w+)\\s*(?<desc>desc)?)?(?:\\s+limit(?:\\s+(?<offset>\\d+)\\s*,\\s*)?\\s*(?<rows>\\d+))\\s*;?\\s*";
			}

			System.out.println(regexSelect);
			Pattern p = Pattern.compile(regexSelect, Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(query);
			if (m.find()) {

				if (m.group("table") != null) {
					table = m.group("table");
				}

				if (m.group("keys") != null) {
					String str = m.group("keys").replaceAll(" ", "").replaceAll("`", "");
					keys = str.split(",");
				}

				if (m.group("whereClause") != null) {
					whereClause = m.group("whereClause");
				}

				if (m.group("orderBy") != null) {
					orderBy = m.group("orderBy");
				}

				if (m.group("desc") != null) {
					desc = true;
				}

				if (m.group("offset") != null) {
					offset = Integer.parseInt(m.group("offset").replaceAll(" ", ""));
				}

				if (m.group("rows") != null) {
					rows = Integer.parseInt(m.group("rows").replaceAll(" ", ""));
				}
				check();
				SelectStatement stat = new SelectStatement(table, keys, whereClause, orderBy, offset, rows);
				
				return null;
			} else {
				System.out.println("not match");
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static int create(String query) {
		String regexCreate = "\\s*create\\s+table\\s*(?<table>)\\s*\\((?<str>.*)\\)[; ]*";
		Pattern p = Pattern.compile(regexCreate, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(query);
		if (m.find()) {

			if (m.group("table") != null) {
				table = m.group("table");
			}

			if (m.group("str") != null) {
				// String[] str = m.group("str").split(",");

			}

		}

		check();

		return 0;
	}

	public static int drop(String query) {
		try {
			String regexDrop = "\\s*drop\\s+table\\s+(?<table>\\w+)\\s*;?\\s*";
			Pattern p = Pattern.compile(regexDrop, Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(query);
			if (m.find()) {

				if (m.group("table") != null) {
					table = m.group("table");
				} else {
					return 0;
				}
				return 0;
			} else {
				return 0;
			}
		} catch (Exception e) {
			return 0;
		}

	}

	private static void check() {
		if (table != null) {
			System.out.println("table:\"" + table + "\"");
		}
		if (keys != null) {
			for (String str : keys) {
				System.out.println("---key:\"" + str + "\"");
			}
		}
		if (values != null) {
			for (String str : values) {
				System.out.println("---value:\"" + str + "\"");
			}
		}
		if (whereClause != null) {
			System.out.println("where:\"" + whereClause + "\"");
		}
		if (orderBy != null) {
			System.out.println("table:\"" + orderBy + "\"");
		}

		System.out.println("desc:\"" + desc + "\"");
		System.out.println("offset:\"" + offset + "\"");
		System.out.println("rows:\"" + rows + "\"");
	}
}
