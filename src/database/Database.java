package database;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;

import database.Unit.UnitSummary;

public class Database {
	private SQLiteConnection db;
	private SQLiteStatement insertReplay = null;
	private SQLiteStatement insertTeam = null;
	private SQLiteStatement insertPlayer = null;
	private SQLiteStatement insertUnit = null;
	private SQLiteStatement insertTimeSeries = null;
	private SQLiteStatement insertTimeSeriesNode = null;
	private SQLiteStatement insertEvent = null;
	private SQLiteStatement insertIntArgument = null;
	private SQLiteStatement insertRealArgument = null;
	private SQLiteStatement insertTextArgument = null;
	private SQLiteStatement insertEventCausality = null;
	
	private SQLiteStatement getReplays = null;
	private SQLiteStatement getTeams = null;
	private SQLiteStatement getPlayers = null;
	private SQLiteStatement getUnits = null;
	private SQLiteStatement getUnit = null;
	private SQLiteStatement getTimeSeries = null;
	private SQLiteStatement getNodes = null;
	private SQLiteStatement getEvents = null;
	private SQLiteStatement getIntArguments = null;
	private SQLiteStatement getRealArguments = null;
	private SQLiteStatement getTextArguments = null;
	
	
	
	public Database(String file, boolean write){
		db = new SQLiteConnection(new File(file));
		try {
			db.open(write);
			db.exec("PRAGMA synchronous=OFF");
			db.exec("PRAGMA temp_store=MEMORY");
			db.exec("PRAGMA journal_mode=TRUNCATE");
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
		if(write)
			open_write();
		else
			open_read();
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

	private void open_read(){
		try{
			getReplays = db.prepare("SELECT replay_id FROM Replays;");
			getTeams = db.prepare("SELECT team_id, Teams.name, tag, SideMap.name AS side FROM Teams, SideMap WHERE Teams.side = SideMap.side_id AND Teams.replay_id = ?;");
			getPlayers = db.prepare("SELECT player_id, Players.name, UnitTypeMap.name AS hero FROM Players, UnitTypeMap WHERE Players.hero = UnitTypeMap.type_id AND Players.team_id = ?;");
			getUnits = db.prepare("SELECT unit_id, UnitTypeMap.name, team, controlled_by_player, illusion FROM Units, UnitTypeMap WHERE Units.type = UnitTypeMap.type_id AND Units.replay_id = ?;");
			getUnit = db.prepare("SELECT UnitTypeMap.name, team, controlled_by_player, illusion FROM Units, UnitTypeMap WHERE Units.type = UnitTypeMap.type_id AND Units.unit_id = ?;");
			getTimeSeries = db.prepare("SELECT timeseries_id, TimeSeriesTypeMap.name FROM TimeSeries, TimeSeriesTypeMap WHERE TimeSeries.type = TimeSeriesTypeMap.type_id  AND TimeSeries.unit_id = ?;");
			getNodes = db.prepare("SELECT time, value FROM TimeSeriesNodes WHERE timeseries_id = ? ORDER BY time;");
			getEvents = db.prepare("SELECT event_id, time, EventTypeMap.name FROM Events, EventTypeMap WHERE Events.type = EventTYpeMap.type_id AND Events.replay_id = ?;");
			getIntArguments = db.prepare("SELECT EventArgumentMap.name AS argument, value FROM EventIntArguments, EventArgumentMap WHERE EventIntArguments.argument = EventArgumentMap.argument_id AND EventIntArguments.event_id = ?;");
			getRealArguments = db.prepare("SELECT EventArgumentMap.name AS argument, value FROM EventRealArguments, EventArgumentMap WHERE EventRealArguments.argument = EventArgumentMap.argument_id AND EventRealArguments.event_id = ?;");
			getTextArguments = db.prepare("SELECT EventArgumentMap.name AS argument, value FROM EventTextArguments, EventArgumentMap WHERE EventTextArguments.argument = EventArgumentMap.argument_id AND EventTextArguments.event_id = ?;");

			
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
	}
	
	private void open_write(){
		//Setup the DB
	    try {
			db.exec("BEGIN TRANSACTION;");

	    	db.exec("CREATE TABLE IF NOT EXISTS Replays (replay_id integer primary key);");
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
			
	    	db.exec("CREATE TABLE IF NOT EXISTS Events (event_id integer primary key, time real, type integer, replay_id integer);");
			db.exec("CREATE TABLE IF NOT EXISTS EventTypeMap (type_id integer primary key, name text);");
			writeConstants(db.prepare("INSERT OR IGNORE INTO EventTypeMap(type_id, name) VALUES(?, ?);"), Constants.eventTypes);
			db.exec("CREATE TABLE IF NOT EXISTS EventIntArguments (intargument_id integer primary key, event_id integer, argument integer, value integer);");
			db.exec("CREATE TABLE IF NOT EXISTS EventRealArguments (realargument_id integer primary key, event_id integer, argument integer, value real);");
			db.exec("CREATE TABLE IF NOT EXISTS EventTextArguments (stringargument_id integer primary key, event_id integer, argument integer, value text);");
			db.exec("CREATE TABLE IF NOT EXISTS EventArgumentMap (argument_id integer primary key, name text);");
			writeConstants(db.prepare("INSERT OR IGNORE INTO EventArgumentMap(argument_id, name) VALUES(?, ?);"), Constants.eventArguments);
			db.exec("CREATE TABLE IF NOT EXISTS EventCausalities (causality_id integer primary key, cause integer, effect integer);");
			

			db.exec("COMMIT TRANSACTION;");
			
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
	    
		try {
			insertReplay = db.prepare("INSERT INTO Replays(replay_id) VALUES(?);");
			insertTeam = db.prepare("INSERT INTO Teams(name, tag, side, replay_id) VALUES(?, ?, ?, ?);");
			insertPlayer = db.prepare("INSERT INTO Players(name, hero, team_id) VALUES(?, ?, ?);");
			insertUnit = db.prepare("INSERT INTO Units(type, team, controlled_by_player, illusion, replay_id) VALUES(?, ?, ?, ?, ?);");
			insertTimeSeries = db.prepare("INSERT INTO TimeSeries(type, unit_id) VALUES(?, ?);");
			insertTimeSeriesNode = db.prepare("INSERT INTO TimeSeriesNodes(timeseries_id, time, value) VALUES(?, ?, ?);");
			insertEvent = db.prepare("INSERT INTO Events(time, type, replay_id) VALUES(?, ?, ?);");
			insertIntArgument = db.prepare("INSERT INTO EventIntArguments(event_id, argument, value) VALUES(?, ?, ?);");
			insertRealArgument = db.prepare("INSERT INTO EventRealArguments(event_id, argument, value) VALUES(?, ?, ?);");
			insertTextArgument = db.prepare("INSERT INTO EventTextArguments(event_id, argument, value) VALUES(?, ?, ?);");
			insertEventCausality = db.prepare("INSERT INTO EventCausalities (cause, effect) VALUES(?, ?);");
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
	
	public int createEvent(int replay_id, double time, String type){
		try {
			insertEvent.bind(1, time).bind(2, Constants.eventTypes.get(type)).bind(3, replay_id);
			insertEvent.step();
			insertEvent.reset();
			return (int) db.getLastInsertId();
			
		} catch (SQLiteException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	public void addEventIntArgument(int event_id, String argument, int value){
		try {
			insertIntArgument.bind(1, event_id).bind(2, Constants.eventArguments.get(argument)).bind(3, value);
			insertIntArgument.step();
			insertIntArgument.reset();			
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
	}
	
	public void addEventRealArgument(int event_id, String argument, double value){
		try {
			insertRealArgument.bind(1, event_id).bind(2, Constants.eventArguments.get(argument)).bind(3, value);
			insertRealArgument.step();
			insertRealArgument.reset();			
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
	}
	
	public void addEventTextArgument(int event_id, String argument, String value){
		try {
			insertTextArgument.bind(1, event_id).bind(2, Constants.eventArguments.get(argument)).bind(3, value);
			insertTextArgument.step();
			insertTextArgument.reset();			
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
	}
	
	public void addEventCausality(int cause, int effect){
		try {
			insertEventCausality.bind(1, cause).bind(2, effect);
			insertEventCausality.step();
			insertEventCausality.reset();		
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
	}
	
	public boolean replayExists(int id){
		SQLiteStatement getReplay;
		boolean result = false;
		try {
			getReplay = db.prepare("SELECT * FROM Replays WHERE replay_id =?;");
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
			db.exec("DELETE FROM Replays WHERE Replays.replay_id ="+id+";");
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
	
	public List<Integer> getReplays(){
		List<Integer> replays = new LinkedList<Integer>();
		try {
			while(getReplays.step()){
				replays.add(getReplays.columnInt(0));
			}
			getReplays.reset();
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
		return replays;
	}
	
	public Replay getReplay(int id){
		List<Team> teams = new LinkedList<Team>();
		try {
			getTeams.bind(1, id);
			while(getTeams.step()){
				int teamID = getTeams.columnInt(0);
				List<Player> players = new LinkedList<Player>();
				getPlayers.bind(1, teamID);
				while(getPlayers.step()){
					players.add(new Player(getPlayers.columnInt(0), getPlayers.columnString(1), getPlayers.columnString(2)));
				}
				getPlayers.reset();
				teams.add(new Team(teamID, getTeams.columnString(1), getTeams.columnString(2), getTeams.columnString(3), players));
			}
			getTeams.reset();
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
		return new Replay(id, teams);
	}
	
	public List<UnitSummary> getUnits(Replay replay){
		List<UnitSummary> units = new LinkedList<UnitSummary>();
		try {
			getUnits.bind(1, replay.id);
			while(getUnits.step()){
				units.add(new UnitSummary(getUnits.columnInt(0), getUnits.columnString(1), getUnits.columnInt(2), getUnits.columnInt(3), getUnits.columnInt(4)==1));
			}
			getUnits.reset();
		} catch (SQLiteException e) {
			e.printStackTrace();
		}		
		return units;
	}
	
	public Unit getUnit(int id, Replay replay){
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
			
			return new Unit(unitType, replay.teamByIndex.get(team), controlledByPlayer == 0?null:replay.playerByIndex.get(controlledByPlayer), illusion, timeSeries);
		} catch (SQLiteException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Event> getEvents(Replay replay){
		List<Event> events = new LinkedList<Event>();
		try {
			getEvents.bind(1, replay.id);
			while(getEvents.step()){
				int eventID = getEvents.columnInt(0);
				List<EventArgumentInterface> arguments =  new  LinkedList<EventArgumentInterface>();
				
				getIntArguments.bind(1, eventID);
				while(getIntArguments.step())
					arguments.add(new EventArgument<Integer>(getIntArguments.columnString(0), getIntArguments.columnInt(1)));
				getIntArguments.reset();
				
				getRealArguments.bind(1, eventID);
				while(getRealArguments.step())
					arguments.add(new EventArgument<Double>(getRealArguments.columnString(0), getRealArguments.columnDouble(1)));
				getRealArguments.reset();
				
				getTextArguments.bind(1, eventID);
				while(getTextArguments.step())
					arguments.add(new EventArgument<String>(getTextArguments.columnString(0), getTextArguments.columnString(1)));
				getTextArguments.reset();
				
				events.add(new Event(getEvents.columnDouble(1), getEvents.columnString(2), arguments));
			}
			getEvents.reset();
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
		return events;
	}
}
