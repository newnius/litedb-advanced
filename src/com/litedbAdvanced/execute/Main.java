package com.litedbAdvanced.execute;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.litedbAdvanced.transaction.Transaction;
import com.litedbAdvanced.util.LiteLogger;
import com.litedbAdvanced.util.Row;
import com.litedbAdvanced.util.StringUtils;
import com.litedbAdvanced.util.TableDef;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.create.table.ColDataType;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.create.table.Index;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.Limit;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.update.Update;

public class Main {
	public static final String TAG = "EXECUTOR";
	private boolean autoCommit = true;
	private Transaction transaction = null;
	public static ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("js");

	public void setAutoCommit(boolean autoCommit) {
		this.autoCommit = autoCommit;
	}

	public Object execute(String query) {
		try {
			Object res = null;
			if (transaction == null)
				transaction = new Transaction();
			
			if(executeCommand(query)){
				return 0;
			}

			Statement statement = com.litedbAdvanced.parser.Main.parse(query);
			if (statement == null) {
				res = 0;
			} else if (statement instanceof Select) {
				res = executeSelect((Select) statement);
			} else if (statement instanceof Delete) {
				res = executeDelete((Delete) statement);
			} else if (statement instanceof Insert) {
				res = executeInsert((Insert) statement);
			} else if (statement instanceof Update) {
				res = executeUpdate((Update) statement);
			} else if (statement instanceof CreateTable) {
				res = executeCreateTable((CreateTable) statement);
			} else if (statement instanceof Drop) {
				res = executeDropTable((Drop) statement);
			}

			if (autoCommit)
				commit();

			return res;
		} catch (Exception ex) {
			LiteLogger.error(Main.TAG, ex);
			return null;
		}
	}

	private List<Row> executeSelect(Select statement) {
		LiteLogger.info(Main.TAG, statement.getSelectBody().toString());
		SelectBody selectBody = statement.getSelectBody();

		String tableName = null;
		List<String> keys = new ArrayList<>();
		long offset = 0;
		long rowCount = -1;
		String orderBy = null;
		boolean isDesc = false;
		String whereClause = "1=1";

		if (selectBody instanceof PlainSelect) {
			PlainSelect plainSelect = (PlainSelect) selectBody;
			// selectItem
			List<SelectItem> selectItem = plainSelect.getSelectItems();
			if (selectItem != null) {
				Iterator<SelectItem> it = selectItem.iterator();
				while (it.hasNext()) {
					keys.add(it.next().toString());
				}
			}

			// FromItem
			FromItem fromItem = plainSelect.getFromItem();
			tableName = fromItem.toString();

			// where
			Expression where = plainSelect.getWhere();
			if (where != null)
				whereClause = where.toString();

			// orderByElements
			List<OrderByElement> orderByElements = plainSelect.getOrderByElements();

			if (orderByElements != null) {
				Iterator<OrderByElement> it = orderByElements.iterator();
				while (it.hasNext()) {
					String str = it.next().toString();
					if (str.equals("DESC")) {
						isDesc = true;
					} else {
						orderBy = str;
					}
				}
			}

			Limit limit = plainSelect.getLimit();
			if (limit != null) {
				offset = limit.getOffset();
				rowCount = limit.getRowCount();
			}
		}

		TableDef tableDef = com.litedbAdvanced.storage.Main.getTableStructure(tableName);
		// LiteLogger.info(Main.TAG, tableName);

		/* analyze which RIDs to read */
		List<Long> RIDs = com.litedbAdvanced.optimizer.Main.RIDsToGet(tableDef, keys, keys);

		WhereParser parser = WhereParser.parseWhere(tableDef, whereClause);

		List<Row> results = new ArrayList<>();

		/* get RIDs to be accessed */
		for (long RID : RIDs) {
			Row row = transaction.read(RID);
			String str = parser.getString(row);
			if (testWhere(str)) {
				results.add(row);
			}
		}
		transaction.stopRead();

		/* SLock related rows */
		/*
		 * for (Long rid : RIDs) { Row row = transaction.read(rid);
		 * LiteLogger.info(Main.TAG, row.toString()); } transaction.stopRead();
		 */
		LiteLogger.info(Main.TAG, results.size() + " rows seltcted");
		return results;
	}

