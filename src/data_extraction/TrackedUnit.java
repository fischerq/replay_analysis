package data_extraction;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.vecmath.Vector2f;

import database.Constants;
import database.Database;
import skadistats.clarity.match.Match;
import skadistats.clarity.model.Entity;
import utils.ConstantMapper;
import utils.Utils;

class Interval{
	public double min;
	public double max;
	
	public Interval(double min, double max){
		this.min = min;
		this.max = max;
	}
	
	public static Interval rounded(int value){
		return new Interval(value-0.5, value+0.5);
	}
	
	public static Interval floored(int value){
		return new Interval(value, value+0.999);
	}
	
	public static Interval intersection(Interval a, Interval b){
		if(a.min > b.max || a.max < b.min )
			return null;
		else
			return new Interval(Math.max(a.min, b.min), Math.min(a.max, b.max));
	}
	
	public String toString(){
		return "["+min+", "+max+"]";
	}
}

class DataPoint{
	public double time;
	public double value;
	public Interval intervalValues;
	public Interval derivativeInterval;
	
	public DataPoint(double t, double v, Interval deQuantized){
		time = t;
		value = v;
		intervalValues = deQuantized;
		derivativeInterval = null;
	}
	
	public String toString(){
		return time+": V="+value+intervalValues.toString()+" D "+(derivativeInterval==null?"[]":derivativeInterval.toString());
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
		lastWrittenNode = new HashMap<String, DataPoint>();
	}
	
