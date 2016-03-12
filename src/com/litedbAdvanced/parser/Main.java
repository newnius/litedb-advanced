package com.litedbAdvanced.parser;

import java.io.StringReader;

import com.litedbAdvanced.util.LiteLogger;

import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.statement.Statement;

public class Main {
	public static final String TAG = "PARSER";

	public static boolean init() {
		 test();
		LiteLogger.info(Main.TAG, "started");
		return true;
	}

	public static boolean close() {
		LiteLogger.info(Main.TAG, "closed");
		return true;
	}

	public static Statement parse(String sql) {
		try {
			CCJSqlParser sqlParser = new CCJSqlParser(new StringReader(sql));
			Statement statement = sqlParser.Statement();
			return statement;
		} catch (Exception ex) {
			LiteLogger.error(Main.TAG, ex);
			return null;
		}
	}

	private static void test() {
		String[] sql = new String[] {
				//"create table tabl_a (cola int (11) auto_increment, colb varchar (20), primary key(cola), index tabl_a(colb))",
				//"select cola,colb from table_a,table_b where a>=b and c<=d group by e,f having e<=123 or f>='456'",
				//"select cola,colb from table_a where a>=b and c<=d  order by cola desc limit 1",
				//"create table tabl_a (cola int (11) auto_increment, colb varchar (20),primary key (cola), foreign key (colb) references table_b(cola))",
				//"create table table_a as select a,b from c,d", "delete from table_a where a = 1 and b = 1",
				//"drop index index_a cascade", "drop table table_a", "insert into table_a(col1,col2) value(123,'456')",
				//"insert into table_a select cola,colb from table_b", 
				//"insert into test values(123,'abc', 13)",
				//"insert into test(cola, colb) values(123,'abc')",
				"update table_a set cola = '123', colb='da' where colb = 456", 
				//"create index ind ON tablea (col1,col2)",
				//"create view view_a as select cola,colb from table_a,table_b" 
				};

		SqlParser parser = new SqlParser();
		for (int i = 0; i < sql.length; ++i) {
			System.out.println(sql[i]);
			parser.parser(sql[i]);
			System.out.println();
		}
	}

}
