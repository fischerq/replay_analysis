package data_extraction;

import java.util.LinkedList;
import java.util.List;

import javax.vecmath.Vector2f;

import skadistats.clarity.match.Match;
import skadistats.clarity.model.Entity;
import skadistats.clarity.model.StringTable;
import skadistats.clarity.model.UserMessage;
import utils.ConstantMapper;
import utils.Utils;

public class Particle {
	public double timeCreated;
	public String name;
	public Entity entity;
	public Vector2f position;
	public int index;
	
	private List<UserMessage> messages = new LinkedList<UserMessage>();
	private List<Double> messageTimes = new LinkedList<Double>();
	
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
		addMessage(message, Utils.getTime(match));
	}
	
	public void addMessage(UserMessage um, double time){
		messages.add(um);
		messageTimes.add(time);
	}
	
	public String toString(){
		String result = index+": "+name+"\n";
		for(int i = 0; i < messages.size(); ++i){
			result += ConstantMapper.formatTime(messageTimes.get(i))+" "+(String)messages.get(i).getProperty("type")+"\n";
		}			
		return result;
	}
	
	public Particle(UserMessage message, Match match, boolean fromEntUpdate){
		index = (int)message.getProperty("index");
		timeCreated = Utils.getTime(match);
		UserMessage upMsg = (UserMessage) message.getProperty("update_particle_ent");
		name = "AttackHit";
		if((int)upMsg.getProperty("entity_handle") != 2097151){
			entity  = match.getEntities().getByHandle((int)upMsg.getProperty("entity_handle"));
		}
		else
			entity = null;
		position = null;
		addMessage(message, Utils.getTime(match));

	}

	public void update(UserMessage update) {
		UserMessage positionMsg = (UserMessage)update.getProperty("position");
		position = new Vector2f();
		position.x = (float)positionMsg.getProperty("x");
		position.y = (float)positionMsg.getProperty("y");
	}
}
