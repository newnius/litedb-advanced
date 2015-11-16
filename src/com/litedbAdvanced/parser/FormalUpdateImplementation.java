/**
 * 
 */
package com.litedbAdvanced.parser;

/**
 * @author zteng
 *
 */
public class FormalUpdateImplementation extends UpdateImplementation {

	/**
	 * 
	 */
	//UPDATE mytable SET col1='as', col2=?, col3=565 WHERE o >= 3
	public FormalUpdateImplementation() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "FormalUpdateImplementation [statement=" + statement + "]";
	}

}
