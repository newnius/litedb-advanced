package com.litedbAdvanced.parser;

import java.util.ArrayList;
import java.util.List;

public class SelectStatement {
	
	public int getTableId(){
		return 0;
	}
	
	public List<String> getSelectedKeys(){
		return new ArrayList<>();
	}
	
	public String getOrderBy(){
		return "id";
	}
	
	public String getWhere(){
		return "1";
	}
	
	public int getOffset(){
		return 0;
	}
	
	public int getCount(){
		return -1;
	}
	
	

}
