package data_extraction;

import database.Database;
import skadistats.clarity.match.Match;
import skadistats.clarity.model.Entity;
import utils.ConstantMapper;

public class TrackedUnit {
	private int handle;
	private int unitID;
	private boolean created;
	private ReplayData replay;
	private Database db;
	
	public TrackedUnit(Entity unit, ReplayData replay, Database db){
		handle = unit.getHandle();

		this.db = db;
		this.replay = replay;
		created = false;
	}
	
	public boolean update(Match match, Match oldMatch){
		Entity e = match.getEntities().getByHandle(handle);
		Entity e_old = oldMatch.getEntities().getByHandle(handle);
		if(e == null)
			return false;
		if(created){
			if(ConstantMapper.isAlive((Integer)e.getProperty("m_lifeState")) && !ConstantMapper.isAlive((Integer)e_old.getProperty("m_lifeState"))){
				db.createEvent(replay.getReplayID(), "Spawn", 0, unitID, "");
			}
			else if(!ConstantMapper.isAlive((Integer)e.getProperty("m_lifeState")) && ConstantMapper.isAlive((Integer)e_old.getProperty("m_lifeState"))){				
				db.createEvent(replay.getReplayID(), "Death", 0, unitID, "");
			}
		}
		else{
			if(ConstantMapper.isAlive((Integer)e.getProperty("m_lifeState"))){
				created = true;
				int playerNr = 0;
				if((Integer)e.getProperty("m_hOwnerEntity") != 2097151){
					playerNr = match.getEntities().getByHandle((Integer)e.getProperty("m_hOwnerEntity")).getProperty("m_iPlayerID");
				}
				unitID = db.createUnit(ConstantMapper.nameForIndex((Integer)e.getProperty("m_iUnitNameIndex")),
							ConstantMapper.team((Integer)e.getProperty("m_iTeamNum")),
							replay.getPlayerID(playerNr),
							replay.getReplayID());
				db.createEvent(replay.getReplayID(), "Spawn", 0, unitID, "");
			}
		}
			
		return true;
	}
}
