package database;

import java.util.List;

public class Unit {
	public String type;
	public Team team;
	public Player controlledByPlayer;
	public boolean illusion;
	public List<TimeSeries> timeSeries;
	
	public Unit(String ty, Team t, Player p, boolean i, List<TimeSeries> ts){
		type = ty;
		team = t;
		controlledByPlayer = p;
		illusion = i;
		timeSeries = ts;
	}
}
