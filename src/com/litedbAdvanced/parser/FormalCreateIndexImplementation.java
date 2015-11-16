/**
 * 
 */
package com.litedbAdvanced.parser;

/**
 * @author zteng
 *
 */
public class FormalCreateIndexImplementation extends CreateIndexImplementation{

	/**
	 * 
	 */
	//CREATE INDEX myindex ON mytab (mycol, mycol2)
	public FormalCreateIndexImplementation() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "FormalCreateIndexImplementation [statement=" + statement + "]";
	}

}
