package com.litedbAdvanced.parser;

import java.io.StringReader;
import java.util.Iterator;
import java.util.List;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.parser.ParseException;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.create.index.CreateIndex;
import net.sf.jsqlparser.statement.create.table.ColDataType;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.create.table.Index;
import net.sf.jsqlparser.statement.create.view.CreateView;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.update.Update;

/**
 * @author zteng
 *
 */
public class SqlParser {

	/**
	 * @param args
	 */
	CCJSqlParser sqlParser;

	public void parser(String sql) {
		System.out.println("SqlParser: void parser(String sql)");
		sqlParser = new CCJSqlParser(new StringReader(sql));
		try {
			Statement statement = sqlParser.Statement();
			if (statement instanceof Select) {
				parser_select((Select) statement);
			} else if (statement instanceof Delete) {
				parser_delete((Delete) statement);
			} else if (statement instanceof Drop) {
				parser_drop((Drop) statement);
			} else if (statement instanceof Insert) {
				parser_insert((Insert) statement);
			} else if (statement instanceof Update) {
				parser_update((Update) statement);
			} else if (statement instanceof CreateTable) {
				parser_createTable((CreateTable) statement);
			} else if (statement instanceof CreateIndex) {
				parser_createIndex((CreateIndex) statement);
			} else if (statement instanceof CreateView) {
				parser_createView((CreateView) statement);
			}
		} catch (ParseException e) {

			e.printStackTrace();
		}
	}

	private void parser_createView(CreateView createView) {
		System.out.println("view name: " + createView.getView());
		List<String> cols = createView.getColumnNames();
		if (cols != null) {
			System.out.print("columns: ");
			Iterator<String> it = cols.iterator();
			while (it.hasNext())
				System.out.print(it.next() + " ");
		}
		SelectBody selectbody = createView.getSelectBody();
		PlainSelect plainSelect = (PlainSelect) selectbody;
		// selectItem
		List<SelectItem> selectItem = plainSelect.getSelectItems();
		if (selectItem != null) {
			Iterator<SelectItem> it = selectItem.iterator();
			System.out.print("columns:");
			while (it.hasNext()) {
				System.out.print(it.next() + " ");
			}
		}
		System.out.println();
		// FromItem
		FromItem fromItem = plainSelect.getFromItem();
		System.out.println("from:" + fromItem);
		// where
		Expression where = plainSelect.getWhere();
		System.out.println("where:" + where);
		// groupByColumnReferences
		List<Expression> groupByColumnReferences = plainSelect.getGroupByColumnReferences();
		if (groupByColumnReferences != null) {
			System.out.print("groupBy:");
			Iterator<Expression> it = groupByColumnReferences.iterator();
			while (it.hasNext()) {
				System.out.print(it.next() + " ");
			}
			System.out.println();
		}
		// orderByElements
		List<OrderByElement> orderByElements = plainSelect.getOrderByElements();
		if (orderByElements != null) {
			Iterator<OrderByElement> it = orderByElements.iterator();
			System.out.print("Order By");
			while (it.hasNext()) {
				System.out.print(it.next() + " ");
			}
			System.out.println();
		}
		// having
		Expression having = plainSelect.getHaving();
		System.out.println("having:" + having);
	}

	private void parser_createIndex(CreateIndex createIndex) {
		System.out.println("table name: " + createIndex.getTable());
		System.out.println("index name: " + createIndex.getIndex().getName());
		Iterator<String> it = createIndex.getIndex().getColumnsNames().iterator();
		System.out.print("columns: ");
		while (it.hasNext())
			System.out.print(it.next() + " ");
	}

	private void parser_select(Select select) {
		System.out.println("SqlParser: void parser_select(Select)");
		SelectBody selectBody = select.getSelectBody();
		System.out.println("select: " + selectBody);
		if (selectBody instanceof PlainSelect) {
			PlainSelect plainSelect = (PlainSelect) selectBody;
			// selectItem
			List<SelectItem> selectItem = plainSelect.getSelectItems();
			if (selectItem != null) {
				Iterator<SelectItem> it = selectItem.iterator();
				System.out.print("columns:");
				while (it.hasNext()) {
					System.out.print(it.next() + " ");
				}
			}
			System.out.println();
			// FromItem
			FromItem fromItem = plainSelect.getFromItem();
			System.out.println("from:" + fromItem);
			// where
			Expression where = plainSelect.getWhere();
			System.out.println("where:" + where);
			// groupByColumnReferences
			List<Expression> groupByColumnReferences = plainSelect.getGroupByColumnReferences();
			if (groupByColumnReferences != null) {
				System.out.print("groupBy:");
				Iterator<Expression> it = groupByColumnReferences.iterator();
				while (it.hasNext()) {
					System.out.print(it.next() + " ");
				}
				System.out.println();
			}
			// orderByElements
			List<OrderByElement> orderByElements = plainSelect.getOrderByElements();
			if (orderByElements != null) {
				Iterator<OrderByElement> it = orderByElements.iterator();
				System.out.print("Order By");
				while (it.hasNext()) {
					System.out.print(it.next() + " ");
				}
				System.out.println();
			}
			// having
			Expression having = plainSelect.getHaving();
			System.out.println("having:" + having);
		}
	}

