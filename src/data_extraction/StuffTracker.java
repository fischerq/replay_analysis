package data_extraction;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;







import java.util.PriorityQueue;

import database.Constants;
import database.Database;
import skadistats.clarity.match.Match;
import skadistats.clarity.model.Entity;
import skadistats.clarity.model.ModifierTableEntry;
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
	private Map<Integer, LinkedList<Projectile> > currentTrackingProjectiles;
	private Map<Integer, Projectile> currentAttacks;
	private Map<Integer, LinkedList<Integer>> unitParticles;

	private Map<Integer, Particle> activeParticles;
	private Map<String, Particle> tickParticles;
	private Map<Integer, OverheadEvent> overheadEvents;
	

	
	
	private int nextIndex;
	

	
	
	public StuffTracker(ReplayData replay, Database db, UnitTracker units){
		this.db = db;
		this.replay = replay;
		this.units = units;
		
		currentLinearProjectiles = new HashMap<Integer, Projectile>();
		currentTrackingProjectiles = new HashMap<Integer, LinkedList<Projectile>>();
		currentAttacks = new HashMap<Integer, Projectile>();
		unitParticles = new HashMap<Integer, LinkedList<Integer>>();
		activeParticles = new HashMap<Integer, Particle>();
		overheadEvents = new HashMap<Integer, OverheadEvent>();
		tickParticles = new HashMap<String, Particle>();
		nextIndex = 0;
		

		
	}
	
	private int getProjectileIndex(){
		int result = nextIndex;
		nextIndex++;
		return result;
	}
	
	public void updateStuff(Match match, Match oldMatch){
		//Update existing projectiles
		
		tickParticles.clear();
		
		
		StringTable particleEffectNames = match.getStringTables().forName("ParticleEffectNames");
		for(Entity e : match.getTempEntities().getAll()){
			/*if(e.getDtClass().getDtName().equals("DT_TEEffectDispatch")){
				System.out.println(ConstantMapper.formatTime(Utils.getTime(match))+" "+e.toString());
			//if(e.getDtClass().getPropertyIndex("m_EffectData.m_iEffectName") != null)
				System.out.println(match.getStringTables().forName("EffectDispatch").getNameByIndex((Integer)e.getProperty("m_EffectData.m_iEffectName")));
				System.out.println(match.getEntities().getByIndex((Integer)e.getProperty("m_EffectData.entindex")).getDtClass().getDtName());
			}*/
			Globals.countString(e.getDtClass().getDtName());
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
				if(e.getProperty("m_iParticleSystem") != null){
					projectileName = ConstantMapper.projectileForParticle(particleEffectNames.getNameByIndex((int)e.getProperty("m_iParticleSystem")));
					//Globals.countInt((int)e.getProperty("m_iParticleSystem"));

				}
				else{
					isAttack = true;
					projectileName = "Attack";
					//System.out.println(match.getTempEntities().tempEntities.size());
//					System.out.println(ConstantMapper.formatTime(Utils.getTime(currentMatch))+" "+currentMatch.getEntities().getByHandle((int)e.getProperty("m_hSource")).getDtClass().getDtName()+" to "+currentMatch.getEntities().getByHandle((int)e.getProperty("m_hTarget")).getDtClass().getDtName());
				}
				
				//System.out.println("Create projectile "+particleEffectNames.getNameByIndex((int)e.getProperty("m_iParticleSystem")));
				
				Projectile newProjectile = new Projectile(getProjectileIndex(), e, match, db, replay);
				if(!projectileName.equals("Error"))
				{
					int eventID = db.createEvent(replay.getReplayID(), Utils.getTime(match), "TrackingProjectileCreation");
					db.addEventIntArgument(eventID, "Projectile", Constants.getIndex("Projectiles", projectileName));
					db.addEventIntArgument(eventID, "Index", newProjectile.projectileIndex);
					if(units.exists((int)e.getProperty("m_hSource")))
						db.addEventIntArgument(eventID, "Unit", units.getUnitID((int)e.getProperty("m_hSource")));
					else
						db.addEventIntArgument(eventID, "Unit", 0);
					if(units.exists((int)e.getProperty("m_hTarget")))
						db.addEventIntArgument(eventID, "Target", units.getUnitID((int)e.getProperty("m_hTarget")));
					else
						db.addEventIntArgument(eventID, "Target", 0);
					int targetIndex = (int)e.getProperty("m_hTarget");
					if(!currentTrackingProjectiles.containsKey(targetIndex))
						currentTrackingProjectiles.put(targetIndex, new LinkedList<Projectile>());
					currentTrackingProjectiles.get(targetIndex).add(newProjectile);
				
					if(e.getProperty("m_bIsAttack") != null || ConstantMapper.isAttackProjectile(projectileName) || isAttack){
						currentAttacks.put((int)e.getProperty("m_hTarget"), newProjectile);
					}
				}
			}
			else if(e.getDtClass().getDtName().equals("DT_TEDotaBloodImpact")){
				//System.out.println("Blood Impact: "+e.toString());
			}
		}
		
		//Process usermessages
		
		//First process all particle creations to avoid unassigned indices
		for (UserMessage um : match.getUserMessages()) {
			if(um.getName().equals("CDOTAUserMsg_ParticleManager")){
				int index = um.getProperty("index");
				
				if(((String)um.getProperty("type")).equals("DOTA_PARTICLE_MANAGER_EVENT_CREATE")){
					UserMessage create = (UserMessage)um.getProperty("create_particle");

					Particle particle = new Particle(um, match);
					if(particle.entity != null){
						if(!unitParticles.containsKey(particle.entity.getHandle()))
							unitParticles.put(particle.entity.getHandle(), new LinkedList<Integer>());
						unitParticles.get(particle.entity.getHandle()).add(index);
					}
					activeParticles.put(index, particle);
					tickParticles.put(particle.name, particle);
					//System.out.println("Created particle "+index);
				}
			}
		}
		
		for (UserMessage um : match.getUserMessages()) {

			//Globals.countString(um.getName());
			switch(um.getName()){
			case "CDOTAUserMsg_CreateLinearProjectile":
				int entityHandle = match.getEntities().getByIndex((Integer)um.getProperty("entindex")).getHandle();
				String particleName = particleEffectNames.getNameByIndex((Integer)um.getProperty("particle_index"));
				UserMessage position = um.getProperty("origin");
				UserMessage velocity =  um.getProperty("velocity");
				int projectileIndex = getProjectileIndex();
				int eventID = db.createEvent(replay.getReplayID(), Utils.getTime(match), "LinearProjectileCreation");
				db.addEventIntArgument(eventID, "Index", projectileIndex);
				if(units.exists(entityHandle))
					db.addEventIntArgument(eventID, "Unit", units.getUnitID(entityHandle));
				else
					db.addEventIntArgument(eventID, "Unit", 0);
				db.addEventIntArgument(eventID, "Projectile", Constants.getIndex("Projectiles", ConstantMapper.projectileForParticle(particleName)));
				db.addEventRealArgument(eventID, "PositionX", (Float)position.getProperty("x"));
				db.addEventRealArgument(eventID, "PositionY", (Float)position.getProperty("y"));
				db.addEventRealArgument(eventID, "VelocityX", (Float)velocity.getProperty("x"));
				db.addEventRealArgument(eventID, "VelocityY", (Float)velocity.getProperty("y"));
				currentLinearProjectiles.put((Integer)um.getProperty("handle"), new Projectile(projectileIndex, um, match));
				//System.out.println("Created linear proj "+um.toString());
				break;
			case "CDOTAUserMsg_DestroyLinearProjectile":
				if(currentLinearProjectiles.containsKey((Integer)um.getProperty("handle"))){
					int destroyLinEventID = db.createEvent(replay.getReplayID(), Utils.getTime(match), "LinearProjectileRemoval");
					db.addEventIntArgument(destroyLinEventID, "Index", currentLinearProjectiles.get((Integer)um.getProperty("handle")).projectileIndex);
					currentLinearProjectiles.remove((Integer)um.getProperty("handle"));
				}
				else
					System.out.println("Couldnt find linear projectile");
				break;
			case "CDOTAUserMsg_DodgeTrackingProjectiles":
				int handle = match.getEntities().getByIndex((Integer)um.getProperty("entindex")).getHandle();
				if(currentTrackingProjectiles.containsKey(handle)){
					int dodgeEventID = db.createEvent(replay.getReplayID(), Utils.getTime(match), "TrackingProjectileDodge");
					Iterator<Projectile> it = currentTrackingProjectiles.get(handle).iterator();
					while(it.hasNext()){
						Projectile p = it.next();
						if(p.dodgeable)
						{
							db.addEventIntArgument(dodgeEventID, "Index", p.projectileIndex);
						
							if(units.exists(handle))
								db.addEventIntArgument(dodgeEventID, "Unit", units.getUnitID(handle));
							else
								db.addEventIntArgument(dodgeEventID, "Unit", 0);
							it.remove();
						}
					}
				}
				break;
			case "CDOTAUserMsg_OverheadEvent":
				OverheadEvent ev = new OverheadEvent(um, match);
				overheadEvents.put(ev.target_entity.getHandle(), ev);
				break;
			case "CDOTAUserMsg_ParticleManager":
				//System.out.println("Particle "+um.toString());
				int index = um.getProperty("index");
				//Globals.countString((String)um.getProperty("type"));
				//System.out.println(ConstantMapper.formatTime(Utils.getTime(match))+" "+index+" "+(String)um.getProperty("type"));
				switch((String)um.getProperty("type")){
				case "DOTA_PARTICLE_MANAGER_EVENT_CREATE"://LOTS
					//Already prcoessed before
					break;
					/*UserMessage create = (UserMessage)um.getProperty("create_particle");
					//System.out.println("Created Particle"+index);
					String createdParticleName = particleEffectNames.getNameByIndex((Integer)create.getProperty("particle_name_index"));

					//System.out.println("Particle "+particleEffectNames.getNameByIndex((Integer)create.getProperty("particle_name_index")));
					//System.out.println("Created Particle"+um.toString());
					
					 if((Integer)create.getProperty("entity_handle") != 2097151)
						System.out.println("Entity"+match.getEntities().getByHandle((Integer)create.getProperty("entity_handle")).getDtClass().getDtName());
					else
						System.out.println("No entity");
					 

					Particle particle = new Particle(um, match);
					activeParticles.put(index, particle);

					System.out.println("Created particle "+index);
					//Globals.countInt(index);
					//Globals.countInt(index);
					break;*/
				case "DOTA_PARTICLE_MANAGER_EVENT_UPDATE"://LOTS
					UserMessage update = (UserMessage)um.getProperty("update_particle");
					if(!activeParticles.containsKey(index)){
						Particle createdParticle = new Particle(um, match);
						activeParticles.put(index, createdParticle);
						tickParticles.put(createdParticle.name, createdParticle);
					}
					else{
						//System.out.println(currentParticles.get(index).name+": "+update.toString());
						activeParticles.get(index).update(update);
						activeParticles.get(index).addMessage(um, Utils.getTime(match));
					}
					break;
				case "DOTA_PARTICLE_MANAGER_EVENT_UPDATE_ENT"://LOTS
					UserMessage updateEnt = (UserMessage)um.getProperty("update_particle_ent");
					//Globals.countInt((int)updateEnt.getProperty("attachment"));
					//System.out.println(particleEffectNames.getNameByIndex((int)updateEnt.getProperty("attachment"))+" "+particleEffectNames.getNameByIndex((int)updateEnt.getProperty("attach_type")));
					if(!activeParticles.containsKey(index)){
						//Create new particle
						Particle createdEntityParticle = new Particle(um, match);
						activeParticles.put(index, createdEntityParticle);
						int entHandle = (int)updateEnt.getProperty("entity_handle");
						if(!unitParticles.containsKey(entHandle))
							unitParticles.put(entHandle, new LinkedList<Integer>());
						unitParticles.get(entHandle).add(index);
						tickParticles.put(createdEntityParticle.name, createdEntityParticle);
						//System.out.println(ConstantMapper.formatTime(Utils.getTime(match))+" "+match.getEntities().getByHandle((int)updateEnt.getProperty("entity_handle")).getDtClass().getDtName()+" "+currentParticles.get(index).name+": "+updateEnt.toString());
						//currentParticles.get(index).update(updateEnt);
					}
					else{
						activeParticles.get(index).addMessage(um, Utils.getTime(match));
						//System.out.println(ConstantMapper.formatTime(Utils.getTime(match))+" "+match.getEntities().getByHandle((int)updateEnt.getProperty("entity_handle")).getDtClass().getDtName()+" updating "+index+" "+(int)updateEnt.getProperty("attachment")+" "+(int)updateEnt.getProperty("attach_type")+" "+(int)updateEnt.getProperty("control_point"));//+updateEnt.toString());
						//System.out.println(um.toString());
						
					}
					break;
				case "DOTA_PARTICLE_MANAGER_EVENT_UPDATE_ORIENTATION":
					activeParticles.get(index).addMessage(um, Utils.getTime(match));
					break;
				case "DOTA_PARTICLE_MANAGER_EVENT_SHOULD_DRAW":
					activeParticles.get(index).addMessage(um, Utils.getTime(match));
					//System.out.println(um.toString());
					break;
				case "DOTA_PARTICLE_MANAGER_EVENT_DESTROY_INVOLVING":
					UserMessage destroy_involving = (UserMessage)um.getProperty("destroy_particle_involving");
					int involvingEntityHandle = destroy_involving.getProperty("entity_handle");
					if(unitParticles.containsKey(involvingEntityHandle)){
						for(Integer i : unitParticles.get(involvingEntityHandle))
							activeParticles.remove(i);
						unitParticles.remove(involvingEntityHandle);
					}
					else
						System.out.println("Removing particles for entity without particles");
					break;
				case "DOTA_PARTICLE_MANAGER_EVENT_DESTROY":
					if(activeParticles.containsKey(index)){
						activeParticles.get(index).addMessage(um, Utils.getTime(match));
					}
					/*	MAYBE TODO
					 * else
						System.out.println("Destroying unknown particle "+index);*/
						
					activeParticles.remove(index);
					//System.out.println("Release "+um);
					break;
				case "DOTA_PARTICLE_MANAGER_EVENT_RELEASE"://LOTS
					if(activeParticles.containsKey(index)){
						activeParticles.get(index).addMessage(um, Utils.getTime(match));
						activeParticles.remove(index);
					}
					//Globals.countInt(index);
					break;
				default:
					break;
				}
				
				break;
			case "CDOTAUserMsg_SpectatorPlayerClick":
				int clickEventID = db.createEvent(replay.getReplayID(), Utils.getTime(match), "PlayerClick");
				Entity player = match.getEntities().getByIndex((int)um.getProperty("entindex"));
				db.addEventIntArgument(clickEventID, "Player", replay.getPlayerID((int)player.getProperty("m_iPlayerID")));
				if(ConstantMapper.clickType((int)um.getProperty("order_type")).equals("")){
					System.out.println(ConstantMapper.formatTime(Utils.getTime(match))+" "+um.toString()+" "/*+player.toString()*/);
					if(player.getProperty("m_hAssignedHero")!= null)
						System.out.println(match.getEntities().getByHandle((int)player.getProperty("m_hAssignedHero")).getDtClass().getDtName());
					if(match.getEntities().getByHandle((int)player.getProperty("m_hViewEntity")) != null)
						System.out.println(match.getEntities().getByHandle((int)player.getProperty("m_hViewEntity")).getDtClass().getDtName());
					if(um.getProperty("target_index") != null && (int)um.getProperty("order_type")!= 7){
						Entity target = match.getEntities().getByIndex((int)um.getProperty("target_index"));
						if(target!= null)
							System.out.println(target.getDtClass().getDtName());

					}
				}
				String clickType = ConstantMapper.clickType((int)um.getProperty("order_type"));
				db.addEventIntArgument(clickEventID, "ClickType", Constants.getIndex("ClickTypes", clickType));
				if(um.getProperty("target_index") != null && (int)um.getProperty("order_type")!= 7){
					Entity target = match.getEntities().getByIndex((int)um.getProperty("target_index"));
					if(target == null){
						target = oldMatch.getEntities().getByIndex((int)um.getProperty("target_index"));
					}
					if(units.getUnitID(target.getHandle()) != 0){
						db.addEventIntArgument(clickEventID, "Target", units.getUnitID(target.getHandle()));
					}
						
				}
				else if((int)um.getProperty("order_type")== 7)
					db.addEventIntArgument(clickEventID, "Target", (int)um.getProperty("target_index"));
				//TODO add clicks to attack tagets to some tracker
				break;
			//if(um.getName() == )
			}
		}
		
		for(LinkedList<Projectile> l : currentTrackingProjectiles.values()){
			Iterator<Projectile> it = l.iterator();
			while(it.hasNext()){
				Projectile p = it.next();
				if(!p.update(match, oldMatch))
					it.remove();
			}
		}
	}
	
	public Map<String, Particle> getTickParticles(){
		return tickParticles;
	}
	
	public Map<Integer, Particle> allParticles(){
		return activeParticles;
	}
}
