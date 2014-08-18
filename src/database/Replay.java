package database;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Replay {
	public int id;
	public List<Team> teams;
	public Map<Integer, Team> teamByIndex;
	public Map<Integer, Player> playerByIndex;
	
	public Replay(int id, List<Team> teams){
		this.id = id;
		this.teams = teams;
		playerByIndex = new TreeMap<Integer, Player>();
		for(Team t:teams){
			teamByIndex.put(t.index, t);
			for(Player p: t.players){
				playerByIndex.put(p.index, p);
			}
		}
	}
}
