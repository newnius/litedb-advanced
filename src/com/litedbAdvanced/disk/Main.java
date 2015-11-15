package com.litedbAdvanced.disk;

public class Main {
	
	//创建文件并保存数据字典
	static boolean createTable(byte[] tableName, TableDef tabelDef){}
	
	//删除表相关所属的所有文件
	static boolean deleteTable(byte[] tableName){}
	
	//根据 lineId 返回行数据
	static Line loadRow(int lineId){}
	
	//更新行数据
	static boolean updateRow(int lineId, Line updatedRow){}
	
	//删除行数据
	static boolean deteteRow(int lineId){}
	
	//添加行  
	static boolean insertRow(Line newRow){}
	
	public static void main(String[] args){
		
	}
}
