package com.litedbAdvanced.execute;

import com.litedbAdvanced.util.Row;

public abstract class WhereNode {
	protected String pre;
	protected String keyName;
	protected String next;
	protected WhereNode leftChild;
	protected WhereNode rightChild;

	public abstract String get(Row row);

	class OrNode extends WhereNode {

		@Override
		public String get(Row row) {
			// TODO Auto-generated method stub
			return null;
		}

	}

	class NotNode extends WhereNode {

		@Override
		public String get(Row row) {
			// TODO Auto-generated method stub
			return null;
		}

	}

	class KeyNode extends WhereNode {
		public KeyNode(String pre, String keyName, String next) {
			this.pre = pre;
			this.keyName = keyName;
			this.next = next;

		}

		@Override
		public String get(Row row) {
			return pre + row.get(keyName) + next;
		}


	}

}