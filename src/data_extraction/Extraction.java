package data_extraction;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;









import javax.vecmath.Vector2f;

import database.Constants;
import database.Database;
import skadistats.clarity.match.Match;
import skadistats.clarity.model.Entity;
import skadistats.clarity.model.GameEvent;
import skadistats.clarity.model.GameEventDescriptor;
import skadistats.clarity.model.ModifierTableEntry;
import skadistats.clarity.model.StringTable;
import skadistats.clarity.model.UserMessage;
import utils.ConstantMapper;
import utils.Utils;
import utils.ConstantMapper.ParticleType;


public class Extraction {

	private ReplayData replay;
	private Database db;
	
	private UnitTracker units;
	private AnimationTracker animations;
	private Map<Integer, Integer> actionTargets;
	private Set<Integer> attackingUnits;
	private StuffTracker stuff;
	private ModifierTracker modifiers;
	private Map<String, LinkedList<ModifierChange>> modifierChangesByName; 
	
	private Match currentMatch = null;
	private Match oldMatch = null;
		
	private static int attacker = 0;
	private static int target = 1;
	
	private static int maxRangeSq = 1000000;
	
	public Extraction(int id, Database db){
		this.db = db;
		replay = new ReplayData(id, db);
				
		units = new UnitTracker(replay, db);
		animations = new AnimationTracker();
		actionTargets = new HashMap<Integer, Integer>();
		stuff = new StuffTracker(replay, db, units);
		
		modifiers = new ModifierTracker();
		modifierChangesByName = new HashMap<String, LinkedList<ModifierChange>>();
		
		attackingUnits = new HashSet<Integer>();
	}
	
	public void analyseTick(Match match, Match matchOld) {
		//db.startTransaction();

		//System.out.println("\n"+ConstantMapper.formatTime(match.getGameTime())+" Tick");
		currentMatch = match;
		oldMatch = matchOld;
		
		//if(true)
		//	return;
		doPrints();
		
		attackingUnits.clear();
		
		units.update(match, matchOld);

		updateAndProcessAnimations();
		
		updateStuff();
		
		updateModifiers();
		
		processCombatLog();
		 
		//db.stopTransaction();
	}
	
