/**
 * service interface
 */
package com.litedbAdvanced.parser;

import java.io.StringReader;

import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.parser.ParseException;
import net.sf.jsqlparser.statement.Statement;

/**
 * @author zteng
 *
 */
public interface Executor {
	//parse the specific SQL statement and return it 
	public static Statement parser(String sql) throws ParseException
	{
		Statement statement;
		CCJSqlParser parser=new CCJSqlParser(new StringReader(sql.toUpperCase()));
		statement=parser.Statement();
		return statement;
	}
	//execute the SQL statement without return
	public static void execute(Statement statement)
	{
		Implementation implementation=ChooseImp.choose(statement);
		implementation.implement();
	}
}
