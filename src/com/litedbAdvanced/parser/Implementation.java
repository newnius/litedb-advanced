/**
 * basic class to implement SQL statement
 */
package com.litedbAdvanced.parser;

import net.sf.jsqlparser.statement.Statement;

/**
 * @author zteng
 *
 */
public class Implementation {
	Statement statement;
	
	public void setStatement(Statement statement) {
		this.statement = statement;
	}

	@Override
	public String toString() {
		return "Implementation [statement=" + statement + "]";
	}

	public void implement(){}
}
