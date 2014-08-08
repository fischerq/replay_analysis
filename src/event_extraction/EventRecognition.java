package event_extraction;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.rits.cloning.Cloner;

import database.Database;
import skadistats.clarity.match.EntityCollection;
import skadistats.clarity.match.Match;
import skadistats.clarity.match.Snapshot;
import skadistats.clarity.model.Entity;
import skadistats.clarity.model.GameEvent;
import skadistats.clarity.model.GameEventDescriptor;
import skadistats.clarity.model.ReceiveProp;
import skadistats.clarity.model.StringTable;
import utils.ConstantMapper;
import utils.Encoder;


public class EventRecognition {

	
	private Database db = null;
	
	private Map<Integer, TrackedUnit> units = new HashMap<Integer, TrackedUnit>();
	
	private Match current_match = null;
	private Match old_match = null;
	
	private int handle_tracked = -1;
	private Entity last_ent = null;
	
	
	private static int attacker = 0;
	private static int target = 1;
	
	public void analyseTick(Match match, Match match_old) {
		
		current_match = match;
		old_match = match_old;
		
		GameEventDescriptor combatlog_descriptor = match.getGameEventDescriptors().forName("dota_combatlog");
		CombatLogEntry.init(
                match.getStringTables().forName("CombatLogNames"),
                combatlog_descriptor
            );
		
		//DT_DOTAFogOfWarWasVisible
		//System.out.println(player_resource.toString());
		//if(doPrints())
		//	return;
		
		
		//System.out.println("Tick");
		List<LinkedList<CombatLogEntry>> grouped_cles = new LinkedList<LinkedList<CombatLogEntry>>();
		for (GameEvent g : match.getGameEvents()) {
			 if (g.getEventId() != combatlog_descriptor.getEventId()) {
				 System.out.println("Strange event id "+g.getEventId());
				 continue;
            }
            CombatLogEntry cle = new CombatLogEntry(g);
            //System.out.println(cle.toString());
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
		}
		 for (LinkedList<CombatLogEntry> group : grouped_cles) {
			 CombatLogEntry firstEntry = group.get(0);
             //System.out.println("Times replay "+match.getReplayTime()+" game "+match.getGameTime()+" raw "+cle.getTimestampRaw()+" timestamp "+cle.getTimestamp());
			 //System.out.println("Group size "+group.size()+" "+firstEntry.getType()+" "+firstEntry.getTargetName());
			 if(group.size() == 1){
				 Event e = new Event();
				 CombatLogEntry cle = firstEntry;
				 switch(firstEntry.getType()) {
				 	case 0:
		            	 //e.acting_unit = findUnit(cle.getAttackerName(), cle, attacker);
		                 e.affected_unit = findUnit(cle.getTargetName(), cle, target);
		                 e.action = cle.getInflictorName();
		                 e.value = cle.getValue();
		                 break;
				 	case 1:
		            	//Heal
		            	 //e.acting_unit = findUnit(cle.getAttackerName(), cle, attacker);
		                 //e.affected_unit = findUnit(cle.getTargetName(), cle, target);
		                 e.action = cle.getInflictorName();
		                 e.value = cle.getValue();
		                 break;
		             case 2:
		            	 //Buff add
		            	 //e.acting_unit = findUnit(cle.getAttackerName(), cle, attacker);
		                 //e.affected_unit = findUnit(cle.getTargetName(), cle, target);
		                 e.action = cle.getInflictorName();
		                 //e.value = cle.getValue();
		                 
		                 break;
		             case 3:
		            	 //Buff loss
		            	 //e.acting_unit = findUnit(cle.getAttackerName(), cle, attacker);
		                 //e.affected_unit = findUnit(cle.getTargetName(), cle, target);
		                 e.action = cle.getInflictorName();
		                 //e.value = cle.getValue();
		                 break;
		             case 4:
		            	 //unit Kill
		            	 //e.acting_unit = findUnit(cle.getAttackerName(), cle, attacker);
		                 e.affected_unit = findUnit(cle.getTargetName(), cle, target);
		                 //e.action = cle.getInflictorName();
		                 //e.value = cle.getValue();
		                 break;
		             case 5:
		            	 //Ability use
		            	 e.acting_unit = findUnit(cle.getAttackerName(), cle, attacker);
		                 //e.affected_unit = findUnit(cle.getTargetName(), cle, target);
		                 e.action = ConstantMapper.abilityName(cle.getInflictorName());
		                 //e.value = cle.getValue();
		            	 break;
		             case 6:
		            	 //Item use
		            	 e.acting_unit = findUnit(cle.getAttackerName(), cle, attacker);
		                 //e.affected_unit = findUnit(cle.getTargetName(), cle, target);
		                 e.action = ConstantMapper.itemName(cle.getInflictorName());
		                 //e.value = cle.getValue();
		            	 break;
		             case 8:
		            	 //Gold gain
		            	 //e.acting_unit = findUnit(cle.getAttackerName(), cle.isAttackerIllusion());
		                 e.affected_unit = findUnit(cle.getTargetName(), cle, target);
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
		                 e.affected_unit = findUnit(cle.getTargetName(), cle, target);
		                 //e.action = cle.getInflictorName();
		                 e.value = cle.getValue();
		            	 break;
		             case 11:
		            	 //item purchase
		            	 //e.acting_unit = findUnit(cle.getAttackerName(), cle.isAttackerIllusion());
		                 e.affected_unit = findUnit(cle.getTargetName(), cle, target);
		                 //e.action = cle.getInflictorName();
		                 e.action = ConstantMapper.itemName(cle.getValueName());
	
		            	 break;
		             default:
		            	 System.out.format(MessageFormat.format("\nUNKNOWN: {0}\n", cle.toString()));
		                 break;
				 }
			 }
			 else{
				 //System.out.println("Skipping group "+group.size());
			 }
		 }
		 
		 
		 
	}
	
