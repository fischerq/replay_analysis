package data_extraction;

import javax.vecmath.Vector2f;

import database.Database;
import skadistats.clarity.match.Match;
import skadistats.clarity.model.Entity;
import skadistats.clarity.model.UserMessage;
import utils.ConstantMapper;
import utils.Utils;

public class Projectile {
	
	private Database db;
	private ReplayData replay;
	
	public int projectileIndex;
	public Entity source;
	public Entity target;
	public Vector2f position;
	public double speed;
	public boolean dodgeable;
	public double expireTime;

	
	public Projectile(int index, Entity e, Match match, Database db, ReplayData replay){
		this.db = db;
		this.replay = replay;
		projectileIndex = index;
		source = match.getEntities().getByHandle((int)e.getProperty("m_hSource"));
		target = match.getEntities().getByHandle((int)e.getProperty("m_hTarget"));
		if(e.getProperty("m_bDodgeable") != null)
			dodgeable = true;
		else 
			dodgeable = false;
		if(e.getProperty("m_iMoveSpeed") == null)
			speed = 0;
			//System.out.println(match.getStringTables().forName("ParticleEffectNames").getNameByIndex((Integer)e.getProperty("m_iParticleSystem"))
			//		+" "+match.getEntities().getByHandle((int)e.getProperty("m_hTarget")).getDtClass().getDtName()+" "+ e.toString());
		else
			speed = (int)e.getProperty("m_iMoveSpeed");
		position = Utils.getPosition(source);
		if(e.getProperty("m_flExpireTime") != null)
			expireTime = (float)e.getProperty("m_flExpireTime");
		else
			expireTime = -1;
		
		
		
		double deltaT = ConstantMapper.replay_tick/2;
		Vector2f targetPos = Utils.getPosition(target);
		Vector2f direction = new Vector2f();
		direction.sub(targetPos, position);
		double distanceConvered = speed * deltaT;
		if(direction.length() < distanceConvered){
			//System.out.println("Projectile hit "+source.getDtClass().getDtName()+" "+target.getDtClass().getDtName());
			int eventID = db.createEvent(replay.getReplayID(), Utils.getTime(match), "TrackingProjectileHit");
			db.addEventIntArgument(eventID, "Index", projectileIndex);
		}
		else{
			direction.scale((float) (distanceConvered/direction.length()));
			position.add(direction);
		}
	}
	
	public Projectile(int index, UserMessage um, Match match){
		projectileIndex = index;
		expireTime = -1;

	}
	
	public boolean update(Match match, Match matchOld){
		double deltaT = ConstantMapper.replay_tick;
		Vector2f targetPos = Utils.getPosition(target);
		Vector2f direction = new Vector2f();
		direction.sub(targetPos, position);
		double distanceConvered = speed * deltaT;
		if(direction.length() < distanceConvered){
			//System.out.println("Projectile hit "+source.getDtClass().getDtName()+" "+target.getDtClass().getDtName());
			int eventID = db.createEvent(replay.getReplayID(), Utils.getTime(match), "TrackingProjectileHit");
			db.addEventIntArgument(eventID, "Index", projectileIndex);
			return false;
		}
		else{
			direction.scale((float) (distanceConvered/direction.length()));
			position.add(direction);
		}
		
		if(expireTime > 0){
			expireTime -= match.getGameTime() - matchOld.getGameTime();
			if(expireTime < 0){
				int eventID = db.createEvent(replay.getReplayID(), Utils.getTime(match), "TrackingProjectileExpiration");
				db.addEventIntArgument(eventID, "Index", projectileIndex);
				return false;
			}
			else
				return true;
		}
		else
			return true;
		
	}
}
