package com.litedbAdvanced.disk;

<<<<<<< HEAD
import java.io.File;

=======
>>>>>>> newnius/master
import com.litedbAdvanced.global.Line;
import com.litedbAdvanced.global.TableDef;

public class Main {
	
	Main(){
		
		LRUCache cache=new LRUCache(1000);
		
	}
	
	//创建文件并保存数据字典
<<<<<<< HEAD
	public static boolean createTable(byte[] tableName, TableDef tabelDef){
		
		String path;
		File fd;
		
		
		
		
		
	return true;}
	
	
	//删除表相关所属的所有文件
	public static boolean deleteTable(byte[] tableName){return true;}
	
	//根据 lineId 返回行数据
	public static Line loadRow(int lineId){
		return null;}
	
	//更新行数据
	public static boolean updateRow(int lineId, Line updatedRow){return true;}
	
	//删除行数据
	public static boolean deteteRow(int lineId){return true;}
	
	//添加行  
	public static boolean insertRow(Line newRow){return true;}
	
	
=======
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
>>>>>>> newnius/master
	
	public static void main(String[] args){
		
	}
}