	public boolean doPrints(){
		/*Entity player_resource;
		if(current_match.getEntities().getAllByDtName("DT_DOTA_PlayerResource").hasNext())
			player_resource	= current_match.getEntities().getAllByDtName("DT_DOTA_PlayerResource").next();
		else 
			return;
		Integer[] selected_heroes = player_resource.getArrayProperty(Integer.class, "m_nSelectedHeroID");
		String[] nicks = player_resource.getArrayProperty(String.class, "m_iszPlayerNames");
		Integer[] hero_handles = player_resource.getArrayProperty(Integer.class, "m_hSelectedHero");
		for(int i = 0; i<selected_heroes.length; ++i){
			if(selected_heroes[i] != -1)
			{
//				System.out.println(player_resource.toString());
				System.out.println("Hero handle: "+hero_handles[i]);
				if(current_match.getEntities().getByHandle(hero_handles[i]) != null)
					System.out.println(current_match.getEntities().getByHandle(hero_handles[i]).toString());
			}
		}*/
		/*
		if(handle_tracked < 0){
			Iterator<Entity> it =current_match.getEntities().getAllByDtName("DT_DOTA_BaseNPC_Creep_Lane");
			while(it.hasNext()){
				Entity e =it.next();
				handle_tracked = e.getHandle();
				break;
				//System.out.println(e.getProperty("m_iTeamNum")+" "+e.getProperty("m_iMaxHealth")+" "+e.getProperty("m_iAttackCapabilities")+" "+e.getProperty("m_iUnitNameIndex"));
			}
		}
		else{
			Entity e = current_match.getEntities().getByHandle(handle_tracked);
			if(e != null){
				for(ReceiveProp prop: e.getDtClass().getReceiveProps()){
					//System.out.println(prop.getVarName());
					if(last_ent == null)
					{}	//System.out.println(prop.getVarName()+": "+ e.getProperty(prop.getVarName()));
					else{
						if(!last_ent.getProperty(prop.getVarName()).equals(e.getProperty(prop.getVarName())))
						{
							//System.out.println(prop.getVarName()+": "+ last_ent.getProperty(prop.getVarName())+" -> "+ e.getProperty(prop.getVarName()));
							if(prop.getVarName().equals("m_iHealthPercentage") )
							{
								System.out.println("found healthchange");
								int health = (int)(Encoder.stdPercent((Integer)e.getProperty("m_iHealthPercentage"))*(Integer)e.getProperty("m_iMaxHealth"));
								int healthold = (int)(Encoder.stdPercent((Integer)last_ent.getProperty("m_iHealthPercentage"))*(Integer)last_ent.getProperty("m_iMaxHealth"));
								System.out.println(healthold+" ->"+ health);
								for (GameEvent g : current_match.getGameEvents()) {
						            CombatLogEntry cle = new CombatLogEntry(g);
						            System.out.println(cle.toString());
								}
								//System.out.println(e.getProperty("m_iHealth"));
							}
						}
						//System.out.println(last_ent.getProperty(prop.getVarName()).getClass());
					}
				}
				last_ent = e.clone();
			}
			else{
				System.out.println("Changing creep");
				handle_tracked = -1;
			}
		}
		
		return true;*/
		return false;
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
		Iterator<Entity> it = current_match.getEntities().getAllByDtName(ConstantMapper.DTClassForName(unitName));
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
			System.out.println("No candidates found: "+combatlog_name+", "+unitName+", "+ConstantMapper.DTClassForName(unitName));
			
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
						Entity old = old_match.getEntities().getByHandle(e.getHandle());
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
						Entity old = old_match.getEntities().getByHandle(e.getHandle());
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
		
		if(!units.containsKey(handle)){
			units.put(handle, new TrackedUnit(handle, ConstantMapper.unitName(combatlog_name)));
		}
		//System.out.println("Found unit");
		return units.get(handle);
	}
	
	public void finish() {
		// TODO Auto-generated method stub
		//Globals.print_names();
		//Globals.print_percent();
	}


	public void setDatabase(Database db) {
		this.db = db;
	}

}
