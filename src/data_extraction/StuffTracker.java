package data_extraction;

import java.util.HashMap;
import java.util.Map;


import database.Constants;
import database.Database;

import skadistats.clarity.match.Match;
import skadistats.clarity.model.Entity;
import skadistats.clarity.model.StringTable;
import skadistats.clarity.model.UserMessage;
import utils.ConstantMapper;
import utils.ConstantMapper.ParticleType;
import utils.Utils;

public class StuffTracker {
	private Database db;
	private ReplayData replay;
	private UnitTracker units;
	
	private Map<Integer, Projectile> currentLinearProjectiles;
	private Map<Integer, Projectile> currentTrackingProjectiles;
	private Map<Integer, Projectile> currentAttacks;
	//private Map<Integer, Effect> unitEffects;
	private Map<Integer, Effect> currentParticles;
	
	
	private int nextIndex;
	
	public StuffTracker(ReplayData replay, Database db, UnitTracker units){
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
				if(!projectileName.equals("Error"))
				{
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
				}
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
				UserMessage position = um.getProperty("origin");
				UserMessage velocity =  um.getProperty("velocity");
				int projectileIndex = getProjectileIndex();
				eventID = db.createEvent(replay.getReplayID(), Utils.getTime(match), "LinearProjectileCreation");
				db.addEventIntArgument(eventID, "Index", projectileIndex);
				if(units.exists(entityHandle))
					db.addEventIntArgument(eventID, "Unit", units.getUnitID(entityHandle));
				else
					db.addEventIntArgument(eventID, "Unit", 0);
				db.addEventIntArgument(eventID, "Projectile", Constants.projectiles.get(ConstantMapper.projectileForParticle(particle)));
				db.addEventRealArgument(eventID, "PositionX", (Float)position.getProperty("x"));
				db.addEventRealArgument(eventID, "PositionY", (Float)position.getProperty("y"));
				db.addEventRealArgument(eventID, "VelocityX", (Float)velocity.getProperty("x"));
				db.addEventRealArgument(eventID, "VelocityY", (Float)velocity.getProperty("y"));
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
				int index = um.getProperty("index");

				switch((String)um.getProperty("type")){
				case "DOTA_PARTICLE_MANAGER_EVENT_CREATE"://LOTS
					UserMessage create = (UserMessage)um.getProperty("create_particle");
					//System.out.println("Created Particle"+create.toString());
					String particleName = particleEffectNames.getNameByIndex((Integer)create.getProperty("particle_name_index"));

					//System.out.println("Particle "+particleEffectNames.getNameByIndex((Integer)create.getProperty("particle_name_index")));
					//System.out.println("Created Particle"+um.toString());
					/*
					 * /*if((Integer)create.getProperty("entity_handle") != 2097151)
						System.out.println("Entity"+match.getEntities().getByHandle((Integer)create.getProperty("entity_handle")).getDtClass().getDtName());
					else
						System.out.println("No entity");
					 */
					ParticleType type = ConstantMapper.particleType(particleName);
					if(handleParticle(type))
						handleEffect(type, um);
//					currentParticles.put(index, new Particle());
					break;
				case "DOTA_PARTICLE_MANAGER_EVENT_UPDATE"://LOTS
				case "DOTA_PARTICLE_MANAGER_EVENT_UPDATE_ENT"://LOTS
				case "DOTA_PARTICLE_MANAGER_EVENT_UPDATE_ORIENTATION":
				case "DOTA_PARTICLE_MANAGER_EVENT_SHOULD_DRAW":
				case "DOTA_PARTICLE_MANAGER_EVENT_DESTROY_INVOLVING":
				case "DOTA_PARTICLE_MANAGER_EVENT_DESTROY":
				case "DOTA_PARTICLE_MANAGER_EVENT_RELEASE"://LOTS
					break;
				default:
					break;
				}
				
				break;
			//if(um.getName() == )
			}
		}
	}
	
	private void handleEffect(ParticleType effectType, UserMessage um){
		switch(effectType){
		default:
			//System.out.println(um.toString());
			return;
		}
	}
	
	private boolean handleParticle(ParticleType type){
		//Only handle particles for stuff that is a) not cosmetic b) not observable through unit data
		//Examples: ground-targeted casts
		//			effects of casts on units
		switch(type){
		case EffectEntity:
		case AffectedUnit:
			return true;
		case Cosmetic:
		case Redundant:
		case Unknown:
			return false;
		default:
			System.out.println("Unhandled particle type "+type);
			return false;
		}
	}
	
}