	private boolean doPrints(){
		/*for(Map.Entry<Integer, GameEventDescriptor> dsc : currentMatch.getGameEventDescriptors().byId.entrySet()){
			System.out.println(dsc.getKey()+" "+dsc.getValue().getName());
		}*/
		
		/*Iterator<Entity> it = current_match.getEntities().getAllByDtName("DT_DOTAGamerulesProxy");
		Entity rules = it.next();
		System.out.println(rules.getProperty("dota_gamerules_data.m_fGameTime")+" "+rules.getProperty("dota_gamerules_data.m_flPreGameStartTime")+" "+rules.getProperty("dota_gamerules_data.m_flGameStartTime"));
		*/
		
		/*for(StringTable t : currentMatch.getStringTables().byName.values()){
			System.out.println(t.getName()+" "+t.getNameByIndex(0));
		}*/
		/*Iterator<Entity> it = currentMatch.getEntities().getAllByDtName("DT_DOTAPlayer");
		System.out.println("Tick");
		while(it.hasNext()){
			Entity e = it.next();
			System.out.println(e.toString());
			//System.out.println("creep "+(Integer)e.getProperty("m_hOwnerEntity"));
		}*/
		
		
		/*Iterator<Entity> it = current_match.getEntities().getAllByDtName("DT_DOTA_BaseNPC_Creep_Lane");
		while(it.hasNext()){
			Entity e = it.next();
			//System.out.println(e.getProperty("m_iAttackCapabilities")+ " "+e.getProperty("m_iMaxHealth"));
			System.out.println("creep "+(Integer)e.getProperty("m_hOwnerEntity"));
			
		}*/
		
		/*Iterator<Entity> it2 = current_match.getEntities().getAllByDtName("DT_DOTA_Unit_Hero_Zuus");
		while(it2.hasNext()){
			Entity e = it2.next();
			System.out.println("mirana "+current_match.getEntities().getByHandle((Integer)e.getProperty("m_hOwnerEntity")).getProperty("m_iPlayerID"));		/*	if(lastpos != null){
				double[] pos = Utils.getPosition(e);
				if(pos[0] != lastpos[0] ||pos[1] != lastpos[1])
				{
					double len = Math.sqrt((pos[0]-lastpos[0])*(pos[0]-lastpos[0]) + (pos[1]-lastpos[1])*(pos[1]-lastpos[1]));
					System.out.println("("+(pos[0]-lastpos[0])/len+", "+(pos[1]-lastpos[1])/len+"): "+e.getProperty("m_angRotation[1]"));
				}
					
			}
			lastpos = Utils.getPosition(e);
		}*/
		
/*		Iterator<Entity> it3 = current_match.getEntities().getAllByDtName("DT_DOTA_Unit_Hero_Mirana");
		while(it3.hasNext()){
			Entity e = it3.next();
			System.out.println("mirana "+current_match.getEntities().getByHandle((Integer)e.getProperty("m_hOwnerEntity")).getProperty("m_iPlayerID"));
			
			for(ReceiveProp p : current_match.getEntities().getByHandle((Integer)e.getProperty("m_hOwnerEntity")).getDtClass().getReceiveProps()){
				//System.out.println(p.getVarName());
			}
		}*/
		/*Iterator<Entity> it2 = current_match.getEntities().getAllByDtName("DT_DOTA_BaseNPC_Creep_Lane");
		while(it2.hasNext()){
			Entity e = it2.next();
			System.out.println((int)e.getProperty("m_iTeamNum")+" "+(int)e.getProperty("m_iTaggedAsVisibleByTeam"));
			//Globals.add_percent();
		}*/
		/*Iterator<Entity> it3 = current_match.getEntities().getAllByDtName("DT_DOTA_BaseNPC_Creep_Lane");
		while(it3.hasNext()){
			Entity e = it3.next();
			System.out.println((int)e.getProperty("m_iUnitNameIndex")+" "+(int)e.getProperty("m_iAttackCapabilities")+" "+(int)e.getProperty("m_iTeamNum"));
			Globals.add_percent((int)e.getProperty("m_iUnitNameIndex"));	
		}*/
		/*Iterator<Entity> it3 = current_match.getEntities().getAllByDtName("DT_DOTA_NPC_Observer_Ward");
		while(it3.hasNext()){
			Entity e = it3.next();
//			System.out.println((int)e.getProperty("m_iUnitNameIndex"));
			Globals.add_percent((int)e.getProperty("m_iUnitNameIndex"));	
		}
*/		
		return true;
	}

	private void updateStuff(){
		stuff.updateStuff(currentMatch, oldMatch);
	}
	
