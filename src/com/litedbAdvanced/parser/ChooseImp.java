/**
 * create right implementation object for specific statement
 */
package com.litedbAdvanced.parser;

import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.alter.Alter;
import net.sf.jsqlparser.statement.create.index.CreateIndex;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.create.view.CreateView;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;

/**
 * @author zteng
 *
 */
public class ChooseImp {
	static Implementation choose(Statement statement)
	{
		Implementation implementation = null;
		ChooseImp chooseImp = new ChooseImp();
		if(statement instanceof Select)
		{
			implementation = chooseImp.chooseFromSelect((Select)statement);
		}
		else if(statement instanceof Insert)
		{
			implementation = chooseImp.chooseFromInsert((Insert)statement);
		}
		else if(statement instanceof Update)
		{
			implementation = chooseImp.chooseFromUpdate((Update)statement);
		}
		else if(statement instanceof Delete)
		{
			implementation = chooseImp.chooseFromDelete((Delete)statement);
		}
		else if(statement instanceof Drop)
		{
			implementation = chooseImp.chooseFromDrop((Drop)statement);
		}
		else if(statement instanceof Alter)
		{
			implementation = chooseImp.chooseFromAlter((Alter)statement);
		}
		else if(statement instanceof CreateIndex)
		{
			implementation = chooseImp.chooseFromCreateIndex((CreateIndex)statement);
		}
		else if(statement instanceof CreateTable)
		{
			implementation = chooseImp.chooseFromCreateTable((CreateTable)statement);
		}
		else if(statement instanceof CreateView)
		{
			implementation = chooseImp.chooseFromCreateView((CreateView)statement);
		}
		return implementation;
	}
	private Implementation chooseFromSelect(Select statement)
	{
		SelectImplementation implementation = new FormalSelectImplementation();
		return implementation;
	}
	private Implementation chooseFromInsert(Insert statement)
	{
		InsertImplementation implementation = new FormalInsertImplementation();
		return implementation;
	}
	private Implementation chooseFromUpdate(Update statement)
	{
		UpdateImplementation implementation = new FormalUpdateImplementation();
		return implementation;
	}
	private Implementation chooseFromDelete(Delete statement)
	{
		DeleteImplementation implementation = new FormalDeleteImplementation();
		return implementation;
	}
	private Implementation chooseFromDrop(Drop statement)
	{
		DropImplementation implementation = new FormalDropImplementation();
		return implementation;
	}
	private Implementation chooseFromAlter(Alter statement)
	{
		AlterImplementation implementation = new AlterImplementation();
		return implementation;
	}
	private Implementation chooseFromCreateIndex(CreateIndex statement)
	{
		CreateIndexImplementation implementation=new FormalCreateIndexImplementation();
		return implementation;
	}	
	private Implementation chooseFromCreateTable(CreateTable statement)
	{
		CreateTableImplementation implementation = new FormalCreateTableImplementation();
		return implementation;
	}	
	private Implementation chooseFromCreateView(CreateView statement)
	{
		CreateViewImplementation implementation = new FormalCreateViewImplementation();
		return implementation;
	}
}
