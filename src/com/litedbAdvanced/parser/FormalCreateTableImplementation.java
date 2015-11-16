/**
 * 
 */
package com.litedbAdvanced.parser;

/**
 * @author zteng
 *
 */
public class FormalCreateTableImplementation extends CreateTableImplementation {

	/**
	 * 
	 */
	//create table A (test verchar(50))
	public FormalCreateTableImplementation() {
		
	}

	@Override
	public String toString() {
		return "FormalCreateTableImplementation [statement=" + statement + "]";
	}

}
