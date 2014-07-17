package database;

import java.io.File;
import java.util.LinkedList;
import java.util.List;


import utils.Replay;

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
			e.printStackTrace();
		}
	}
	
	public void close(){
		db.dispose();
	}
	
	public boolean replayExists(Replay r){
		SQLiteStatement get_replay;
		boolean result = false;
		try {
			get_replay = db.prepare("SELECT * FROM Replays WHERE id =?;");
			get_replay.bind(1, r.id);
			result = get_replay.step();
			get_replay.dispose();
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
		return result;			
	}
	
	public void storePaths(Replay replay, List<path_recognition.Path> paths){
		try {
			db.exec("BEGIN TRANSACTION;");
			if(replayExists(replay)){
				db.exec("DELETE FROM PathNodes WHERE EXISTS ( SELECT * FROM Paths AS p WHERE p.replay_id = "+replay.id+" AND p.path_id = PathNodes.path);");
				db.exec("DELETE FROM Paths WHERE Paths.replay_id = "+replay.id+";");
				System.out.println("Removed existing data");
			}
			else
				db.exec("INSERT INTO Replays VALUES("+replay.id+");");
			
			SQLiteStatement insert_path = db.prepare("INSERT INTO Paths(replay_id, unit_id, name) VALUES("+replay.id+", ?, ?);");
			SQLiteStatement insert_node = db.prepare("INSERT INTO PathNodes(path, x,y,t,duration) VALUES(?,?,?,?,?);");
			
			try {
				for(path_recognition.Path p : paths){
					List<PathNode> nodes = p.getNodes();
					if(nodes.size() == 0)
						continue;
					insert_path.bind(1, p.unit_id).bind(2, p.name.replace("'", "\""));
					insert_path.step();
					int path_id = (int) db.getLastInsertId();
					for(PathNode n : nodes){
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
	
	public List<Integer> getPathIDs(){
		List<Integer> ids = new LinkedList<Integer>();
		
		try {
			SQLiteStatement get_path_ids = db.prepare("SELECT path_id FROM Paths;");
			while(get_path_ids.step()){
				ids.add(get_path_ids.columnInt(0));
			}
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
		return ids;
	}
	
	public Path loadPath(int id){
		Path path = new Path();
		try {
			SQLiteStatement get_path = db.prepare("SELECT unit_id, name FROM Paths WHERE path_id = "+id+";");
			SQLiteStatement get_nodes = db.prepare("SELECT x,y,t,duration FROM PathNodes AS n WHERE n.path = ? ORDER BY node_id;");
			if(get_path.step()){
				path.unit_id = get_path.columnInt(0);
				path.player = get_path.columnString(1);
				
				get_nodes.bind(1, id);
				while(get_nodes.step()){
					PathNode n = new PathNode();
					n.position[0] = get_nodes.columnInt(0);
					n.position[1] = get_nodes.columnInt(1);
					n.time = (float) get_nodes.columnDouble(2);
					n.duration = (float) get_nodes.columnDouble(3);
					path.nodes.add(n);
				}
			}
		} catch (SQLiteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return path;
	}
	
	public List<Path> loadPaths(){
		List<Path> result =  new LinkedList<Path>();
		try {
			SQLiteStatement get_paths = db.prepare("SELECT path_id, unit_id, name FROM Paths;");
			SQLiteStatement get_nodes = db.prepare("SELECT x,y,t,duration FROM PathNodes AS n WHERE n.path = ? ORDER BY node_id;");
			while(get_paths.step()){
				System.out.println("Loading path "+result.size());
				int path_id = get_paths.columnInt(0);
				int unit_id = get_paths.columnInt(1);
				String player_name = get_paths.columnString(2);
				
				Path path = new Path();
				path.unit_id = unit_id;
				path.player = player_name;
				
				get_nodes.bind(1, path_id);
				while(get_nodes.step()){
					PathNode n = new PathNode();
					n.position[0] = get_nodes.columnInt(0);
					n.position[1] = get_nodes.columnInt(1);
					n.time = (float) get_nodes.columnDouble(2);
					n.duration = (float) get_nodes.columnDouble(3);
					path.nodes.add(n);
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