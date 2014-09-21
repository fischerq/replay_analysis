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
	private Set<Integer> livingUnits;
	
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
		livingUnits = new HashSet<Integer>(); 
	}
	
	public void update(Match match, Match oldMatch){
		if(!wroteTeams)
			wroteTeams = replay.tryWriteTeams(match);
		if(wroteTeams && !wrotePlayers){
			wrotePlayers = replay.writePlayers(match, db);
		}
		for(Integer handle : match.getEntities().getAddedHandles()){
			Globals.countString(match.getEntities().getByHandle(handle).getDtClass().getDtName());
			if(units.containsKey(handle)){
				//System.out.println("again"+handle);
				continue;
			}
			Entity e = match.getEntities().getByHandle(handle);
			if(trackedClasses.contains(e.getDtClass().getDtName()))
				units.put(e.getHandle(), new TrackedUnit(e, replay, db));
			
		}
		for(TrackedUnit u : units.values()){
			u.update(match, oldMatch);
			switch(u.getLifeChange()){
			case NOTHING:
				break;
			case SPAWN:
				livingUnits.add(u.getHandle());
				break;
			case DEATH:
				livingUnits.remove(u.getHandle());
				break;
			}
		}
	}
	
	public boolean exists(int handle){
		return units.get(handle) != null;
	}
	
	public int getUnitID(int handle){
		if(units.containsKey(handle))
			return units.get(handle).getID();
		else
			return -1;
	}
	
	public TrackedUnit getUnit(int handle){
		return units.get(handle);
	}
	
	public Set<Integer> getLivingUnits(){
		return livingUnits;
	}
}
