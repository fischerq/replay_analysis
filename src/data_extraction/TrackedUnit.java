package data_extraction;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.vecmath.Vector2f;





import database.Constants;
import database.Database;
import skadistats.clarity.match.Match;
import skadistats.clarity.model.Entity;
import skadistats.clarity.model.ModifierTableEntry;
import skadistats.clarity.model.ReceiveProp;
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

	private Vector2f position;
	private Vector2f oldPosition;
	private boolean denieable;
	
	boolean hasInventory;
	
	private int[] abilityIDs;
	private Match currentMatch;
	
	enum LifeChange{
		NOTHING, 
		SPAWN,
		DEATH
	};
	private LifeChange lastLifeChange;
	
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
		lastLifeChange = LifeChange.NOTHING;
		position = null;
		oldPosition = null;
		denieable = false;
		System.out.println("reset "+handle);
		abilityIDs = new int[0];
		hasInventory = true;
	}
	
	public int getHandle(){
		return handle;
	}
	
	public int getID(){
		return unitID;
	}
	
	public LifeChange getLifeChange(){
		return lastLifeChange;
	}
	
	public Vector2f getPosition(){
		return position;
	}
	public Vector2f getOldPosition(){
		return oldPosition;
	}
	
	public boolean update(Match match, Match oldMatch){
		currentMatch = match;
		
		Entity e = match.getEntities().getByHandle(handle);
		Entity eOld = oldMatch.getEntities().getByHandle(handle);
		if(e == null)
			return false;
		
		boolean aliveNow = ConstantMapper.isAlive((Integer)e.getProperty("m_lifeState"));
		boolean aliveOld = false;
		if(eOld != null)
			aliveOld = ConstantMapper.isAlive((Integer)eOld.getProperty("m_lifeState"));
		
		if(aliveNow){
			oldPosition = position;
			position = Utils.getPosition(e);
			if(oldPosition == null)
				oldPosition = position;
		}
	
		if(created){
			if(aliveNow && !aliveOld){
				lastLifeChange = LifeChange.SPAWN;
				int eventID = db.createEvent(replay.getReplayID(), Utils.getTime(match), "Spawn");
				db.addEventIntArgument(eventID, "Unit", unitID);
				pushTimeSeries(match);
			}
			else if(!aliveNow && aliveOld){	
				lastLifeChange = LifeChange.DEATH;
				int eventID = db.createEvent(replay.getReplayID(), Utils.getTime(match), "Death");
				db.addEventIntArgument(eventID, "Unit", unitID);
				pushTimeSeries(match);
				position = null;
				oldPosition = null;
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
				updateDenieable(match);
				if(hasInventory)
					updateInventory(match, oldMatch);
				updateAbilities(match, oldMatch);
			}
		}
		else{
			if(aliveNow){
				created = true;
				controlID = 0;
				if((Integer)e.getProperty("m_hOwnerEntity") != 2097151){
					if(match.getEntities().getByHandle((Integer)e.getProperty("m_hOwnerEntity")) == null)
						System.out.println("Owner is null");
					else
						controlID = replay.getPlayerID((Integer) match.getEntities().getByHandle((Integer)e.getProperty("m_hOwnerEntity")).getProperty("m_iPlayerID"));
				}
				unitID = db.createUnit(ConstantMapper.nameForIndex((Integer)e.getProperty("m_iUnitNameIndex")),
							ConstantMapper.team((Integer)e.getProperty("m_iTeamNum")),
							controlID,
							Utils.isIllusion(e),
							replay.getReplayID());
				lastLifeChange = LifeChange.SPAWN;
				int eventID = db.createEvent(replay.getReplayID(), Utils.getTime(match), "Spawn");
				db.addEventIntArgument(eventID, "Unit", unitID);
				creationTime = Utils.getTime(match);
				ownerHandle = (Integer)e.getProperty("m_hOwnerEntity");
				createTimeSeries(e);
				pushTimeSeries(match);
				
				if(e.getDtClass().getPropertyIndex("m_Inventory.m_hItems.0000") == null)
					hasInventory = false;
				else
					hasInventory = true;
				//For initialization
				updateAbilities(match, oldMatch);
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
			db.addEventIntArgument(eventID, "Side", Constants.getIndex("Sides", "Radiant"));
			db.addEventIntArgument(eventID, "Unit", unitID);
		}
		else if(!radiantVision[0] && radiantVision[1]){
			int eventID = db.createEvent(replay.getReplayID(), Utils.getTime(match), "VisionLoss");
			db.addEventIntArgument(eventID, "Side", Constants.getIndex("Sides", "Radiant"));
			db.addEventIntArgument(eventID, "Unit", unitID);
		}
		if(direVision[0] && !direVision[1]){
			int eventID = db.createEvent(replay.getReplayID(), Utils.getTime(match), "VisionGain");
			db.addEventIntArgument(eventID, "Side", Constants.getIndex("Sides", "Dire"));
			db.addEventIntArgument(eventID, "Unit", unitID);
		}
		else if(!direVision[0] && direVision[1]){
			int eventID = db.createEvent(replay.getReplayID(), Utils.getTime(match), "VisionLoss");
			db.addEventIntArgument(eventID, "Side", Constants.getIndex("Sides", "Dire"));
			db.addEventIntArgument(eventID, "Unit", unitID);
		}	
	}
	
	private void createTimeSeries(Entity e){
		for(String series : Constants.getAllValues("TimeSeries")){
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
		
		switch(type){
		case "PositionX":
			return new DataPoint(time, position.x, Interval.rounded((int) position.x));
		case "PositionY":
			return new DataPoint(time, position.y, Interval.rounded((int) position.y));
		case "Orientation":
			double orientation = (Float)e.getProperty("m_angRotation[1]")/360.0*2*Math.PI;
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

	private void updateInventory(Match match, Match oldMatch) {
		Entity e = match.getEntities().getByHandle(handle);
		Entity eOld = oldMatch.getEntities().getByHandle(handle);
		
		if(eOld == null || e == null )
			return;
		//System.out.println("Checking inventory "+e.getDtClass().getDtName());
		//System.out.println(e.toString());
		int[] items = new int[14];
		items[0] = e.getProperty("m_Inventory.m_hItems.0000");
		items[1] = e.getProperty("m_Inventory.m_hItems.0001");
		items[2] = e.getProperty("m_Inventory.m_hItems.0002");
		items[3] = e.getProperty("m_Inventory.m_hItems.0003");
		items[4] = e.getProperty("m_Inventory.m_hItems.0004");
		items[5] = e.getProperty("m_Inventory.m_hItems.0005");
		items[6] = e.getProperty("m_Inventory.m_hItems.0006");
		items[7] = e.getProperty("m_Inventory.m_hItems.0007");
		items[8] = e.getProperty("m_Inventory.m_hItems.0008");
		items[9] = e.getProperty("m_Inventory.m_hItems.0009");
		items[10] = e.getProperty("m_Inventory.m_hItems.0010");
		items[11] = e.getProperty("m_Inventory.m_hItems.0011");
		items[12] = e.getProperty("m_Inventory.m_hItems.0012");
		items[13] = e.getProperty("m_Inventory.m_hItems.0013");

		int[] itemsOld = new int[14];
		itemsOld[0] = eOld.getProperty("m_Inventory.m_hItems.0000");
		itemsOld[1] = eOld.getProperty("m_Inventory.m_hItems.0001");
		itemsOld[2] = eOld.getProperty("m_Inventory.m_hItems.0002");
		itemsOld[3] = eOld.getProperty("m_Inventory.m_hItems.0003");
		itemsOld[4] = eOld.getProperty("m_Inventory.m_hItems.0004");
		itemsOld[5] = eOld.getProperty("m_Inventory.m_hItems.0005");
		itemsOld[6] = eOld.getProperty("m_Inventory.m_hItems.0006");
		itemsOld[7] = eOld.getProperty("m_Inventory.m_hItems.0007");
		itemsOld[8] = eOld.getProperty("m_Inventory.m_hItems.0008");
		itemsOld[9] = eOld.getProperty("m_Inventory.m_hItems.0009");
		itemsOld[10] = eOld.getProperty("m_Inventory.m_hItems.0010");
		itemsOld[11] = eOld.getProperty("m_Inventory.m_hItems.0011");
		itemsOld[12] = eOld.getProperty("m_Inventory.m_hItems.0012");
		itemsOld[13] = eOld.getProperty("m_Inventory.m_hItems.0013");
		//itemsOld.getArrayProperty(Integer.class, "m_Inventory.m_hItems");
		if(items != null && itemsOld != null){
			for(int i = 0; i< items.length; ++i){
				//System.out.println(itemsOld[i]+" "+items[i]);
				if((int)items[i] == 2097151 && (int)itemsOld[i] == 2097151)
					continue;
				Entity itemNew = match.getEntities().getByHandle(items[i]);
				Entity itemOld = oldMatch.getEntities().getByHandle(itemsOld[i]);
				if(items[i] != itemsOld[i]){
					boolean found = false;
					int index = -1;
					if(itemNew == null){
						for(int j = 0; j < items.length; ++j)
							if(items[j] == itemsOld[i]){
								found = true;
								index = j;
								break;
							}
						if(!found){
							int eventID = db.createEvent(replay.getReplayID(), Utils.getTime(match), "ItemLoss");
							db.addEventIntArgument(eventID, "Unit", unitID);
							db.addEventIntArgument(eventID, "Item", Constants.getIndex("Items", ConstantMapper.itemName((String)itemOld.getProperty("m_iName"))));
	
							//db.addEventIntArgument(eventID, "Item", Constants.items.get(ConstantMapper.itemName((String)itemOld.getProperty("m_iName"))));
							db.addEventIntArgument(eventID, "InventorySlot", Constants.getIndex("InventorySlots", ConstantMapper.inventorySlotName(i)));
							//System.out.println(e.getDtClass().getDtName()+" Lost Item "+ConstantMapper.itemName((String)itemOld.getProperty("m_iName")));
						}
						else if(index > i){
							int eventID = db.createEvent(replay.getReplayID(), Utils.getTime(match), "ItemMove");
							db.addEventIntArgument(eventID, "Unit", unitID);
							db.addEventIntArgument(eventID, "Item", Constants.getIndex("Items", ConstantMapper.itemName((String)itemOld.getProperty("m_iName"))));
							db.addEventIntArgument(eventID, "InventorySlot", Constants.getIndex("InventorySlots", ConstantMapper.inventorySlotName(index)));
							db.addEventIntArgument(eventID, "InventorySlotOrigin", Constants.getIndex("InventorySlots", ConstantMapper.inventorySlotName(i)));
							//System.out.println(e.getDtClass().getDtName()+" Moved Item "+ConstantMapper.itemName((String)itemOld.getProperty("m_iName"))+" from "+i+" to "+index);
						}
					}
					else if(itemOld == null){
						for(int j = 0; j < itemsOld.length; ++j)
							if(itemsOld[j] == items[i]){
								found = true;
								index = j;
								break;
							}
						if(!found){
							int eventID = db.createEvent(replay.getReplayID(), Utils.getTime(match), "ItemAddition");
							db.addEventIntArgument(eventID, "Unit", unitID);
							db.addEventIntArgument(eventID, "Item", Constants.getIndex("Items", ConstantMapper.itemName((String)itemNew.getProperty("m_iName"))));
							db.addEventIntArgument(eventID, "InventorySlot", Constants.getIndex("InventorySlots", ConstantMapper.inventorySlotName(i)));
							//System.out.println(e.getDtClass().getDtName()+" Got Item "+ConstantMapper.itemName((String)itemNew.getProperty("m_iName")));
						}
						else if(index > i){
							int eventID = db.createEvent(replay.getReplayID(), Utils.getTime(match), "ItemMove");
							db.addEventIntArgument(eventID, "Unit", unitID);
							db.addEventIntArgument(eventID, "Item", Constants.getIndex("Items", ConstantMapper.itemName((String)itemNew.getProperty("m_iName"))));
							db.addEventIntArgument(eventID, "InventorySlot", Constants.getIndex("InventorySlots", ConstantMapper.inventorySlotName(i)));
							db.addEventIntArgument(eventID, "InventorySlotOrigin", Constants.getIndex("InventorySlots", ConstantMapper.inventorySlotName(index)));
							//System.out.println(e.getDtClass().getDtName()+" Moved Item "+ConstantMapper.itemName((String)itemNew.getProperty("m_iName"))+" from "+index+" to "+i);
						}
					}
					else{
						for(int j = 0; j < items.length; ++j)
							if(items[j] == itemsOld[i] && itemsOld[j] == items[i]){
								found = true;
								index = j;
								break;
							}
						if(!found){
							int eventID2 = db.createEvent(replay.getReplayID(), Utils.getTime(match), "ItemLoss");
							db.addEventIntArgument(eventID2, "Unit", unitID);
							db.addEventIntArgument(eventID2, "Item", Constants.getIndex("Items", ConstantMapper.itemName((String)itemOld.getProperty("m_iName"))));
							db.addEventIntArgument(eventID2, "InventorySlot", Constants.getIndex("InventorySlots", ConstantMapper.inventorySlotName(i)));
	
							int eventID = db.createEvent(replay.getReplayID(), Utils.getTime(match), "ItemAddition");
							db.addEventIntArgument(eventID, "Unit", unitID);
							db.addEventIntArgument(eventID, "Item", Constants.getIndex("Items", ConstantMapper.itemName((String)itemNew.getProperty("m_iName"))));
							db.addEventIntArgument(eventID, "InventorySlot", Constants.getIndex("InventorySlots", ConstantMapper.inventorySlotName(i)));
	
							//System.out.println(e.getDtClass().getDtName()+" Lost&Got Item "+ConstantMapper.itemName((String)itemNew.getProperty("m_iName"))+" for "+ConstantMapper.itemName((String)itemOld.getProperty("m_iName"))+"("+i+")");
						}
						else if(index > i){
							int eventID = db.createEvent(replay.getReplayID(), Utils.getTime(match), "ItemMove");
							db.addEventIntArgument(eventID, "Unit", unitID);
							db.addEventIntArgument(eventID, "Item", Constants.getIndex("Items", ConstantMapper.itemName((String)itemOld.getProperty("m_iName"))));
							db.addEventIntArgument(eventID, "InventorySlot", Constants.getIndex("InventorySlots", ConstantMapper.inventorySlotName(index)));
							db.addEventIntArgument(eventID, "InventorySlotOrigin", Constants.getIndex("InventorySlots", ConstantMapper.inventorySlotName(i)));
							
							int eventID2 = db.createEvent(replay.getReplayID(), Utils.getTime(match), "ItemMove");
							db.addEventIntArgument(eventID2, "Unit", unitID);
							db.addEventIntArgument(eventID2, "Item", Constants.getIndex("Items", ConstantMapper.itemName((String)itemNew.getProperty("m_iName"))));
							db.addEventIntArgument(eventID2, "InventorySlot", Constants.getIndex("InventorySlots", ConstantMapper.inventorySlotName(i)));
							db.addEventIntArgument(eventID2, "InventorySlotOrigin", Constants.getIndex("InventorySlots", ConstantMapper.inventorySlotName(index)));
							//System.out.println(e.getDtClass().getDtName()+" Swapped Items "+ConstantMapper.itemName((String)itemOld.getProperty("m_iName"))+"("+i+") with "+ConstantMapper.itemName((String)itemNew.getProperty("m_iName"))+"("+index+")");
						}
					}
				}
				else{
					if(!((Integer)itemNew.getProperty("m_iCurrentCharges")).equals((Integer)itemOld.getProperty("m_iCurrentCharges"))){
						//System.out.println(e.getDtClass().getDtName()+"Charges changed: "+ConstantMapper.itemName((String)itemOld.getProperty("m_iName"))+": "+(Integer)itemOld.getProperty("m_iCurrentCharges")+"->"+(Integer)itemNew.getProperty("m_iCurrentCharges"));
						int eventID = db.createEvent(replay.getReplayID(), Utils.getTime(match), "ItemChargeChange");
						db.addEventIntArgument(eventID, "Unit", unitID);
						db.addEventIntArgument(eventID, "Item", Constants.getIndex("Items", ConstantMapper.itemName((String)itemOld.getProperty("m_iName"))));
						db.addEventIntArgument(eventID, "InventorySlot", Constants.getIndex("InventorySlots", ConstantMapper.inventorySlotName(i)));
						db.addEventIntArgument(eventID, "Amount", (Integer)itemNew.getProperty("m_iCurrentCharges"));
					}
					if(!((Integer)itemNew.getProperty("m_bToggleState")).equals((Integer)itemOld.getProperty("m_bToggleState"))){
						int eventID = db.createEvent(replay.getReplayID(), Utils.getTime(match), "ItemToggle");
						db.addEventIntArgument(eventID, "Unit", unitID);
						db.addEventIntArgument(eventID, "Item", Constants.getIndex("Items", ConstantMapper.itemName((String)itemOld.getProperty("m_iName"))));
						db.addEventIntArgument(eventID, "InventorySlot", Constants.getIndex("InventorySlots", ConstantMapper.inventorySlotName(i)));
						db.addEventIntArgument(eventID, "ToggleState", Constants.getIndex("ToggleStates", ConstantMapper.toggleName((Integer)itemNew.getProperty("m_bToggleState"))));
						//System.out.println(e.getDtClass().getDtName()+"Toggle changed: "+ConstantMapper.itemName((String)itemOld.getProperty("m_iName"))+": "+(Integer)itemOld.getProperty("m_bToggleState")+"->"+(Integer)itemNew.getProperty("m_bToggleState"));
					}
					if(itemNew.getDtClass().getDtName().equals("DT_DOTA_Item_PowerTreads") && !((Integer)itemNew.getProperty("m_iStat")).equals((Integer)itemOld.getProperty("m_iStat"))){
						//System.out.println(e.getDtClass().getDtName()+"Treads toggled: "+ConstantMapper.itemName((String)itemOld.getProperty("m_iName"))+": "+(Integer)itemOld.getProperty("m_iStat")+"->"+(Integer)itemNew.getProperty("m_iStat"));
						int eventID = db.createEvent(replay.getReplayID(), Utils.getTime(match), "ItemToggle");
						db.addEventIntArgument(eventID, "Unit", unitID);
						db.addEventIntArgument(eventID, "Item", Constants.getIndex("Items", ConstantMapper.itemName((String)itemOld.getProperty("m_iName"))));
						db.addEventIntArgument(eventID, "InventorySlot", Constants.getIndex("InventorySlots", ConstantMapper.inventorySlotName(i)));
						db.addEventIntArgument(eventID, "ToggleState", Constants.getIndex("ToggleStates", ConstantMapper.treadsToggle((Integer)itemNew.getProperty("m_iStat"))));
					}
					
					for(ReceiveProp p : itemNew.getDtClass().getReceiveProps())
						printPropChange(itemNew, itemOld, p.getVarName());
				}
			}
				
		}
	}
	
	public void updateDenieable(Match match){
		Entity e = match.getEntities().getByHandle(handle);
		int unitNameIndex = e.getProperty("m_iUnitNameIndex");
		if(unitNameIndex <= 115 ){//Heroes
			denieable =  true;
		}
		else if(unitNameIndex >= 124 && unitNameIndex <= 157){//Buildings
			if(Utils.getHealth(e) < (int)e.getProperty("m_iMaxHealth")/10)
				denieable =  true;
			else
				denieable =  false;
		}
		else if(unitNameIndex >= 116 && unitNameIndex <= 123 || unitNameIndex >= 160 && unitNameIndex <= 163){
			if(Utils.getHealth(e) < (int)e.getProperty("m_iMaxHealth")/2)
				denieable =  true;
			else
				denieable =  false;
		}
	}
	
	public boolean isDenieable(){
		return denieable;
	}
	
	private void printPropChange(Entity e, Entity old, String name){
		//System.out.println(name+" "+e.getProperty(name)+" "+old.getProperty(name)+" "+e.getProperty(name).equals(old.getProperty(name)));
		if(!e.getProperty(name).equals(old.getProperty(name)))
			System.out.println(ConstantMapper.formatTime(Utils.getTime(currentMatch))+" "+name+" "+old.getProperty(name)+" -> "+e.getProperty(name));
	}
	
	private void updateAbilities(Match match, Match oldMatch){
		Entity e = match.getEntities().getByHandle(handle);
		Entity eOld = oldMatch.getEntities().getByHandle(handle);
		
		if(eOld == null && e != null){
			System.out.println("init "+handle);
			//System.out.println(e.toString());
			int[] abilities_extract = new int[16];
			abilities_extract[0] = e.getProperty("m_hAbilities.0000");
			abilities_extract[1] = e.getProperty("m_hAbilities.0001");
			abilities_extract[2] = e.getProperty("m_hAbilities.0002");
			abilities_extract[3] = e.getProperty("m_hAbilities.0003");
			abilities_extract[4] = e.getProperty("m_hAbilities.0004");
			abilities_extract[5] = e.getProperty("m_hAbilities.0005");
			abilities_extract[6] = e.getProperty("m_hAbilities.0006");
			abilities_extract[7] = e.getProperty("m_hAbilities.0007");
			abilities_extract[8] = e.getProperty("m_hAbilities.0008");
			abilities_extract[9] = e.getProperty("m_hAbilities.0009");
			abilities_extract[10] = e.getProperty("m_hAbilities.0010");
			abilities_extract[11] = e.getProperty("m_hAbilities.0011");
			abilities_extract[12] = e.getProperty("m_hAbilities.0012");
			abilities_extract[13] = e.getProperty("m_hAbilities.0013");
			abilities_extract[14] = e.getProperty("m_hAbilities.0014");
			abilities_extract[15] = e.getProperty("m_hAbilities.0015");
			int nAbilities = 0;
			for(int i = 0; i < 16;++i)
				if(abilities_extract[i] == 2097151){
					nAbilities = i;
					break;
				}
			abilityIDs = new int[nAbilities];
			for(int i = 0; i< nAbilities; ++i){
				abilityIDs[i] = abilities_extract[i];
				Entity ability = match.getEntities().getByHandle(abilityIDs[i]);
				//System.out.println(ability.toString());
			}
		}
		
		if(eOld == null || e == null)
			return;

		Entity abilityEntity = null;
		Entity abilityEntityOld = null;
		//System.out.println(ConstantMapper.formatTime(Utils.getTime(match))+" "+match.getGameTime()+" "+match.getReplayTime()+" ");
		for(int i = 0; i < abilityIDs.length; ++i){
			abilityEntity = match.getEntities().getByHandle(abilityIDs[i]);
			abilityEntityOld = oldMatch.getEntities().getByHandle(abilityIDs[i]);
			for(ReceiveProp p : abilityEntity.getDtClass().getReceiveProps())
				printPropChange(abilityEntity, abilityEntityOld, p.getVarName());
		}
		
		for(int i = 0; i < abilityIDs.length; ++i){
			abilityEntity = match.getEntities().getByHandle(abilityIDs[i]);
			abilityEntityOld = oldMatch.getEntities().getByHandle(abilityIDs[i]);
			if(!abilityEntity.getProperty("m_iLevel").equals(abilityEntityOld.getProperty("m_iLevel"))){
				int eventID = db.createEvent(replay.getReplayID(), Utils.getTime(match), "AbilityLevel");
				db.addEventIntArgument(eventID, "Unit", unitID);
				db.addEventIntArgument(eventID, "Ability", Constants.getIndex("Actions", ConstantMapper.abilityName((String)abilityEntity.getProperty("m_iName"))));
				db.addEventIntArgument(eventID, "Level", (int)abilityEntity.getProperty("m_iLevel"));
			}
			if(!abilityEntity.getProperty("m_fCooldown").equals(abilityEntityOld.getProperty("m_fCooldown"))){
				if((float)abilityEntity.getProperty("m_fCooldown") > currentMatch.getReplayTime()){
					if(!abilityEntity.getProperty("m_bInAbilityPhase").equals(abilityEntityOld.getProperty("m_bInAbilityPhase")) && (int)abilityEntity.getProperty("m_bInAbilityPhase") == 0){
						int eventID = db.createEvent(replay.getReplayID(), Utils.getTime(match), "AbilityUse");
						db.addEventIntArgument(eventID, "Unit", unitID);
						db.addEventIntArgument(eventID, "Ability", Constants.getIndex("Actions", ConstantMapper.abilityName((String)abilityEntity.getProperty("m_iName"))));
						System.out.println("casted "+ConstantMapper.abilityName((String)abilityEntity.getProperty("m_iName")));
					}
					
					int eventID = db.createEvent(replay.getReplayID(), Utils.getTime(match), "CooldownStart");
					db.addEventIntArgument(eventID, "Unit", unitID);
					db.addEventIntArgument(eventID, "Ability", Constants.getIndex("Actions", ConstantMapper.abilityName((String)abilityEntity.getProperty("m_iName"))));
					double cd = (float)abilityEntity.getProperty("m_fCooldown") - currentMatch.getReplayTime();
					System.out.println("Enter cooldown "+cd);
					db.addEventRealArgument(eventID, "Duration", cd);
				}
				else{
					System.out.println("Strange cd change. Refresher?");
					int eventID = db.createEvent(replay.getReplayID(), Utils.getTime(match), "CooldownEnd");
					db.addEventIntArgument(eventID, "Unit", unitID);
					db.addEventIntArgument(eventID, "Ability", Constants.getIndex("Actions", ConstantMapper.abilityName((String)abilityEntity.getProperty("m_iName"))));
				}	
			}
			else{
				if((float)abilityEntity.getProperty("m_fCooldown") <= currentMatch.getReplayTime() && (float)abilityEntity.getProperty("m_fCooldown") > oldMatch.getReplayTime()){
					int eventID = db.createEvent(replay.getReplayID(), Utils.getTime(match), "CooldownEnd");
					db.addEventIntArgument(eventID, "Unit", unitID);
					db.addEventIntArgument(eventID, "Ability", Constants.getIndex("Actions", ConstantMapper.abilityName((String)abilityEntity.getProperty("m_iName"))));
					System.out.println("Ability came off cd");
				}
			}
		}
		//Maybe todo: Channelign abilities
	}
}