	private int executeUpdate(Update statement) {
		LiteLogger.info(Main.TAG, "update table " + statement.getTables().get(0).toString());

		String tableName = statement.getTables().get(0).toString();
		List<String> keys = new ArrayList<>();
		List<String> values = new ArrayList<>();
		String whereClause = "1=1";

		List<Column> cols = statement.getColumns();
		if (cols != null) {
			Iterator<Column> it = cols.iterator();
			while (it.hasNext()) {
				keys.add(it.next().toString());
			}
		}

		List<Expression> exp = statement.getExpressions();
		if (exp != null) {
			Iterator<Expression> it = exp.iterator();
			while (it.hasNext()) {
				values.add(it.next().toString());
			}
		}

		// where
		if (statement.getWhere() != null)
			whereClause = statement.getWhere().toString();

		if (keys.size() != values.size()) {
			LiteLogger.info(Main.TAG, "keys and values not match");
			return 1;
		}

		TableDef tableDef = com.litedbAdvanced.storage.Main.getTableStructure(tableName);
		// LiteLogger.info(Main.TAG, tableName);

		/* analyze which RIDs to read */
		List<Long> RIDs = com.litedbAdvanced.optimizer.Main.RIDsToGet(tableDef, keys,
				WhereParser.getKeys(tableDef, whereClause));

		WhereParser parser = WhereParser.parseWhere(tableDef, whereClause);

		List<Row> results = new ArrayList<>();

		/* get RIDs to be accessed */
		for (long RID : RIDs) {
			Row row = transaction.readForUpdate(RID);
			String str = parser.getString(row);
			if (testWhere(str)) {
				/* do update */
				for (int i = 0; i < keys.size(); i++) {
					row.set(keys.get(i), values.get(i));
				}
				transaction.updateRow(RID, row);
				results.add(row);
			}
		}

		LiteLogger.info(Main.TAG, results.size() + " rows updated");
		return 0;
	}

	private int executeDelete(Delete statement) {
		LiteLogger.info(Main.TAG, "delete row");

		String tableName = statement.getTable().toString();
		String whereClause = "1=1";

		// where
		Expression where = statement.getWhere();
		if (where != null)
			whereClause = where.toString();

		TableDef tableDef = com.litedbAdvanced.storage.Main.getTableStructure(tableName);
		// LiteLogger.info(Main.TAG, tableName);

		/* analyze which RIDs to read */
		List<Long> RIDs = com.litedbAdvanced.optimizer.Main.RIDsToGet(tableDef, new ArrayList<>(), new ArrayList<>());

		WhereParser parser = WhereParser.parseWhere(tableDef, whereClause);

		List<Long> results = new ArrayList<>();

		/* get RIDs to be accessed */
		for (long RID : RIDs) {
			Row row = transaction.readForUpdate(RID);
			String str = parser.getString(row);
			if (testWhere(str)) {
				results.add(RID);
			}
		}

		/* SLock related rows */
		/*
		 * for (Long rid : RIDs) { Row row = transaction.read(rid);
		 * LiteLogger.info(Main.TAG, row.toString()); } transaction.stopRead();
		 */
		for (long rid : results)
			transaction.deleteRow(rid);
		LiteLogger.info(Main.TAG, results.size() + " rows deleted");
		return 0;
	}

	private int executeInsert(Insert statement) {
		String tableName = statement.getTable().getName();
		List<Column> cols = statement.getColumns();
		List<String> keys = new ArrayList<>();
		List<String> values = new ArrayList<>();
		if (cols != null) {
			for (Column column : cols)
				keys.add(column.getColumnName());
		}

		ExpressionList expressionList = ((ExpressionList) statement.getItemsList());
		if (expressionList != null) {
			List<Expression> expressions = expressionList.getExpressions();
			Iterator<Expression> is = expressions.iterator();
			while (is.hasNext())
				values.add(is.next().toString());
		}
		TableDef tableDef = com.litedbAdvanced.storage.Main.getTableStructure(tableName);
		if (tableDef == null) {
			LiteLogger.info(Main.TAG, "table not exist");
			return 0;
		}

		// keys and values not match
		if (keys.size() != 0 && keys.size() != values.size()
				&& (keys.size() == 0 && values.size() != tableDef.getKeyNames().size())) {
			LiteLogger.info(Main.TAG, "keys and values not match");
			return 0;
		}

		Row row = null;
		if (keys.size() == 0) {
			row = new Row(tableDef, values);
		} else {
			row = new Row(tableDef, keys, values);
		}

		LiteLogger.info(Main.TAG, row.toString());
		/*
		 * do not consider "insert select" now if (statement.getSelect() !=
		 * null) executeSelect(statement.getSelect());
		 */
		long nextRID = com.litedbAdvanced.storage.Main.nextRID(tableName);
		return transaction.insertRow(nextRID, row);
	}

