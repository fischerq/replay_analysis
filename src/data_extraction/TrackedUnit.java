package data_extraction;

import database.Database;
import skadistats.clarity.match.Match;
import skadistats.clarity.model.Entity;
import utils.ConstantMapper;

public class TrackedUnit {
	private int handle;
	private int unit_id;
	private String type;
	
	private Database db;
	
	public TrackedUnit(Entity unit, Database db){
		handle = unit.getHandle();
		String type = ConstantMapper.nameForIndex((Integer)unit.getProperty("m_iUnitNameIndex"));
		this.db = db;
	}
	
	public boolean update(Match match){
		return true;
	}
}
