package data_extraction;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import database.Constants;
import database.Database;
import skadistats.clarity.match.Match;
import skadistats.clarity.model.Entity;
import utils.ConstantMapper;

public class UnitTracker {
	private ReplayData replay;
	private Database db;
	private boolean wroteTeams;
	private boolean wrotePlayers;
	
	private Set<String> trackedClasses;
	private Map<Integer, TrackedUnit> units;
	private Map<String, LinkedList<TrackedUnit>> modifierAdds;
	private Map<String, LinkedList<TrackedUnit>> modifierLosses;
	
	public UnitTracker(ReplayData replay, Database db){
		this.db = db;
		this.replay = replay;
		wroteTeams = false;
		wrotePlayers = false;
		
		trackedClasses = new HashSet<String>();
		/*for(String unit : Constants.getAllValues("UnitTypes")){
			trackedClasses.add(ConstantMapper.DTClassForName(unit));
		}*/
		trackedClasses.add(ConstantMapper.DTClassForName("Zeus"));
		//trackedClasses.add(ConstantMapper.DTClassForName("Nature's Prophet"));
		//trackedClasses.add(ConstantMapper.DTClassForName("Radiant Siege Creep"));
		
		units = new HashMap<Integer, TrackedUnit>();
	}
	
	public void update(Match match, Match oldMatch){
		if(!wroteTeams)
			wroteTeams = replay.tryWriteTeams(match);
		if(wroteTeams && !wrotePlayers){
			wrotePlayers = replay.writePlayers(match, db);
		}
		for(String dtClass : trackedClasses){
			Iterator<Entity> it = match.getEntities().getAllByDtName(dtClass);
			while(it.hasNext()){
				Entity e = it.next();
				if(!units.containsKey(e.getHandle()))
					units.put(e.getHandle(), new TrackedUnit(e, replay, db));
			}
		}
		for(TrackedUnit u : units.values()){
			u.update(match, oldMatch);
			u.checkModifiers(match, oldMatch, modifierAdds, modifierLosses);
			u.updateInventory(match, oldMatch);
		}
	}
	
	public boolean exists(int handle){
		return units.get(handle) != null;
	}
	
	public LinkedList<TrackedUnit> getUnitsAddingModifier(String modifier){
		return modifierAdds.get(modifier);
	}
	
	public LinkedList<TrackedUnit> getUnitsLosingModifier(String modifier){
		return modifierLosses.get(modifier);
	}
	
	public int getUnitID(int handle){
		if(units.containsKey(handle))
			return units.get(handle).getID();
		else
			return 0;
	}
}
