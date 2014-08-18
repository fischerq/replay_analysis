package db_viewer;

import java.util.List;

import javax.swing.JFrame;

import database.Database;

public class DatabaseWindow extends JFrame{
	private Database db;
	
	private List<Integer> replays;
	
	public DatabaseWindow(Database db){

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		this.db = db;
		replays = db.getReplays();
		
	}
}
