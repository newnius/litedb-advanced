package com.litedbAdvanced.execute;

import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.litedbAdvanced.parser.sqlParser;
import com.litedbAdvanced.transaction.Transaction;
import com.litedbAdvanced.util.LiteLogger;
import com.litedbAdvanced.util.Row;

public class Main {
	public static final String TAG = "EXECUTOR";
	private boolean autoCommit = true;
	private Transaction transaction = null;
	public static ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("js");

	public void setAutoCommit(boolean autoCommit) {
		this.autoCommit = autoCommit;
	}

	public String execute(String query) {
		transaction = new Transaction();
		/* build parse tree */
		sqlParser.parse(query);
		
		/* get RIDs to be accessed*/
		List<Long> RIDs = com.litedbAdvanced.optimizer.Main.RIDsToGet();
		
		/* SLock related rows */
		for (Long rid : RIDs) {
			Row row = transaction.read(rid);
			LiteLogger.info(Main.TAG, row.toString());
		}
		transaction.stopRead();
		return query;
	}

	public void executeCommand(String command) {
		if (command == null)
			return;
		String[] params = command.split(" ");
		switch (params[0]) {
		case "begin":
			begin();
			break;
		case "read":
			read(params);
			break;
		case "write":
			write(params);
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
			LiteLogger.info(TAG, "Unknown command.");
		}
		return;
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
		transaction.write(Long.parseLong(args[1]));
		afterAccess();
	}

	public static boolean testWhere(String whereClause) {
		String query = whereClause;
		query = query.replaceAll("and", "&&").replaceAll("or", "||");
		try {
			String res = scriptEngine.eval(query) + "";
			return res.equals("true");
		} catch (ScriptException ex) {

		}
		return true;
	}

}