	private void parser_delete(Delete delete) {
		System.out.println("Parser: void parser_delete(Delete)");
		System.out.println("table_name:" + delete.getTable());
		System.out.println("where: " + delete.getWhere());
	}

	private void parser_drop(Drop drop) {
		System.out.println("Parser: void parser_drop(Drop)");
		System.out.println("type: " + drop.getType());
		System.out.println("name: " + drop.getName());
		System.out.println("option: " + drop.getParameters());
	}

	private void parser_insert(Insert insert) {
		System.out.println("Parser: void parser_insert(Insert)");
		System.out.println("table: " + insert.getTable());
		List<Column> cols = insert.getColumns();
		if (cols != null) {
			System.out.print("columns: ");
			Iterator<Column> it = cols.iterator();
			while (it.hasNext())
				System.out.print(it.next() + " ");
			System.out.println();
		}
		ExpressionList expressionList = ((ExpressionList) insert.getItemsList());
		if (expressionList != null) {
			List<Expression> values = expressionList.getExpressions();
			Iterator<Expression> is = values.iterator();
			System.out.print("values: ");
			while (is.hasNext())
				System.out.print(is.next() + " ");
		}

		if (insert.getSelect() != null)
			parser_select(insert.getSelect());
	}

	private void parser_update(Update update) {
		System.out.println("Parser: void parser_update(Update)");
		System.out.println("table name: " + update.getTables());
		List<Column> cols = update.getColumns();
		if (cols != null) {
			System.out.print("columns: ");
			Iterator<Column> it = cols.iterator();
			while (it.hasNext())
				System.out.print(it.next() + " ");
		}
		System.out.println();
		List<Expression> exp = update.getExpressions();
		if (exp != null) {
			System.out.print("new value: ");
			Iterator<Expression> it = exp.iterator();
			while (it.hasNext())
				System.out.print(it.next() + " ");
		}
		System.out.println();
		if (update.getWhere() != null)
			System.out.println("where: " + update.getWhere());
	}

	private void parser_createTable(CreateTable createTable) {
		System.out.println("Parser: void parser_createTable(CreateTable)");
		// table
		Table table = createTable.getTable();
		System.out.println("table:" + table);
		// ColumnDefinitions
		List<ColumnDefinition> columnDefinitions = createTable.getColumnDefinitions();
		if (columnDefinitions != null) {
			System.out.println("columns:");
			Iterator<ColumnDefinition> it = columnDefinitions.iterator();
			while (it.hasNext()) {
				ColumnDefinition columnDifinition = it.next();
				StringBuilder show = new StringBuilder();
				show.append("name:").append(columnDifinition.getColumnName()).append(" ");
				// colDataType
				ColDataType colDataType = columnDifinition.getColDataType();
				// dataType
				show.append("type:").append(colDataType.getDataType()).append(" ");
				// argumentsStringList
				show.append("option: ");
				List<String> argumentsStringList = colDataType.getArgumentsStringList();
				if (argumentsStringList != null) {
					Iterator<String> iter = argumentsStringList.iterator();
					while (iter.hasNext()) {
						show.append(iter.next()).append(" ");
					}
				}
				// stringSet
				List<String> columnSpecStrings = columnDifinition.getColumnSpecStrings();
				if (columnSpecStrings != null) {
					Iterator<String> ite = columnSpecStrings.iterator();
					while (ite.hasNext()) {
						show.append(ite.next()).append(" ");
					}
				}
				System.out.println(show);
			}
			List<Index> inds = createTable.getIndexes();
			if (inds != null) {
				System.out.println("indexs: ");
				Iterator<Index> is = inds.iterator();
				while (is.hasNext()) {
					Index temp = is.next();
					System.out.println("type: " + temp.getType());
					Iterator<String> ia = temp.getColumnsNames().iterator();
					System.out.print("column: ");
					while (ia.hasNext())
						System.out.print(ia.next() + " ");
					System.out.println();
				}
			}
		}
		// select
		Select select = createTable.getSelect();
		System.out.println("select:" + select);
		if (select != null)
			parser_select(select);
	}

}
