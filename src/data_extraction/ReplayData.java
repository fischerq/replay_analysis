package data_extraction;

import java.util.Iterator;

import database.Database;
import skadistats.clarity.match.Match;
import skadistats.clarity.model.Entity;
import utils.ConstantMapper;

public class ReplayData {
	private Database db;
	private int id;
	private int[] playerIDs;
	private String[] playerHeroes;
	private int nPlayersWritten;
	private int radiant_id;
	private int dire_id;
	
	public ReplayData(int id, Database db){
		this.db = db;
		this.id = id;
		radiant_id = 0;
		dire_id = 0;
		playerIDs = new int[10];
		for(int i = 0; i<10; ++i){
			playerIDs[i] = 0;
		}
		playerHeroes = new String[10];
		for(int i = 0; i<10; ++i){
			playerHeroes[i] = "";
		}
		nPlayersWritten = 0;
	}
	
	public int getReplayID(){
		return id;
	}
	
	public int getPlayerID(int playerNr){
		return playerIDs[playerNr];
	}
	
	public boolean tryWriteTeams(Match match){
		Iterator<Entity> it = match.getEntities().getAllByDtName("DT_DOTATeam");
		int nWritten = 0;
		while(it.hasNext()){
			Entity e = it.next();
			String team = ConstantMapper.team((Integer)e.getProperty("m_iTeamNum"));
			if(team.equals("Radiant") || team.equals("Dire")){
				String name =(String)e.getProperty("m_szTeamname");
				if(name.equals("#DOTA_GoodGuys"))
					name = "Team Radiant";
				else if(name.equals("#DOTA_BadGuys"))
					name = "Team Dire";
				int team_id = db.createTeam(name, (String)e.getProperty("m_szTag"), team, id);
				if(team.equals("Radiant"))
					radiant_id = team_id;
				else if(team.equals("Dire"))
					dire_id = team_id;
				nWritten++;
			}
		}
		if(nWritten == 1)
			System.out.println("Found only one team?");
		return nWritten == 2;
	}
	
	public boolean writePlayers(Match match, Database db){
		Entity player_resource;
		if(match.getEntities().getAllByDtName("DT_DOTA_PlayerResource").hasNext())
			player_resource	= match.getEntities().getAllByDtName("DT_DOTA_PlayerResource").next();
		else 
			return false;
		Integer[] selected_heroes = player_resource.getArrayProperty(Integer.class, "m_nSelectedHeroID");
		String[] nicks = player_resource.getArrayProperty(String.class, "m_iszPlayerNames");
		
		for(int i = 0; i < 10; ++i){
			if(playerIDs[i] != 0)
				continue;
			if(selected_heroes[i] != -1){
				int teamID = 0;
				if(i < 5)
					teamID = radiant_id;
				else
					teamID = dire_id;
				playerIDs[i] = db.createPlayer(nicks[i], ConstantMapper.heroName(selected_heroes[i]), teamID);
				playerHeroes[i] = ConstantMapper.heroName(selected_heroes[i]);
				nPlayersWritten++;
			}
		}
		return nPlayersWritten == 10;
	}
	
	int getPlayerID(String hero){
		for(int i = 0; i < 10; ++i){
			if(playerHeroes[i].equals(hero)){
				return playerIDs[i];
			}
		}
		System.out.println("Couldn't find player for hero "+hero);
		for(int i = 0; i < 10; ++i){
			System.out.println(playerHeroes[i]+": "+playerIDs[i]);
		}
		return -1;
	}
}
