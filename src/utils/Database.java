package utils;

import java.io.File;
import java.util.List;

import path_recognition.Path;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;

public class Database {
	private SQLiteConnection db;
	
	public Database(String file){
		db = new SQLiteConnection(new File(file));
	    
	}
	
	public void open(){
		try {
			db.open(true);
		} catch (SQLiteException e) {
			System.out.println("Failed when opening DB connection");
			db = null;
			return;
		}

	    try {
	    	db.exec("CREATE TABLE IF NOT EXISTS Replays (id integer primary key);");
			db.exec("CREATE TABLE IF NOT EXISTS Paths (path_id integer primary key, replay_id integer);");
			db.exec("CREATE TABLE IF NOT EXISTS PathNodes (node_id integer primary key, path integer, x integer, y integer, t float, duration float);");
		} catch (SQLiteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void close(){
		db.dispose();
	}
	
	public void storePaths(int replay_id, List<Path> paths){
		try {
			SQLiteStatement get_replay = db.prepare("SELECT * FROM Replays WHERE id ="+replay_id+";");
			if(get_replay.step()){
				db.exec("DELETE * FROM PathNodes n WHERE COUNT(SELECT * FROM Paths p WHERE p.replay_id = "+replay_id+" AND p.path_id = n.path);");
				db.exec("DELETE * FROM Paths p WHERE p.replay_id = "+replay_id+";");
				System.out.println("Removed existing data");
			}
			else
				db.exec("INSERT INTO Replays VALUES("+replay_id+");");
			
			SQLiteStatement insert_path = db.prepare("INSERT INTO Paths(replay_id) VALUES(?);");
			SQLiteStatement insert_node = db.prepare("INSERT INTO PathNodes(path, x,y,t,duration) VALUES(?,?,?,?,?);");
			
			db.exec("BEGIN TRANSACTION;");
			
			db.exec("COMMIT TRANSACTION;");
		} catch (SQLiteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public List<Path> loadPaths(){
		return null;
	}
}
