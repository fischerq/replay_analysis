package utils;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import path_recognition.Path;
import path_recognition.PathNode;

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
			db.exec("CREATE TABLE IF NOT EXISTS Paths (path_id integer primary key, replay_id integer, unit_id integer, name text);");
			db.exec("CREATE TABLE IF NOT EXISTS PathNodes (node_id integer primary key, path integer, x integer, y integer, t real, duration real);");
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
			
			db.exec("BEGIN TRANSACTION;");
			if(get_replay.step()){
				db.exec("DELETE FROM PathNodes WHERE EXISTS ( SELECT * FROM Paths AS p WHERE p.replay_id = "+replay_id+" AND p.path_id = PathNodes.path);");
				db.exec("DELETE FROM Paths WHERE Paths.replay_id = "+replay_id+";");
				System.out.println("Removed existing data");
			}
			else
				db.exec("INSERT INTO Replays VALUES("+replay_id+");");
			
			SQLiteStatement insert_path = db.prepare("INSERT INTO Paths(replay_id, unit_id, name) VALUES("+replay_id+", ?, ?);");
			SQLiteStatement insert_node = db.prepare("INSERT INTO PathNodes(path, x,y,t,duration) VALUES(?,?,?,?,?);");
			
			try {
				Iterator<Path> p_it = paths.iterator();
				while(p_it.hasNext()){
					Path p = p_it.next();
					List<PathNode> nodes = p.getNodes();
					if(nodes.size() == 0)
						continue;
					insert_path.bind(1, p.unit_id).bind(2, p.name.replace("'", "\""));
					insert_path.step();
					int path_id = (int) db.getLastInsertId();
					Iterator<PathNode> it_n = nodes.iterator();
					while(it_n.hasNext()){
						PathNode n = it_n.next();
						insert_node.bind(1, path_id).bind(2, n.position[0]).bind(3, n.position[1]).bind(4, n.time).bind(5, n.duration);
						insert_node.step();
						insert_node.reset();
					}
					insert_path.reset();
				}
			 } finally {
			 }
			db.exec("COMMIT TRANSACTION;");
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
	}
	
	public List<LinkedList<PathNode>> loadPaths(){
		List<LinkedList<PathNode> > result =  new LinkedList<LinkedList<PathNode> >();
		try {
			SQLiteStatement get_paths = db.prepare("SELECT path_id FROM Paths;");
			SQLiteStatement get_nodes = db.prepare("SELECT x,y,t,duration FROM PathNodes AS n WHERE n.path = ? ORDER BY node_id;");
			while(get_paths.step()){
				System.out.println("Loading path "+result.size());
				int path_id = get_paths.columnInt(0);
				LinkedList<PathNode> path = new LinkedList<PathNode>();
				get_nodes.bind(1, path_id);
				while(get_nodes.step()){
					PathNode n = new PathNode();
					n.position[0] = get_nodes.columnInt(0);
					n.position[1] = get_nodes.columnInt(1);
					n.time = (float) get_nodes.columnDouble(2);
					n.duration = (float) get_nodes.columnDouble(3);
					path.add(n);
				}
				get_nodes.reset();
				result.add(path);
			}
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
		
		return result;
	}
}
