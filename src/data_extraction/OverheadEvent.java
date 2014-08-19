package data_extraction;

import skadistats.clarity.match.Match;
import skadistats.clarity.model.Entity;
import skadistats.clarity.model.UserMessage;

public class OverheadEvent {
	public String type;
	public int value;
	public Entity target_entity;
	public Entity target_player;
	public Entity source_player;
	
	public OverheadEvent(UserMessage um, Match match){
		if(um.getProperty("message_type") != null)
			type = (String) um.getProperty("message_type");
		if(um.getProperty("value") != null)
			value = (int) um.getProperty("value");
		if(um.getProperty("target_player_entindex") != null)
			target_player = match.getEntities().getByIndex((int) um.getProperty("target_player_entindex"));
		if(um.getProperty("target_entindex") != null)
			target_entity = match.getEntities().getByIndex((int) um.getProperty("target_entindex"));
		if(um.getProperty("source_player_entindex") != null)
			source_player = match.getEntities().getByIndex((int) um.getProperty("source_player_entindex"));
	}
}