	private int executeCreateTable(CreateTable statement) {
		String tableName = statement.getTable().getName();
		String primaryKey = null;
		List<String> keyNames = new ArrayList<>();
		List<Integer> types = new ArrayList<>();
		List<Integer> lengths = new ArrayList<>();
		List<String> indexs = new ArrayList<>();

		List<ColumnDefinition> columnDefinitions = statement.getColumnDefinitions();
		if (columnDefinitions != null) {
			Iterator<ColumnDefinition> it = columnDefinitions.iterator();
			while (it.hasNext()) {
				ColumnDefinition columnDifinition = it.next();
				keyNames.add(columnDifinition.getColumnName());
				ColDataType colDataType = columnDifinition.getColDataType();
				switch (colDataType.getDataType()) {
				case "int":
					types.add(TableDef.TYPE_INT);
					break;
				case "double":
					types.add(TableDef.TYPE_DOUBLE);
					break;
				case "char":
					types.add(TableDef.TYPE_CHAR);
					break;
				case "varchar":
					types.add(TableDef.TYPE_VARCHAR);
					break;
				}
				List<String> argumentsStringList = colDataType.getArgumentsStringList();
				if (argumentsStringList != null) {
					Iterator<String> iter = argumentsStringList.iterator();
					while (iter.hasNext()) {
						String str = iter.next();
						if (StringUtils.isInteger(str)) {
							lengths.add(Integer.parseInt(str));
						}
					}
				}
			}
			List<Index> inds = statement.getIndexes();
			if (inds != null) {
				Iterator<Index> is = inds.iterator();
				while (is.hasNext()) {
					Index temp = is.next();
					Iterator<String> ia = temp.getColumnsNames().iterator();
					List<String> indexNames = new ArrayList<>();
					while (ia.hasNext()) {
						indexNames.add(ia.next());
					}

					if (temp.getType().equals("primary key")) {
						primaryKey = indexNames.get(0);
					} else if (temp.getType().equals("index")) {
						indexs.add(indexNames.get(0));
					}
				}
			}
		}
		if (keyNames.size() != lengths.size()) {
			LiteLogger.info(Main.TAG, "key size and length size not match");
			return 1;
		}
		TableDef tableDef = new TableDef(tableName, primaryKey, keyNames, types, lengths, indexs);
		LiteLogger.info(Main.TAG, tableDef.toString());
		return transaction.createTable(tableDef);
	}

	private int executeDropTable(Drop statement) {
		if (statement.getType().equals("table"))
			return transaction.dropTable(statement.getName());
		return 0;
	}

	public boolean executeCommand(String command) {
		if (command == null)
			return false;
		String[] params = command.split(" ");
		boolean isSimpleCommand = true;
		switch (params[0]) {
		case "begin":
			begin();
			break;
		case "savepoint":
			savepoint(params);
			break;
		case "rollback":
			rollback(params);
			break;
		case "commit":
			commit();
			break;
		default:
			isSimpleCommand = false;
			// LiteLogger.info(TAG, "Unknown command.");
		}
		return isSimpleCommand;
	}

	public void begin() {
		transaction = new Transaction();
		autoCommit = false;
	}

	public void commit() {
		if (transaction == null) {
			LiteLogger.info(TAG, "transaction not exist.");
			return;
		}
		transaction.commit();
		transaction = null;
		autoCommit = true;
	}

	public void savepoint(String[] args) {
		if (args.length < 2) {
			LiteLogger.info(Main.TAG, "point name not specified.");
		}
		transaction.savePoint(args[1]);

	}

	public void rollback(String[] args) {
		if (transaction == null) {
			LiteLogger.info(TAG, "transaction not exist.");
		} else if (args.length == 1) {
			transaction.abort();
			transaction = null;
			autoCommit = true;
		} else if (args.length < 2) {
			LiteLogger.info(TAG, "savepoint not specified.");
			return;
		} else
			transaction.rollback(args[1]);
	}

	private void beforeAccess() {
		if (transaction == null)
			transaction = new Transaction();
	}

	private void afterAccess() {
		if (autoCommit)
			commit();
	}

	public void read(String[] args) {
		beforeAccess();
		if (args.length < 2) {
			LiteLogger.info(TAG, "RID not specified.");
			return;
		}
		transaction.read(Long.parseLong(args[1]));
		afterAccess();
	}

	public void write(String[] args) {
		beforeAccess();
		if (args.length < 2) {
			LiteLogger.info(TAG, "RID not specified.");
			return;
		}
		// transaction.write(Long.parseLong(args[1]));
		afterAccess();
	}

	public static boolean testWhere(String whereClause) {
		String query = whereClause;
		try {
			String res = scriptEngine.eval(query) + "";
			LiteLogger.info(Main.TAG, whereClause + "(" + res + ")");
			return res.equals("true");
		} catch (ScriptException ex) {
			LiteLogger.error(Main.TAG, ex);
			return false;
		}
	}

}
