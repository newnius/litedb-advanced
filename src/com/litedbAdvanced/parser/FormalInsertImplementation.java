/**
 * 
 */
package com.litedbAdvanced.parser;

/**
 * @author zteng
 *
 */
public class FormalInsertImplementation extends InsertImplementation {

	/**
	 * 
	 */
	//INSERT INTO mytable (col1, col2, col3) VALUES (?, 'sadfsd', 234)
	public FormalInsertImplementation() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "FormalInsertImplementation [statement=" + statement + "]";
	}

}