	private void updateModifiers(){
		modifiers.updateModifiers(currentMatch);
		
		modifierChangesByName.clear();
		
		StringTable modifierNames = currentMatch.getStringTables().forName("ModifierNames");
		
		for(ModifierChange  change : modifiers.modifierChanges()){
			
			Entity ent = currentMatch.getEntities().getByIndex((Integer)change.entry.getField("parent") & 0x7FF);
			if(ent != null){
				String rawName = modifierNames.getNameByIndex((Integer)change.entry.getField("modifier_class"));

				if(!modifierChangesByName.containsKey(rawName))
					modifierChangesByName.put(rawName, new LinkedList<ModifierChange>());
				modifierChangesByName.get(rawName).add(change);
				//System.out.println(rawName+" "+change.toString());
				
				String modifierName = ConstantMapper.modifier(rawName);

				if(units.exists(ent.getHandle()) && modifierName != null){
					int eventID = 0;
					switch(change.type){
					case CREATE:
						eventID = db.createEvent(replay.getReplayID(), Utils.getTime(currentMatch), "ModifierGain");
						break;
					case TIMEOUT:
					case REMOVE:
						eventID = db.createEvent(replay.getReplayID(), Utils.getTime(currentMatch), "ModifierLoss");
						break;
					}
					int unitID = units.getUnitID(ent.getHandle());
					db.addEventIntArgument(eventID, "Unit", unitID);
					db.addEventIntArgument(eventID, "Modifier", Constants.getIndex("Modifiers", modifierName));
					if(change.entry.hasField("caster")){
						Entity caster = currentMatch.getEntities().getByIndex((Integer)change.entry.getField("caster") & 0x7FF);
						int casterID = units.getUnitID(ent.getHandle());
						if(units.exists(caster.getHandle()) && unitID != casterID){
							db.addEventIntArgument(eventID, "ActingUnit", casterID);
						}
					}
				}
				
				/*String indexDT = "BadIndex";
				indexDT = currentMatch.getEntities().getByIndex((Integer)change.entry.getField("parent") & 0x7FF).getDtClass().getDtName();
				System.out.println(ConstantMapper.formatTime(currentMatch.getGameTime())+ " "+ConstantMapper.formatTime(currentMatch.getReplayTime())+" "+change.type+" "+modifierNames.getNameByIndex((Integer)change.entry.getField("modifier_class"))+" "+(Integer)change.entry.getField("parent")+" "+indexDT+ " "+change.entry.toString());
				*/
			}
			else {
				//MAYBE TODO: Fix not-found entities
				/*if(!change.type.equals(ModifierChange.Type.TIMEOUT))
					System.out.println("Couldn't find entity "+modifierNames.getNameByIndex((Integer)change.entry.getField("modifier_class"))+" "+change.toString());*/
			}
		}
	}

	
	private void updateAndProcessAnimations(){
		animations.updateAnimations(currentMatch);
		//Specifically ignored walking animation (activity 0)
		for(Animation a : animations.getStartedAnimations()){
			/* NOTE Check specific animation activity numbers here
			 * if(a.activity == 426)
				System.out.println(ConstantMapper.formatTime(Utils.getTime(current_match))+" "+a.entity.getDtClass().getDtName());
			*/
			if(!units.exists(a.entity.getHandle()))
				continue;
			/*if(a.activity == 438)
				System.out.println(ConstantMapper.formatTime(Utils.getTime(currentMatch))+" "+a.entity.getDtClass().getDtName());*/
			if(a.activity != 0){
				int eventID = db.createEvent(replay.getReplayID(), Utils.getTime(currentMatch), "AnimationStart");
				a.eventID = eventID;
				db.addEventIntArgument(eventID, "Unit", units.getUnitID(a.entity.getHandle()));
				//System.out.println(ConstantMapper.formatTime(Utils.getTime(currentMatch))+" Animation type "+ConstantMapper.animationType(a.type)+" "+ConstantMapper.animationAction(a.activity));
				
				String type = ConstantMapper.animationType(a.type);
				if(type.equals("Unknown")){
					if(a.activity >= 424 && a.activity <= 426)
						type = "Attack";
					else if (a.activity >= 431 && a.activity <= 436)
						type = "AbilityUse";
				}
					
				switch(type){
				case "Unknown":
					System.out.println("Unknown type: "+a.activity);
					db.addEventIntArgument(eventID, "Action", Constants.getIndex("Actions", ConstantMapper.animationAction(a.activity)));
					break;
				case "Attack":
					//attack
					db.addEventIntArgument(eventID, "Action", Constants.getIndex("Actions", ConstantMapper.animationAction(a.activity)));
					//Find target
					/*Vector2f position = Utils.getPosition(a.entity); 
					Vector2f positionOld = Utils.getPosition(oldMatch.getEntities().getByHandle(a.entity.getHandle())); 
					Vector2f dir = Utils.getDirection(a.entity);
					Vector2f dirOld = Utils.getDirection(oldMatch.getEntities().getByHandle(a.entity.getHandle()));
					double attackRangeSq = Utils.getAttackRange(a.entity) + 150;//522 speed for 2/30 sec
					attackRangeSq *= attackRangeSq;
					List<Integer> targets = new LinkedList<Integer>();
					
					System.out.println("## Searching for target "+a.entity.getDtClass().getDtName()+" "+a.entity.getProperty("m_iAttackCapabilities"));
					for(Integer handle : units.getLivingUnits()){
						TrackedUnit t = units.getUnit(handle);
						if((Integer)currentMatch.getEntities().getByHandle(t.getHandle()).getProperty("m_iTeamNum") == (int)a.entity.getProperty("m_iTeamNum") && (!t.isDenieable() || !Utils.isPlayerControlled(a.entity)))
							continue;
												
						Vector2f relative = t.getPosition();
						relative.sub(position);
						
						Vector2f relativeOld = t.getOldPosition();
						relativeOld.sub(positionOld);
						
						double angle;
						double angleChange;
						if(dir == null){
							angle = 1;
							angleChange = 0;
						}
						else{
							angle = relative.dot(dir)/relative.length();
							angleChange = angle - relativeOld.dot(dirOld)/relativeOld.length();
						}
						
						if(relative.lengthSquared() > attackRangeSq || relative.lengthSquared() == 0 || angle < 0.9 || angleChange < 0){
							//System.out.println("Skipped "+relative.length()+" "+relative.dot(dir)/relative.length()+" "+angleChange+" "+currentMatch.getEntities().getByHandle(t.getHandle()).getDtClass().getDtName());
							continue;
						}
						double distance = relative.length();
						System.out.println("Found "+distance+" "+relative.dot(dir)/distance+" "+angleChange+" "+currentMatch.getEntities().getByHandle(t.getHandle()).getDtClass().getDtName());
						targets.add(handle);
					}
					if(targets.size() == 1)
						System.out.println("Found one");
					else if(targets.size() > 1)
						System.out.println("Found multiple");
					else{
						System.out.println("None");
						for(Integer handle : units.getLivingUnits()){
							TrackedUnit t = units.getUnit(handle);
							if(!t.isDenieable() && (Integer)currentMatch.getEntities().getByHandle(t.getHandle()).getProperty("m_iTeamNum") == (int)a.entity.getProperty("m_iTeamNum"))
								continue;
													
							Vector2f relative = t.getPosition();
							relative.sub(position);
							
							Vector2f relativeOld = t.getOldPosition();
							relativeOld.sub(positionOld);
							
							double angle = relative.dot(dir)/relative.length();
							double angleChange = angle - relativeOld.dot(dirOld)/relativeOld.length();
							
							if(relative.lengthSquared() > attackRangeSq || relative.lengthSquared() == 0 || angle < 0.9 || angleChange < 0){
								System.out.println("Skipped "+relative.length()+" "+relative.dot(dir)/relative.length()+" "+angleChange+" "+currentMatch.getEntities().getByHandle(t.getHandle()).getDtClass().getDtName());
								continue;
							}
						}
					}*/
					break;
				case "AbilityUse":
					int abilityIndex = a.activity - 431;
					if(a.activity > 436){
						db.addEventIntArgument(eventID, "Action", Constants.getIndex("Actions", ConstantMapper.animationAction(a.activity)));
						break;
					}						
					//ability?
					int abilityHandle = (Integer)a.entity.getProperty("m_hAbilities.000"+abilityIndex);
					String rawName = currentMatch.getEntities().getByHandle(abilityHandle).getProperty("m_iName");
					String name = ConstantMapper.abilityName(rawName);
					db.addEventIntArgument(eventID, "Action", Constants.getIndex("Actions", name));
					if(name == null)
						break;
					switch(name){
					//Find target if necessary
					default:
						break;
					}
					break;
				default:
					System.out.println("Unknown animation type");
					break;
					//System.out.println("animation type "+a.type);
				}
			}
			System.out.println("Started "+a.toString());
		}
		for(Animation a : animations.getCastedAnimations()){
			if(!units.exists(a.entity.getHandle()))
				continue;
			if(a.activity != 0){
				int eventID = db.createEvent(replay.getReplayID(), Utils.getTime(currentMatch), "AnimationCast");
				db.addEventIntArgument(eventID, "AnimationEvent", a.eventID);
			}
			
			//TODO Search affected
			if(Utils.isAttack(a))
				attackingUnits.add(a.entity.getHandle());
			System.out.println("Casted "+a.toString());
		}
		for(Animation a : animations.getCancelledAnimations()){
			if(!units.exists(a.entity.getHandle()))
				continue;
			if(a.activity != 0){
				int eventID = db.createEvent(replay.getReplayID(), Utils.getTime(currentMatch), "AnimationCancel");
				db.addEventIntArgument(eventID, "AnimationEvent", a.eventID);		
			}
			System.out.println("Cancelled "+a.toString());
		}
		for(Animation a : animations.getStoppedAnimations()){
			if(!units.exists(a.entity.getHandle()))
				continue;
			if(a.activity != 0){
				int eventID = db.createEvent(replay.getReplayID(), Utils.getTime(currentMatch), "AnimationStop");
				db.addEventIntArgument(eventID, "AnimationEvent", a.eventID);
			}
			System.out.println("Stopped "+a.toString());
		}
	}
	
