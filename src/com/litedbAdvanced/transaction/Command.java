package com.litedbAdvanced.transaction;

public abstract class Command {

	public abstract void exec();

	public abstract void undo();
	
	
	
	class UpdateRowCommand extends Command{

		@Override
		public void exec() {
			
		}

		@Override
		public void undo() {
			
		}
	}
	
	class DeleteRowCommand extends Command{

		@Override
		public void exec() {
			
		}

		@Override
		public void undo() {
			
		}
	}
	
	class InsertRowCommand extends Command{

		@Override
		public void exec() {
			
		}

		@Override
		public void undo() {
			
		}
	}
	
	class CreateTableCommand extends Command{

		@Override
		public void exec() {
			
		}

		@Override
		public void undo() {
			
		}
	}
	
	class DropTableCommand extends Command{

		@Override
		public void exec() {
			
		}

		@Override
		public void undo() {
			
		}
	}

}
