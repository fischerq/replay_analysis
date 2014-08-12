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
	
	private static double epsilon = 0.0001;
	
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
		lastWrittenNode = new HashMap<String, DataPoint>();
	}
	
	public boolean update(Match match, Match oldMatch){
		Entity e = match.getEntities().getByHandle(handle);
		Entity eOld = oldMatch.getEntities().getByHandle(handle);
		if(e == null)
			return false;
		
		boolean aliveNow = ConstantMapper.isAlive((Integer)e.getProperty("m_lifeState"));
		boolean aliveOld = false;
		if(eOld != null)
			aliveOld = ConstantMapper.isAlive((Integer)eOld.getProperty("m_lifeState"));
		
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
					db.makeUnitControlChanging(unitID);
					int controlSeries = db.createTimeSeries("Control", unitID);
					trackedTimeSeries.put("Control", controlSeries);
					db.addTimeSeriesNode(controlSeries, creationTime, controlID);
				}
				updateTimeSeries(match, oldMatch);
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
			return e.getDtClass().getPropertyIndex("m_cellX") != null &&
				e.getDtClass().getPropertyIndex("m_cellY") != null &&
				e.getDtClass().getPropertyIndex("m_vecOrigin") != null;
		case "Orientation":
			return e.getDtClass().getPropertyIndex("m_angRotation[1]") != null;
		case "Health":
			return e.getDtClass().getPropertyIndex("iHealth") != null || e.getDtClass().getPropertyIndex("m_iHealthPercentage") != null;
		case "Mana":
			return e.getDtClass().getPropertyIndex("m_flMana") != null;
		case "Control":
			return false;
		default:
			System.out.println("Checking unknown series type "+series);
			return false;
		}
	}
	
	private void pushTimeSeries(Match match){
		for(Map.Entry<String, Integer> entry : trackedTimeSeries.entrySet()){
			double time = Utils.getTime(match);
			double value = getTimeSeriesValue(entry.getKey(), match.getEntities().getByHandle(handle), match);
			db.addTimeSeriesNode(entry.getValue(), time, value);
			lastWrittenNode.put(entry.getKey(), new DataPoint(time, value));
		}
	}
	
	private void updateTimeSeries(Match match, Match old){
		for(Map.Entry<String, Integer> entry : trackedTimeSeries.entrySet()){
			double timeOld = Utils.getTime(old);
			DataPoint lastNode = lastWrittenNode.get(entry.getKey()); 
			if(lastNode.time == timeOld)
				continue;
			else{
				double valueOld = getTimeSeriesValue(entry.getKey(), old.getEntities().getByHandle(handle), old);
				double derivativePrev = (valueOld - lastNode.value)/(timeOld - lastNode.time);
				
				double timeNow = Utils.getTime(match);
				double valueNow = getTimeSeriesValue(entry.getKey(), match.getEntities().getByHandle(handle), match);
				double derivativeNew = (valueNow - valueOld)/(timeNow - timeOld);
				if(Math.abs(derivativePrev - derivativeNew) > epsilon){
					if(entry.getKey() == "Mana")System.out.println("Writing node "+entry.getKey()+" "+derivativePrev +" "+derivativeNew);
					db.addTimeSeriesNode(entry.getValue(), timeOld, valueOld);
					lastWrittenNode.put(entry.getKey(), new DataPoint(timeOld, valueOld));
				}
			}
		}
	}
	
	private double getTimeSeriesValue(String type, Entity e, Match match){
		double[] position = null;
		switch(type){
		case "PositionX":
			position = Utils.getPosition(e);
			return position[0];
		case "PositionY":
			position = Utils.getPosition(e);
			return position[1];
		case "Orientation":
			return (Float)e.getProperty("m_angRotation[1]")/360.0;
		case "Health":
			if(e.getProperty("m_iHealthPercentage") != null){
				return Math.floor((Integer)e.getProperty("m_iHealthPercentage")/127.0*(Integer)e.getProperty("m_iMaxHealth"));
			}
			else 
				return (Integer)e.getProperty("m_iHealthPercentage");
		case "Mana":
			return (Float)e.getProperty("m_flMana");
		case "Control":
			if((Integer)e.getProperty("m_hOwnerEntity") == 2097151)
			return 0;
			else{
				int playerID = match.getEntities().getByHandle((Integer)e.getProperty("m_hOwnerEntity")).getProperty("m_iPlayerID");
				return replay.getPlayerID(playerID);
			} 
		default:
			System.out.println("Unknown TimeSeries: "+type);
			return 0;
		}
	}
}
