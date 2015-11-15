package com.litedbAdvanced.disk;

import com.litedbAdvanced.global.Line;
import com.litedbAdvanced.global.TableDef;

public class Main {
	
	//创建文件并保存数据字典
	static boolean createTable(byte[] tableName, TableDef tabelDef){
		return false;
	}
	
	//删除表相关所属的所有文件
	static boolean deleteTable(byte[] tableName){
		return false;
	}
	
	//根据 lineId 返回行数据
	static Line loadRow(int lineId){
		return null;
	}
	
	//更新行数据
	static boolean updateRow(int lineId, Line updatedRow){
		return false;
	}
	
	//删除行数据
	static boolean deteteRow(int lineId){
		return false;
	}
	
	//添加行  
	static boolean insertRow(Line newRow){
		return false;
	}
	
	public static void main(String[] args){
		
	}
}
