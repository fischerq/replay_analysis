package database;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


import utils.Replay;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;

public class Database {
	private SQLiteConnection db;
	private SQLiteStatement insertReplay;
	private SQLiteStatement insertTeam;
	private SQLiteStatement insertPlayer;
	private SQLiteStatement insertUnit;
	private SQLiteStatement insertTimeSeries;
	private SQLiteStatement insertTimeSeriesNode;
	private SQLiteStatement insertEvent;
	
	public Database(String file){
		db = new SQLiteConnection(new File(file));
	    
	}
	
	private void writeConstants(SQLiteStatement insert_statement, Map<String, Integer> constantMap){
    	for(Map.Entry<String, Integer> e : constantMap.entrySet()){
    		try {
				insert_statement.bind(1,e.getValue());
				insert_statement.bind(2,e.getKey());
	    		insert_statement.step();
	    		insert_statement.reset();
    		} catch (SQLiteException e1) {
				e1.printStackTrace();
			}
    	}
	}

	public void open_read(){
		try {
			db.open(false);
		} catch (SQLiteException e) {
			System.out.println("Failed when opening DB connection");
			db = null;
		}
	}
	
	public void open_write(){
		try {
			db.open(true);
		} catch (SQLiteException e) {
			System.out.println("Failed when opening DB connection");
			db = null;
			return;
		}

		//Setup the DB
	    try {
			db.exec("BEGIN TRANSACTION;");

	    	db.exec("CREATE TABLE IF NOT EXISTS Replays (id integer primary key);");
	    	db.exec("CREATE TABLE IF NOT EXISTS Teams (team_id integer primary key, name text, side integer, replay_id integer);");
	    	db.exec("CREATE TABLE IF NOT EXISTS SideMap (side_id integer primary key, name text);");
	    	writeConstants(db.prepare("INSERT OR IGNORE INTO SideMap(side_id, name) VALUES(?, ?);"), Constants.sides);
	    	
	    	db.exec("CREATE TABLE IF NOT EXISTS Players (player_id integer primary key, name text, hero integer, team_id integer);");
	    	//Ensure there is no player 0, as it is used to signal NPCs
	    	db.exec("INSERT INTO Players(player_id) VALUES (0)");
	    	db.exec("DELETE FROM Players WHERE Players.player_id = 0;");	    	
	    	
	    	db.exec("CREATE TABLE IF NOT EXISTS Units (unit_id integer primary key, type integer, team integer, controlled_by_player integer, replay_id integer);");
	    	//Also reserve unit 0 for empty fields
	    	db.exec("INSERT INTO Units(unit_id) VALUES (0)");
	    	db.exec("DELETE FROM Units WHERE Units.unit_id = 0;");	    	

	    	db.exec("CREATE TABLE IF NOT EXISTS UnitTypeMap (type_id integer primary key, name text);");
	    	writeConstants(db.prepare("INSERT OR IGNORE INTO UnitTypeMap(type_id, name) VALUES(?, ?);"), Constants.unitTypes);
	    	
	    	db.exec("CREATE TABLE IF NOT EXISTS TeamMap (team_id integer primary key, name text);");
	    	writeConstants(db.prepare("INSERT OR IGNORE INTO TeamMap(team_id, name) VALUES(?, ?);"), Constants.teams);
	    	
			db.exec("CREATE TABLE IF NOT EXISTS TimeSeries (timeseries_id integer primary key, type integer, unit_id integer);");
	    	db.exec("CREATE TABLE IF NOT EXISTS TimeSeriesTypeMap (type_id integer primary key, name text);");
	    	writeConstants(db.prepare("INSERT OR IGNORE INTO TimeSeriesTypeMap(type_id, name) VALUES(?, ?);"), Constants.timeSeries);
	    	db.exec("CREATE TABLE IF NOT EXISTS TimeSeriesNodes (node_id integer primary key, timeseries_id integer, t real, value real);");
			
	    	db.exec("CREATE TABLE IF NOT EXISTS Events (event_id integer primary key, type integer, actor_unit integer, affected_unit integer, value text, replay_id integer);");
			db.exec("CREATE TABLE IF NOT EXISTS EventTypes (type_id integer primary key, name text);");
			writeConstants(db.prepare("INSERT OR IGNORE INTO EventTypes(type_id, name) VALUES(?, ?);"), Constants.eventTypes);

			db.exec("COMMIT TRANSACTION;");
			
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
	    
		try {
			insertReplay = db.prepare("INSERT INTO Replays(id) VALUES(?);");
			insertTeam = db.prepare("INSERT INTO Teams(name, side_id, replay_id) VALUES(?, ?, ?);");
			insertPlayer = db.prepare("INSERT INTO Players(name, hero, team_id) VALUES(?, ?, ?, ?);");
			insertUnit = db.prepare("INSERT INTO Units(type, team, controlled_by_player, replay_id) VALUES(?, ?, ?, ?);");
			insertTimeSeriesNode = db.prepare("INSERT INTO TimeSeriesNodes(timeseries_id, t, value) VALUES(?, ?, ?);");
			insertTimeSeries = db.prepare("INSERT INTO TimeSeries(type, unit_id) VALUES(?, ?);");
			insertEvent = db.prepare("INSERT INTO Events(type, actor_unit, affected_unit, value, replay_id) VALUES(?, ?, ?, ?, ?);");
		} catch (SQLiteException e) {
			e.printStackTrace();
		}

	}
	
	public void close(){
		db.dispose();
	}
	
	public void startTransaction(){
		try {
			db.exec("BEGIN TRANSACTION;");
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
	}
	public void stopTransaction(){
		try {
			db.exec("COMMIT TRANSACTION;");
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
	}
	
	public int createReplay(int id){
		try {
			insertReplay.bind(1, id);
			insertReplay.step();
			insertReplay.reset();
			return (int) db.getLastInsertId();
			
		} catch (SQLiteException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	public int createTeam(String name, String side, int replay_id){
		try {
			insertTeam.bind(1, name).bind(2, Constants.sides.get(side)).bind(3, replay_id);
			insertTeam.step();
			insertTeam.reset();
			return (int) db.getLastInsertId();
			
		} catch (SQLiteException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	public int createPlayer(String name, String hero, int team_id){
		try {
			insertPlayer.bind(1, name).bind(2, Constants.unitTypes.get(hero)).bind(3, team_id);
			insertPlayer.step();
			insertPlayer.reset();
			return (int) db.getLastInsertId();
			
		} catch (SQLiteException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	public int createUnit(String unit_name, String team, int player_id, int replay){
		try {
			insertUnit.bind(1, Constants.unitTypes.get(unit_name)).bind(2, Constants.teams.get(team)).bind(3, player_id).bind(4, replay);
			insertUnit.step();
			insertUnit.reset();
			return (int) db.getLastInsertId();
			
		} catch (SQLiteException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	public void makeUnitControlChanging(int unit_id){
		SQLiteStatement modifyUnitControl;
		try {
			modifyUnitControl = db.prepare("UPDATE Units SET team ="+Constants.teams.get("Changing")+", controlled_by_player =-1 WHERE Units.unit_id = ?;");
			modifyUnitControl.bind(1, unit_id);
			modifyUnitControl.step();			
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
	}
	
	public int createTimeSeries(String type, int unit_id){
		try {
			insertTimeSeries.bind(1, Constants.timeSeries.get(type)).bind(2, unit_id);
			insertTimeSeries.step();
			insertTimeSeries.reset();
			return (int) db.getLastInsertId();
			
		} catch (SQLiteException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	public void addTimeSeriesNode(int series_id, double t, double value){
		try {
			insertTimeSeriesNode.bind(1, series_id).bind(2, t).bind(3, value);
			insertTimeSeriesNode.step();
			insertTimeSeriesNode.reset();
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
	}
	
	public int createEvent(int replay_id, String type, int actor_unit, int affected_unit, String value){
		try {
			insertEvent.bind(1, Constants.eventTypes.get(type)).bind(2, actor_unit).bind(3, affected_unit).bind(4, value).bind(5, replay_id);
			insertEvent.step();
			insertEvent.reset();
			return (int) db.getLastInsertId();
			
		} catch (SQLiteException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	public boolean replayExists(int id){
		SQLiteStatement getReplay;
		boolean result = false;
		try {
			getReplay = db.prepare("SELECT * FROM Replays WHERE id =?;");
			getReplay.bind(1, id);
			result = getReplay.step();
			getReplay.dispose();
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
		return result;			
	}
	
	public void deleteReplay(int id){
		//TODO
	}
}
