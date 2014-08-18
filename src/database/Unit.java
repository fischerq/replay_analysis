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
	
	public static class UnitSummary{
		public int index;
		public String type;
		public int teamIndex;
		public int controlledByPlayerIndex;
		public boolean illusion;	
		
		public UnitSummary(int index, String type, int teamIndex, int controlIndex, boolean illusion){
			this.index = index;
			this.type = type;
			this.teamIndex = teamIndex;
			this.controlledByPlayerIndex = controlIndex;
			this.illusion = illusion;
		}
	}
}
