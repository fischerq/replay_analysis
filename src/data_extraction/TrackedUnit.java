package data_extraction;

import java.util.HashMap;
import java.util.Map;

import database.Constants;
import database.Database;
import skadistats.clarity.match.Match;
import skadistats.clarity.model.Entity;
import utils.ConstantMapper;
import utils.Utils;

class DataPoint{
	public double time;
	public double value;
	
	public DataPoint(double t, double v){
		time = t;
		value = v;
	}
}

public class TrackedUnit {
	private int handle;
	private int unitID;
	private boolean created;
	private int ownerHandle;
	private int controlID;
	private double creationTime;
	private ReplayData replay;
	private Database db;
	private Map<String, Integer> trackedTimeSeries;
	private Map<String, DataPoint> lastWrittenNode;
	
	
	public TrackedUnit(Entity unit, ReplayData replay, Database db){
		handle = unit.getHandle();
		this.db = db;
		this.replay = replay;
		created = false;
		creationTime = 0;
		ownerHandle = 0;
		controlID = 0;
		trackedTimeSeries = new HashMap<String, Integer>();
	}
	
	public boolean update(Match match, Match oldMatch){
		Entity e = match.getEntities().getByHandle(handle);
		Entity e_old = oldMatch.getEntities().getByHandle(handle);
		if(e == null)
			return false;
		
		boolean aliveNow = ConstantMapper.isAlive((Integer)e.getProperty("m_lifeState"));
		boolean aliveOld = false;
		if(e != null)
			aliveOld = ConstantMapper.isAlive((Integer)e_old.getProperty("m_lifeState"));
		
		if(created){
			if(aliveNow && !aliveOld){
				db.createEvent(replay.getReplayID(), Utils.getTime(match), "Spawn", 0, unitID, "");
				pushTimeSeries(match);
			}
			else if(!aliveNow && aliveOld){				
				db.createEvent(replay.getReplayID(), Utils.getTime(match), "Death", 0, unitID, "");
				pushTimeSeries(match);
			}
			else if(aliveNow && aliveOld){
				if(ownerHandle != 0 && (Integer)e.getProperty("m_hOwnerEntity") != ownerHandle){
					ownerHandle = 0;
					int controlSeries = db.createTimeSeries("Control", unitID);
					trackedTimeSeries.put("Control", controlSeries);
					db.addTimeSeriesNode(controlSeries, creationTime, controlID);
				}
				updateTimeSeries(match);
			}
		}
		else{
			if(aliveNow){
				created = true;
				controlID = 0;
				if((Integer)e.getProperty("m_hOwnerEntity") != 2097151){
					controlID = replay.getPlayerID((Integer) match.getEntities().getByHandle((Integer)e.getProperty("m_hOwnerEntity")).getProperty("m_iPlayerID"));
				}
				unitID = db.createUnit(ConstantMapper.nameForIndex((Integer)e.getProperty("m_iUnitNameIndex")),
							ConstantMapper.team((Integer)e.getProperty("m_iTeamNum")),
							controlID,
							Utils.isIllusion(e),
							replay.getReplayID());
				db.createEvent(replay.getReplayID(), Utils.getTime(match), "Spawn", 0, unitID, "");
				creationTime = Utils.getTime(match);
				ownerHandle = (Integer)e.getProperty("m_hOwnerEntity");
				createTimeSeries(e);
				pushTimeSeries(match);
			}
		}
		return true;
	}
	
	private void createTimeSeries(Entity e){
		for(String series : Constants.timeSeries.keySet()){
			if(trackSeriesForUnit(series, e)){
				int seriesID = db.createTimeSeries(series, unitID);
				trackedTimeSeries.put(series, seriesID);
			}
		}
	}
	
	private boolean trackSeriesForUnit(String series, Entity e){
		switch(series){
		case "PositionX":
		case "PositionY":
			return e.getProperty("m_cellX") != null && e.getProperty("m_cellY") != null && e.getProperty("m_vecOrigin") != null;
		case "Orientation":
			return e.getProperty("m_angRotation[1]") != null;
		case "Health":
			return e.getProperty("iHealth") != null || e.getProperty("m_iHealthPercentage") != null;
		case "Mana":
			return e.getProperty("m_flMana") != null;
		case "Control":
			return false;
		default:
			System.out.println("Checking unknown series type "+series);
			return false;
		}
	}
	
	private void pushTimeSeries(Match match){
		for(Map.Entry<String, Integer> series : trackedTimeSeries.entrySet()){
			db.addTimeSeriesNode(series.getValue(), Utils.getTime(match), getTimeSeriesValue(series.getKey(), match.getEntities().getByHandle(handle)));
		}
	}
	
	private void updateTimeSeries(Match match){
		for(Map.Entry<String, Integer> series : trackedTimeSeries.entrySet()){
			
		}
	}
	
	private double getTimeSeriesValue(String type, Entity e){
		switch(type){
		case "PositionX":
		case "PositionY":
		case "Orientation":
		case "Health":
		case "Mana":
		case "Control":
		default:
			System.out.println("Unknown TimeSeries: "+type);
			return 0;
		}
	}
}
