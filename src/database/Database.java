package database;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;

public class Database {
	private SQLiteConnection db;
	private SQLiteStatement insertReplay = null;
	private SQLiteStatement insertTeam = null;
	private SQLiteStatement insertPlayer = null;
	private SQLiteStatement insertUnit = null;
	private SQLiteStatement insertTimeSeries = null;
	private SQLiteStatement insertTimeSeriesNode = null;
	private SQLiteStatement insertEvent = null;
	
	private SQLiteStatement getUnit = null;
	private SQLiteStatement getTimeSeries = null;
	private SQLiteStatement getNodes = null;
	
	
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
			return;
		}	
		try{
			getUnit = db.prepare("SELECT UnitTypeMap.name, team, controlled_by_player, illusion FROM Units, UnitTypeMap WHERE Units.type = UnitTypeMap.type_id AND unit_id = ?;");
			getTimeSeries = db.prepare("SELECT timeseries_id, TimeSeriesTypeMap.name FROM TimeSeries, TimeSeriesTypeMap WHERE TimeSeries.type = TimeSeriesTypeMap.type_id  AND TimeSeries.unit_id = ?;");
			getNodes = db.prepare("SELECT time, value FROM TimeSeriesNodes WHERE timeseries_id = ? ORDER BY time;");
		} catch (SQLiteException e) {
			e.printStackTrace();
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
	    	db.exec("CREATE TABLE IF NOT EXISTS Teams (team_id integer primary key, name text, tag text, side integer, replay_id integer);");
	    	db.exec("CREATE TABLE IF NOT EXISTS SideMap (side_id integer primary key, name text);");
	    	writeConstants(db.prepare("INSERT OR IGNORE INTO SideMap(side_id, name) VALUES(?, ?);"), Constants.sides);
	    	
	    	db.exec("CREATE TABLE IF NOT EXISTS Players (player_id integer primary key, name text, hero integer, team_id integer);");
	    	//Ensure there is no player 0, as it is used to signal NPCs
	    	db.exec("INSERT INTO Players(player_id) VALUES (0)");
	    	db.exec("DELETE FROM Players WHERE Players.player_id = 0;");	    	
	    	
	    	db.exec("CREATE TABLE IF NOT EXISTS Units (unit_id integer primary key, type integer, team integer, controlled_by_player integer, illusion integer, replay_id integer);");
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
	    	db.exec("CREATE TABLE IF NOT EXISTS TimeSeriesNodes (node_id integer primary key, timeseries_id integer, time real, value real);");
			
	    	db.exec("CREATE TABLE IF NOT EXISTS Events (event_id integer primary key, time real, type integer, actor_unit integer, affected_unit integer, value text, replay_id integer);");
			db.exec("CREATE TABLE IF NOT EXISTS EventTypeMap (type_id integer primary key, name text);");
			writeConstants(db.prepare("INSERT OR IGNORE INTO EventTypeMap(type_id, name) VALUES(?, ?);"), Constants.eventTypes);

			db.exec("COMMIT TRANSACTION;");
			
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
	    
		try {
			insertReplay = db.prepare("INSERT INTO Replays(id) VALUES(?);");
			insertTeam = db.prepare("INSERT INTO Teams(name, tag, side, replay_id) VALUES(?, ?, ?, ?);");
			insertPlayer = db.prepare("INSERT INTO Players(name, hero, team_id) VALUES(?, ?, ?);");
			insertUnit = db.prepare("INSERT INTO Units(type, team, controlled_by_player, illusion, replay_id) VALUES(?, ?, ?, ?, ?);");
			insertTimeSeries = db.prepare("INSERT INTO TimeSeries(type, unit_id) VALUES(?, ?);");
			insertTimeSeriesNode = db.prepare("INSERT INTO TimeSeriesNodes(timeseries_id, time, value) VALUES(?, ?, ?);");
			insertEvent = db.prepare("INSERT INTO Events(time, type, actor_unit, affected_unit, value, replay_id) VALUES(?, ?, ?, ?, ?, ?);");
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
	
	public int createTeam(String name, String tag, String side, int replay_id){
		try {
			insertTeam.bind(1, name).bind(2, tag).bind(3, Constants.sides.get(side)).bind(4, replay_id);
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
	
	public int createUnit(String unit_name, String team, int player_id, boolean illusion, int replay){
		try {
			insertUnit.bind(1, Constants.unitTypes.get(unit_name)).bind(2, Constants.teams.get(team)).bind(3, player_id).bind(4, illusion? 1: 0).bind(5, replay);
			insertUnit.step();
			insertUnit.reset();
			return ((int) db.getLastInsertId());
			
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
	
	public int createEvent(int replay_id, double time, String type, int actor_unit, int affected_unit, String value){
		try {
			insertEvent.bind(1, time).bind(2, Constants.eventTypes.get(type)).bind(3, actor_unit).bind(4, affected_unit).bind(5, value).bind(6, replay_id);
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
		startTransaction();
		try {
			db.exec("DELETE FROM Replays WHERE Replays.id ="+id+";");
			db.exec("DELETE FROM Players WHERE EXISTS (SELECT * FROM Teams WHERE Teams.replay_id ="+id+" AND Teams.team_id = Players.team_id);");
			db.exec("DELETE FROM Teams WHERE replay_id ="+id+";");
			db.exec("DELETE FROM TimeSeriesNodes WHERE EXISTS (SELECT * FROM Units, TimeSeries WHERE Units.replay_id = "+id+" AND Units.unit_id = TimeSeries.unit_id AND TimeSeriesNodes.timeseries_id = TimeSeries.timeseries_id );");
			db.exec("DELETE FROM TimeSeries WHERE EXISTS (SELECT * FROM Units WHERE Units.replay_id ="+id+" AND TimeSeries.unit_id = Units.unit_id);");
			db.exec("DELETE FROM Units WHERE replay_id ="+id+";");
			db.exec("DELETE FROM Events WHERE replay_id ="+id+";");
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
		stopTransaction();
	}
	
	public Unit getUnit(int id){
		try {
			getUnit.bind(1, id);
			getUnit.step();
			String unitType = getUnit.columnString(0);
			int team = getUnit.columnInt(1);
			int controlledByPlayer = getUnit.columnInt(2);
			boolean illusion = (getUnit.columnInt(3) == 1);
			getUnit.reset();
			
			getTimeSeries.bind(1, id);
			List<TimeSeries> timeSeries = new LinkedList<TimeSeries>();
			while(getTimeSeries.step()){
				int seriesID = getTimeSeries.columnInt(0);
				String seriesType = getTimeSeries.columnString(1);
				List<TimeSeriesNode> nodes = new LinkedList<TimeSeriesNode>();
				getNodes.bind(1, seriesID);
				while(getNodes.step()){
					nodes.add(new TimeSeriesNode(getNodes.columnDouble(0), getNodes.columnDouble(1)));
				}
				getNodes.reset();
				timeSeries.add(new TimeSeries(seriesType, nodes));
			}
			getTimeSeries.reset();
			
			return new Unit(unitType, null, null, illusion, timeSeries);
		} catch (SQLiteException e) {
			e.printStackTrace();
			return null;
		}
	}
}
