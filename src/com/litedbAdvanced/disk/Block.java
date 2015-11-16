package com.litedbAdvanced.disk;


import java.util.List;

import com.litedbAdvanced.global.Line; 

public class Block {
	
	int BlockNumber; //块号
	List<Line> LineList; //行
	byte[] tableName; //表名
}