	private void processCombatLog(){
		GameEventDescriptor combatlog_descriptor = currentMatch.getGameEventDescriptors().forName("dota_combatlog");
		if(combatlog_descriptor == null)
			return;
		CombatLogEntry.init(
                currentMatch.getStringTables().forName("CombatLogNames"),
                combatlog_descriptor
            );
		//System.out.println("CLE id: "+combatlog_descriptor.getEventId());
		List<LinkedList<CombatLogEntry>> grouped_cles = new LinkedList<LinkedList<CombatLogEntry>>();
		for (GameEvent g : currentMatch.getGameEvents()) {
			 if (g.getEventId() != combatlog_descriptor.getEventId()) {
				 //System.out.println("Strange event id "+g.getEventId()+" "+g.getName());
				 continue;
            }
            CombatLogEntry cle = new CombatLogEntry(g);
            System.out.println(cle.toString());
            /*if(true)
            	continue;*/
            boolean found = false;
            for(LinkedList<CombatLogEntry> group : grouped_cles){
            	CombatLogEntry group_entry = group.get(0);
            	if(shouldGroupCLEs(cle, group_entry)){
            		group.add(cle);
            		found = true;
            	}
            }
            if(!found){
            	LinkedList<CombatLogEntry> new_group = new LinkedList<CombatLogEntry>();
            	new_group.add(cle);
            	grouped_cles.add(new_group);
            }
		}/*
		 for (LinkedList<CombatLogEntry> group : grouped_cles) {
			 CombatLogEntry firstEntry = group.get(0);
             //System.out.println("Times replay "+match.getReplayTime()+" game "+match.getGameTime()+" raw "+cle.getTimestampRaw()+" timestamp "+cle.getTimestamp());
			 //System.out.println("Group size "+group.size()+" "+firstEntry.getType()+" "+firstEntry.getTargetName());
			 if(group.size() == 1){
				 Event e = new Event();
				 CombatLogEntry cle = firstEntry;
				 switch(firstEntry.getType()) {
				 	case 0:
				 		//System.out.println(group.size()+" "+firstEntry.toString());
		            	 //+e.acting_unit = findUnit(cle.getAttackerName(), cle, attacker);
		                 //+e.affected_unit = findUnit(cle.getTargetName(), cle, target);
		                 e.action = cle.getInflictorName();
		                 e.value = cle.getValue();
		                 break;
				 	case 1:
		            	//Heal
		            	 //+e.acting_unit = findUnit(cle.getAttackerName(), cle, attacker);
		                 //+e.affected_unit = findUnit(cle.getTargetName(), cle, target);
		                 e.action = cle.getInflictorName();
		                 e.value = cle.getValue();
		                 break;
		             case 2:
		            	 //already tracked
		            	 break;
		            	 //Buff add
		            	 //+e.acting_unit = findUnit(cle.getAttackerName(), cle, attacker);
		                 //+e.affected_unit = findUnit(cle.getTargetName(), cle, target);
		                 //e.action = cle.getInflictorName();
		                 //e.value = cle.getValue();
		                 
		                 //break;
		             case 3:
		            	 //already tracked
		            	 break;
		            	 //Buff loss
		            	 //e.acting_unit = findUnit(cle.getAttackerName(), cle, attacker);
		                 //+e.affected_unit = findUnit(cle.getTargetName(), cle, target);
		                 //e.action = cle.getInflictorName();
		                 //e.value = cle.getValue();
		                 //break;
		             case 4:
		            	 //unit Kill
		            	 //e.acting_unit = findUnit(cle.getAttackerName(), cle, attacker);
		                 //+e.affected_unit = findUnit(cle.getTargetName(), cle, target);
		                 //e.action = cle.getInflictorName();
		                 //e.value = cle.getValue();
		                 break;
		             case 5:
		            	 //Ability use
		            	 //+e.acting_unit = findUnit(cle.getAttackerName(), cle, attacker);
		                 //+e.affected_unit = findUnit(cle.getTargetName(), cle, target);
		                 e.action = ConstantMapper.abilityName(cle.getInflictorName());
		                 //e.value = cle.getValue();
		                 //System.out.println(cle.toString());
		            	 break;
		             case 6:
		            	 //Item use
		            	 //+e.acting_unit = findUnit(cle.getAttackerName(), cle, attacker);
		                 //e.affected_unit = findUnit(cle.getTargetName(), cle, target);
		                 e.action = ConstantMapper.itemName(cle.getInflictorName());
		                 //e.value = cle.getValue();
		            	 break;
		             case 8:
		            	 //Gold gain
		            	 //e.acting_unit = findUnit(cle.getAttackerName(), cle.isAttackerIllusion());
		                 //+e.affected_unit = findUnit(cle.getTargetName(), cle, target);
		                 //e.action = cle.getInflictorName();
		                 e.value = cle.getValue();
	
		            	 break;
		             case 9:
		            	 //Gamestate change
		            	 //e.acting_unit = findUnit(cle.getAttackerName(), cle.isAttackerIllusion());
		                 //e.affected_unit = findUnit(cle.getTargetName(), cle.isTargetIllusion());
		                 //e.action = cle.getInflictorName();
		                 e.value = cle.getValue();
		            	 break;
		             case 10:
		            	 //EXP gain
		            	 //e.acting_unit = findUnit(cle.getAttackerName(), cle.isAttackerIllusion());
		                 //+e.affected_unit = findUnit(cle.getTargetName(), cle, target);
		                 //e.action = cle.getInflictorName();
		                 e.value = cle.getValue();
		            	 break;
		             case 11:
		            	 //item purchase
		            	 //e.acting_unit = findUnit(cle.getAttackerName(), cle.isAttackerIllusion());
		                 //+e.affected_unit = findUnit(cle.getTargetName(), cle, target);
		                 //e.action = cle.getInflictorName();
		                 int purchaseID = db.createEvent(replay.getReplayID(), Utils.getTime(currentMatch), "ItemPurchase");
		                 String hero = ConstantMapper.unitName(cle.getTargetName());
		                 
		                 db.addEventIntArgument(purchaseID, "Player", replay.getPlayerID(hero));
	                	 db.addEventIntArgument(purchaseID, "Item", Constants.getIndex("Items", ConstantMapper.itemName(cle.getValueName())));
		            	 break;
		             default:
		            	 System.out.format(MessageFormat.format("\nUNKNOWN: {0}\n", cle.toString()));
		                 break;
				 }
			 }
			 else{
				 //System.out.println("Skipping group "+group.size());
			 }
		 }*/
	}
	
