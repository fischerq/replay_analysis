package database;

import java.util.List;

public class Team {
	public int index;
	public String name;
	public String tag;
	public String side;
	public List<Player> players;
	
	public Team(int index, String name, String tag, String side, List<Player> players){
		this.index = index;
		this.name = name;
		this.tag = tag;
		this.side = side;
		this.players = players;
	}
}
