package data_extraction;

import javax.vecmath.Vector2f;

import skadistats.clarity.match.Match;
import skadistats.clarity.model.Entity;
import skadistats.clarity.model.StringTable;
import skadistats.clarity.model.UserMessage;
import utils.Utils;

public class Particle {
	public double timeCreated;
	public String name;
	public Entity entity;
	public Vector2f position;
	public int index;
	
	public Particle(UserMessage message, Match match){
		index = (int)message.getProperty("index");
		timeCreated = Utils.getTime(match);
		UserMessage createMsg = (UserMessage) message.getProperty("create_particle");
		StringTable particleEffectNames = match.getStringTables().forName("ParticleEffectNames");
		name = particleEffectNames.getNameByIndex((int)createMsg.getProperty("particle_name_index"));
		if((int)createMsg.getProperty("entity_handle") != 2097151){
			entity  = match.getEntities().getByHandle((int)createMsg.getProperty("entity_handle"));
		}
		else
			entity = null;
		position = null;
	}
	
	public Particle(UserMessage message, Match match, boolean fromEntUpdate){
		index = (int)message.getProperty("index");
		timeCreated = Utils.getTime(match);
		UserMessage upMsg = (UserMessage) message.getProperty("update_particle_ent");
		name = "from updateEnt";
		if((int)upMsg.getProperty("entity_handle") != 2097151){
			entity  = match.getEntities().getByHandle((int)upMsg.getProperty("entity_handle"));
		}
		else
			entity = null;
		position = null;
	}

	public void update(UserMessage update) {
		UserMessage positionMsg = (UserMessage)update.getProperty("position");
		position = new Vector2f();
		position.x = (float)positionMsg.getProperty("x");
		position.y = (float)positionMsg.getProperty("y");
	}
}
