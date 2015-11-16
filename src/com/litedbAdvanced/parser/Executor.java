/**
 * service interface
 */
package com.litedbAdvanced.parser;

import java.io.StringReader;

import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.parser.ParseException;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.truncate.Truncate;
import net.sf.jsqlparser.statement.update.Update;

/**
 * @author zteng
 *
 */
public interface Executor {
	//parse the specific SQL statement and return it 
	public static Statement parser(String sql) throws ParseException
	{
		Statement sta;
		CCJSqlParser parser=new CCJSqlParser(new StringReader(sql));
		sta=parser.Statement();
		return sta;
	}
	//execute the SQL statement without return
	public static void execute(Statement statement)
	{
		Implementation implementation=ChooseImp.choose(statement);
		implementation.implement();
	}
}
