package data_extraction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.vecmath.Vector2f;

import database.Constants;
import database.Database;

import skadistats.clarity.match.Match;
import skadistats.clarity.model.Entity;
import skadistats.clarity.model.GameEvent;
import skadistats.clarity.model.StringTable;
import skadistats.clarity.model.UserMessage;
import utils.ConstantMapper;
import utils.Utils;

public class ProjectileTracker {
	private Database db;
	private ReplayData replay;
	private UnitTracker units;
	
	private Map<Integer, Projectile> currentLinearProjectiles;
	private Map<Integer, Projectile> currentTrackingProjectiles;
	private Map<Integer, Projectile> currentAttacks;
	
	private int nextIndex;
	
	public ProjectileTracker(ReplayData replay, Database db, UnitTracker units){
		this.db = db;
		this.replay = replay;
		this.units = units;
		
		currentLinearProjectiles = new HashMap<Integer, Projectile>();
		currentTrackingProjectiles = new HashMap<Integer, Projectile>();
		currentAttacks = new HashMap<Integer, Projectile>();
		nextIndex = 0;
	}
	
	private int getProjectileIndex(){
		int result = nextIndex;
		nextIndex++;
		return result;
	}
	
	public void updateProjectiles(Match match){
		StringTable particleEffectNames = match.getStringTables().forName("ParticleEffectNames");
		for(Entity e : match.getTempEntities().getAll()){
			//Globals.countString(e.getDtClass().getDtName());
			/*if(e.getDtClass().getDtName().equals("DT_TEEffectDispatch")){
				System.out.println(ConstantMapper.formatTime(Utils.getTime(currentMatch))+" "+e.toString());//
			//if(e.getDtClass().getPropertyIndex("m_EffectData.m_iEffectName") != null)
				System.out.println(currentMatch.getStringTables().forName("EffectDispatch").getNameByIndex((Integer)e.getProperty("m_EffectData.m_iEffectName")));
			}*/
			if(e.getDtClass().getDtName().equals("DT_TEDOTAProjectile")){
				//System.out.println(e.toString());
				if(e.getProperty("m_hSource") == null || e.getProperty("m_hTarget") == null){
					/*System.out.println("Broken projectile:"+e.toString());
					if(e.getProperty("m_hSource") != null )
						System.out.println(match.getEntities().getByHandle((Integer)e.getProperty("m_hSource")).getDtClass().getDtName());
					if(e.getProperty("m_hTarget") != null )
						System.out.println(match.getEntities().getByHandle((Integer)e.getProperty("m_hTarget")).getDtClass().getDtName());*/
					continue;//Skip broken particles
				}
				//System.out.print(ConstantMapper.formatTime(Utils.getTime(currentMatch))+" "+currentMatch.getEntities().getByHandle((int)e.getProperty("m_hSource")).getDtClass().getDtName());
				//System.out.println(" to "+currentMatch.getEntities().getByHandle((int)e.getProperty("m_hTarget")).getDtClass().getDtName());
				String projectileName = "";
				boolean isAttack = false;
				if(e.getProperty("m_iParticleSystem") != null)
					projectileName = ConstantMapper.projectileForParticle(particleEffectNames.getNameByIndex((int)e.getProperty("m_iParticleSystem")));
				else{
					//TODO map to attack
					isAttack = true;
					projectileName = "Attack";
					//System.out.println(match.getTempEntities().tempEntities.size());
//					System.out.println(ConstantMapper.formatTime(Utils.getTime(currentMatch))+" "+currentMatch.getEntities().getByHandle((int)e.getProperty("m_hSource")).getDtClass().getDtName()+" to "+currentMatch.getEntities().getByHandle((int)e.getProperty("m_hTarget")).getDtClass().getDtName());
				}
				
				Projectile newProjectile = new Projectile(getProjectileIndex(), (int)e.getProperty("m_hSource"));
				
				int eventID = db.createEvent(replay.getReplayID(), Utils.getTime(match), "TrackingProjectileCreation");
				db.addEventIntArgument(eventID, "Projectile", Constants.projectiles.get(projectileName));
				db.addEventIntArgument(eventID, "Index", newProjectile.projectileIndex);
				if(units.exists((int)e.getProperty("m_hSource")))
					db.addEventIntArgument(eventID, "Unit", units.getUnitID((int)e.getProperty("m_hSource")));
				else
					db.addEventIntArgument(eventID, "Unit", 0);
				if(units.exists((int)e.getProperty("m_hTarget")))
					db.addEventIntArgument(eventID, "Target", units.getUnitID((int)e.getProperty("m_hTarget")));
				else
					db.addEventIntArgument(eventID, "Target", 0);
				
				currentTrackingProjectiles.put((int)e.getProperty("m_hTarget"), newProjectile);
				
				if(e.getProperty("m_bIsAttack") != null || ConstantMapper.isAttackProjectile(projectileName) || isAttack){
					currentAttacks.put((int)e.getProperty("m_hTarget"), newProjectile);
				}
				
			}
		}
		
		//Process usermessages
		for (UserMessage um : match.getUserMessages()) {
			int eventID;
			//Globals.countString(um.getName());
			switch(um.getName()){
			case "CDOTAUserMsg_CreateLinearProjectile":
				int entityHandle = match.getEntities().getByIndex((Integer)um.getProperty("entindex")).getHandle();
				String particle = particleEffectNames.getNameByIndex((Integer)um.getProperty("particle_index"));
				Vector2f position = new Vector2f();//(Vector2f)um.getProperty("origin"); TODO
				Vector2f velocity =  new Vector2f();//(Vector2f)um.getProperty("velocity"); TODO
				int projectileIndex = getProjectileIndex();
				eventID = db.createEvent(replay.getReplayID(), Utils.getTime(match), "LinearProjectileCreation");
				db.addEventIntArgument(eventID, "Index", projectileIndex);
				if(units.exists(entityHandle))
					db.addEventIntArgument(eventID, "Unit", units.getUnitID(entityHandle));
				else
					db.addEventIntArgument(eventID, "Unit", 0);
				db.addEventIntArgument(eventID, "Projectile", Constants.projectiles.get(ConstantMapper.projectileForParticle(particle)));
				db.addEventRealArgument(eventID, "PositionX", position.x);
				db.addEventRealArgument(eventID, "PositionY", position.y);
				db.addEventRealArgument(eventID, "VelocityX", velocity.x);
				db.addEventRealArgument(eventID, "VelocityY", velocity.y);
				currentLinearProjectiles.put((Integer)um.getProperty("handle"), new Projectile(projectileIndex, 0));
				break;
			case "CDOTAUserMsg_DestroyLinearProjectile":
				if(currentLinearProjectiles.containsKey((Integer)um.getProperty("handle"))){
					eventID = db.createEvent(replay.getReplayID(), Utils.getTime(match), "LinearProjectileRemoval");
					db.addEventIntArgument(eventID, "Index", currentLinearProjectiles.get((Integer)um.getProperty("handle")).projectileIndex);
					currentLinearProjectiles.remove((Integer)um.getProperty("handle"));
				}
				else
					System.out.println("Couldnt find linear projectile");
				break;
			case "CDOTAUserMsg_DodgeTrackingProjectiles":
				int handle = match.getEntities().getByIndex((Integer)um.getProperty("entindex")).getHandle();
				if(currentTrackingProjectiles.containsKey(handle)){
					eventID = db.createEvent(replay.getReplayID(), Utils.getTime(match), "TrackingProjectileDodge");
					db.addEventIntArgument(eventID, "Index", currentTrackingProjectiles.get(handle).projectileIndex);
					if(units.exists(handle))
						db.addEventIntArgument(eventID, "Unit", units.getUnitID(handle));
					else
						db.addEventIntArgument(eventID, "Unit", 0);
				}
				break;
			case "CDOTAUserMsg_ParticleManager":
				//System.out.println("Particle "+um.toString());
				if(um.getProperty("create_particle") != null){
					//Globals.countString((um.getProperty("create_particle"))
				}
				break;
			//if(um.getName() == )
			}
		}
	}
	
	
}