	private boolean shouldGroupCLEs(CombatLogEntry a, CombatLogEntry b){
		if(a.getType() != b.getType()){
			boolean ok = false;
			if((a.getType() == 0 && b.getType() == 1) || (a.getType() == 1 || b.getType() == 0))
				ok = true;
			else if((a.getType() == 2 && b.getType() == 3) || (a.getType() == 3 || b.getType() == 2))
				ok = true;
			if(!ok)
				return false;
		}
		if(a.getTargetName() != b.getTargetName() || a.isTargetIllusion() != b.isTargetIllusion())
			return false;
		if(a.getInflictorName() != b.getInflictorName())
			return false;
		return true;
	}
	public TrackedUnit findUnit(String combatlog_name, CombatLogEntry cle, int index){
		if(combatlog_name == null)
			return null;
		
		boolean isIllusion = false;
		boolean isAttacker = true;
		if(index == attacker){
			isIllusion = cle.isAttackerIllusion();
			isAttacker = true;
		}
		else if(index == target){
			isIllusion = cle.isTargetIllusion();
			isAttacker = false;
		}
		else
			System.out.println("Bad index");
		List<Entity> candidates = new LinkedList<Entity>();
		List<Entity> unit_candidates = new LinkedList<Entity>();
		String unitName = ConstantMapper.unitName(combatlog_name);
		Iterator<Entity> it = currentMatch.getEntities().getAllByDtName(ConstantMapper.DTClassForName(unitName));
		while(it.hasNext()){
			Entity e = it.next();
			boolean unit_ok = true;
			if(e.getDtClass().getPropertyIndex("m_iUnitNameIndex") != null && ConstantMapper.nameForIndex((Integer)e.getProperty("m_iUnitNameIndex")) != unitName){
				unit_ok = false;
			}
			if(!isIllusion){
				if(e.getDtClass().getPropertyIndex("m_hReplicatingOtherHeroModel") == null || (Integer)e.getProperty("m_hReplicatingOtherHeroModel") == 2097151){
					candidates.add(e);
					if(unit_ok)
						unit_candidates.add(e);
				}
			}
			else{
				if(e.getDtClass().getPropertyIndex("m_hReplicatingOtherHeroModel") != null && (Integer)e.getProperty("m_hReplicatingOtherHeroModel") != 2097151){
					candidates.add(e);
					if(unit_ok)
						unit_candidates.add(e);
				}
			}
		}
		int handle = -1;
		if(unit_candidates.size() > 0)
			candidates = unit_candidates;
		if(candidates.size() == 1)
		{
			handle = candidates.get(0).getHandle();
			//System.out.println("Single candidate");
		}
		else if(candidates.size() == 0){
			System.out.println("No candidates found: "+combatlog_name+", "+unitName+", "+ConstantMapper.DTClassForName(unitName)+"for cle: "+cle.toString());
			
		}
		else{
			Entity result = null;
			boolean multiple = false;
			
			switch(cle.getType()){
			case 0:
				if(!isAttacker){
					System.out.println(cle.toString());
					System.out.println("Multiple got hit "+candidates.size());
					//System.out.println("Value: "+cle.getValue());
					Iterator<Entity> candidate_it = candidates.iterator();
					while(candidate_it.hasNext()){
						Entity e = candidate_it.next();
						Entity old = oldMatch.getEntities().getByHandle(e.getHandle());
						//System.out.println(e.toString());
						if(old == null)
							continue;
						//System.out.println(old.toString());
						int health_old = 0;
						int health_new = 0;
						boolean from_percent = false;
						if(e.getDtClass().getPropertyIndex("m_iHealth") != null){
							health_old = (Integer)old.getProperty("m_iHealth");
							health_new = (Integer)e.getProperty("m_iHealth");
						}
						else if(e.getDtClass().getPropertyIndex("m_iHealthPercentage") != null){
							health_old = (int)((Integer)old.getProperty("m_iHealthPercentage")*(1/127.0)*(Integer)old.getProperty("m_iMaxHealth"));
							health_new = (int)((Integer)e.getProperty("m_iHealthPercentage")*(1/127.0)*(Integer)e.getProperty("m_iMaxHealth"));
							System.out.println(health_old+" -> "+health_new);
							from_percent = true;
							//Globals.add_percent((Integer)e.getProperty("m_iHealthPercentage"));
						}
						else 
							System.out.println("No way to determine health");
						
						if(health_old != health_new){
							System.out.println("keep changed Healths: "+health_old+", "+health_new);
							//if(e.getDtClass().getPropertyIndex("m_flHealthThinkRegen") != null)
							//	System.out.println("reg: "+e.getProperty("m_flHealthThinkRegen")+" "+old.getProperty("m_flHealthThinkRegen"));
						}
						else{
							candidate_it.remove();
							continue;
						}
						if(!from_percent){
							if(health_new != cle.getHealth() || (Math.abs(health_old-health_new- cle.getValue()) > 3 && 
									! (health_old <= cle.getValue() && health_new == 0) )){
								candidate_it.remove();
								continue;
							}
						}
						else{
							System.out.println((double)(health_old - health_new) / cle.getValue()+", "+health_new+" "  );
						}
						
					}
					if(candidates.size() == 1){
						result = candidates.get(0);
						multiple = false;
					}
					else
						multiple = true;
				}
				else{
					//Disambiguate attackers
					//System.out.println("Multiple attackers");
				}
				break;
			case 4:
				if(!isAttacker){
					for(Entity e : candidates){
						Entity old = oldMatch.getEntities().getByHandle(e.getHandle());
						if(old == null){
							System.out.println("Couldnt find old ent");							
							continue;
						}
						else{
							if((Integer)e.getProperty("m_lifeState") == 1 && (Integer)old.getProperty("m_lifeState") == 0){
								if(result != null)
									multiple = true;
								else
									result = e;
							}
						}
					}						
				}
				else{
					//Disambiguate killers
				}
				break;
			default:
				break;
			}
			
			
			if(result != null && multiple == false)
			{
				handle = result.getHandle();
				//System.out.println("Resolved "+unitName +": "+(Integer)result.getProperty("m_iUnitNameIndex"));
				if(!Globals.add((Integer)result.getProperty("m_iUnitNameIndex"), unitName)){
					System.out.println("Strange add "+unitName+" "+(Integer)result.getProperty("m_iUnitNameIndex"));
					
				}
			}
			else{
				System.out.println("Unresolved candidates: "+candidates.size());//+"\n"+cle.toString());
			}
		}
		
		if(!units.exists(handle)){
		//	units.put(handle, new TrackedUnit(handle, ConstantMapper.unitName(combatlog_name)));
			System.out.println("Reference to unknown unit?");
		}
		//System.out.println("Found unit");
		return null;//nullunits.getUnitID(handle);
	}
	
	public void finish() {
		/*TODO apparently some particles are not removed
		 * mostly: 	sf necromastery souls
		 * 			Orchid pop
		 * 			Rosh timer, maybe only the last one
		 * System.out.println("Left Particles"+ stuff.allParticles().size());
		for(Particle p : stuff.allParticles().values()){
			System.out.println("Particle "+p.toString());
			for(UserMessage m: p.messages)
				System.out.println(m.toString());
			System.out.println();
		}*/
		//Globals.print_names();
		Globals.printCountedInts();
		Globals.printCountedStrings();
		Globals.printCounter();
	}
}
