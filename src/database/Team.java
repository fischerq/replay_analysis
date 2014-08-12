package database;

import java.util.List;

public class Team {
	public String name;
	public String tag;
	public String side;
	public List<Player> players;
	
	public Team(String n, String t, String s, List<Player> p){
		name = n;
		tag = t;
		side = s;
		players = p;
	}
}