	public int getID(){
		return unitID;
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
				int eventID = db.createEvent(replay.getReplayID(), Utils.getTime(match), "Spawn");
				db.addEventIntArgument(eventID, "Unit", unitID);
				pushTimeSeries(match);
			}
			else if(!aliveNow && aliveOld){				
				int eventID = db.createEvent(replay.getReplayID(), Utils.getTime(match), "Death");
				db.addEventIntArgument(eventID, "Unit", unitID);
				pushTimeSeries(match);
			}
			else if(aliveNow && aliveOld){
				if(ownerHandle != 0 && (Integer)e.getProperty("m_hOwnerEntity") != ownerHandle){
					ownerHandle = 0;
					db.makeUnitControlChanging(unitID);
					int controlSeries = db.createTimeSeries("Control", unitID);
					trackedTimeSeries.put("Control", controlSeries);
					db.addTimeSeriesNode(controlSeries, creationTime, controlID);
					DataPoint dp = new DataPoint(creationTime, controlID, new Interval(controlID, controlID));
					dp.derivativeInterval = new Interval(0, 0);
					lastWrittenNode.put("Control", dp);
				}
				updateTimeSeries(match, oldMatch);
				checkVisibility(match, oldMatch);
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
				int eventID = db.createEvent(replay.getReplayID(), Utils.getTime(match), "Spawn");
				db.addEventIntArgument(eventID, "Unit", unitID);
				creationTime = Utils.getTime(match);
				ownerHandle = (Integer)e.getProperty("m_hOwnerEntity");
				createTimeSeries(e);
				pushTimeSeries(match);
			}
		}
		return true;
	}
	
	private void checkVisibility(Match match, Match oldMatch){
		int visibilityNow = match.getEntities().getByHandle(handle).getProperty("m_iTaggedAsVisibleByTeam");
		int visibilityOld = oldMatch.getEntities().getByHandle(handle).getProperty("m_iTaggedAsVisibleByTeam");
		boolean[] radiantVision = new boolean[2];
		boolean[] direVision = new boolean[2];
		radiantVision[0] = ((visibilityNow>>2) & 1) > 0;
		radiantVision[1] = ((visibilityOld>>2) & 1) > 0;
		direVision[0] = ((visibilityNow>>3) & 1) > 0;
		direVision[1] = ((visibilityOld>>3) & 1) > 0;
		if(radiantVision[0] && !radiantVision[1]){
			int eventID = db.createEvent(replay.getReplayID(), Utils.getTime(match), "VisionGain");
			db.addEventIntArgument(eventID, "Side", Constants.sides.get("Radiant"));
			db.addEventIntArgument(eventID, "Unit", unitID);
		}
		else if(!radiantVision[0] && radiantVision[1]){
			int eventID = db.createEvent(replay.getReplayID(), Utils.getTime(match), "VisionLoss");
			db.addEventIntArgument(eventID, "Side", Constants.sides.get("Radiant"));
			db.addEventIntArgument(eventID, "Unit", unitID);
		}
		if(direVision[0] && !direVision[1]){
			int eventID = db.createEvent(replay.getReplayID(), Utils.getTime(match), "VisionGain");
			db.addEventIntArgument(eventID, "Side", Constants.sides.get("Dire"));
			db.addEventIntArgument(eventID, "Unit", unitID);
		}
		else if(!direVision[0] && direVision[1]){
			int eventID = db.createEvent(replay.getReplayID(), Utils.getTime(match), "VisionLoss");
			db.addEventIntArgument(eventID, "Side", Constants.sides.get("Dire"));
			db.addEventIntArgument(eventID, "Unit", unitID);
		}	
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
			return e.getDtClass().getPropertyIndex("m_iHealth") != null || e.getDtClass().getPropertyIndex("m_iHealthPercentage") != null;
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
			DataPoint node =  getTimeSeriesValue(entry.getKey(), match);
			db.addTimeSeriesNode(entry.getValue(), node.time, node.value);
			lastWrittenNode.put(entry.getKey(), node);
		}
	}
	
	private void updateTimeSeries(Match match, Match old){
		for(Map.Entry<String, Integer> entry : trackedTimeSeries.entrySet()){
			DataPoint lastNode = lastWrittenNode.get(entry.getKey());
			DataPoint nodeOld = getTimeSeriesValue(entry.getKey(), old);
			DataPoint nodeNow = getTimeSeriesValue(entry.getKey(), match);

			if(lastNode.time == nodeOld.time){
				if(lastNode.derivativeInterval == null){
					double deltaT = nodeNow.time - nodeOld.time;
					lastNode.derivativeInterval = new Interval((nodeNow.intervalValues.min - nodeOld.intervalValues.max)/deltaT, (nodeNow.intervalValues.max - nodeOld.intervalValues.min)/deltaT);
				}
				else{
					System.out.println("Sth strange happened");
				}
				continue;
			}
			else{
				double mergedDeltaT = nodeNow.time - lastNode.time;
				Interval mergedDerivativeInterval = new Interval((nodeNow.intervalValues.min - lastNode.intervalValues.max)/mergedDeltaT,
																(nodeNow.intervalValues.max - lastNode.intervalValues.min)/mergedDeltaT);
				Interval updatedDerivativeInterval = Interval.intersection(lastNode.derivativeInterval, mergedDerivativeInterval);
				if(updatedDerivativeInterval == null){
					/*if(entry.getKey() == "Mana")
						System.out.println("Writing Mana node.\n\t"+lastNode.toString()+"\n\t"+nodeOld.toString()+"\n\t"+nodeNow.toString());*/
					db.addTimeSeriesNode(entry.getValue(), nodeOld.time, nodeOld.value);
					double deltaT = nodeNow.time - nodeOld.time;
					nodeOld.derivativeInterval = new Interval((nodeNow.intervalValues.min - nodeOld.intervalValues.max)/deltaT, (nodeNow.intervalValues.max - nodeOld.intervalValues.min)/deltaT);
					lastWrittenNode.put(entry.getKey(), nodeOld);
				}
				else{
					lastNode.derivativeInterval = mergedDerivativeInterval;
				}
			}
		}
	}
	
	private DataPoint getTimeSeriesValue(String type, Match match){
		Entity e = match.getEntities().getByHandle(handle);
		double time = Utils.getTime(match);
		Vector2f position = null;
		
		switch(type){
		case "PositionX":
			position = Utils.getPosition(e);
			return new DataPoint(time, position.x, Interval.rounded((int) position.x));
		case "PositionY":
			position = Utils.getPosition(e);
			return new DataPoint(time, position.y, Interval.rounded((int) position.y));
		case "Orientation":
			double orientation = (Float)e.getProperty("m_angRotation[1]")/360.0;
			return new DataPoint(time, orientation, new Interval(orientation, orientation));
		case "Health":
			if(e.getDtClass().getPropertyIndex("m_iHealthPercentage") != null){
				double healthPercent = (Integer)e.getProperty("m_iMaxHealth")/127.0;
				double health = Math.floor((Integer)e.getProperty("m_iHealthPercentage")*healthPercent);
				return new DataPoint(time, health, new Interval(health-0.5*healthPercent, health+0.5*healthPercent));
			}
			else {
				int health = (Integer)e.getProperty("m_iHealth");
				return new DataPoint(time, health, Interval.rounded(health));
			}
		case "Mana":
			int mana = (int) Math.floor((Float)e.getProperty("m_flMana"));
			return new DataPoint(time, mana, Interval.rounded(mana));
		case "Control":
			int dbID;
			if((Integer)e.getProperty("m_hOwnerEntity") == 2097151)
				dbID = 0;
			else{
				int playerID = match.getEntities().getByHandle((Integer)e.getProperty("m_hOwnerEntity")).getProperty("m_iPlayerID");
				dbID = replay.getPlayerID(playerID);
			} 
			return new DataPoint(time, dbID, new Interval(dbID, dbID));
		default:
			System.out.println("Unknown TimeSeries: "+type);
			return null;
		}
	}

	public void checkModifiers(Match match, Match oldMatch,
			Map<String, LinkedList<TrackedUnit>> modifierChanges, Map<String, LinkedList<TrackedUnit>> modifierLosses) {
		Entity e = match.getEntities().getByHandle(handle);
		Entity eOld = oldMatch.getEntities().getByHandle(handle);
		if(eOld == null || e == null)
			return;
		
		if(e.getProperty("m_ModifierManager.m_hModifierParent") != null){
			Entity modifierParent = match.getEntities().getByHandle((int)e.getProperty("m_ModifierManager.m_hModifierParent"));
			Entity modifierParentOld = oldMatch.getEntities().getByHandle((int)e.getProperty("m_ModifierManager.m_hModifierParent"));
			//System.out.println("Modifier parent: "+modifierParent.toString());
		}
	}

	public void updateInventory(Match match, Match oldMatch) {
		Entity e = match.getEntities().getByHandle(handle);
		Entity eOld = oldMatch.getEntities().getByHandle(handle);
		
		if(eOld == null || e == null)
			return;
		System.out.println("Checking inventory "+e.getDtClass().getDtName());
		Integer[] items = e.getArrayProperty(Integer.class, "m_hItems");
		Integer[] itemsOld = eOld.getArrayProperty(Integer.class, "m_hItems");
		if(items != null && itemsOld != null){
			if(items.length != itemsOld.length)
				System.out.println("Item num different?"+items.length +" "+ itemsOld.length);
			for(int i = 0; i< items.length; ++i){
				if(items[i] != itemsOld[i]){
					Entity itemNew = match.getEntities().getByHandle(items[i]);
					Entity itemOld = oldMatch.getEntities().getByHandle(itemsOld[i]);
					if(itemNew == null)
						System.out.println("Lost Item "+itemOld.getDtClass().getDtName());
					else if(itemOld == null)
						System.out.println("Got Item "+itemNew.getDtClass().getDtName());
					else
						System.out.println("Item changed "+itemOld.getDtClass().getDtName()+" "+itemNew.getDtClass().getDtName());
				}
			}
				
		}
	}
}
