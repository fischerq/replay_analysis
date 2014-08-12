package data_extraction;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;



import database.Constants;
import database.Database;
import skadistats.clarity.match.Match;
import skadistats.clarity.model.Entity;
import skadistats.clarity.model.GameEvent;
import skadistats.clarity.model.GameEventDescriptor;
import utils.ConstantMapper;


public class Extraction {

	private ReplayData replay;
	private Database db;
	private boolean wroteTeams;
	private boolean wrotePlayers;
	
	private Map<Integer, TrackedUnit> units = new HashMap<Integer, TrackedUnit>();
	private Set<Integer> ignoredUnits = new HashSet<Integer>();
	private Set<String> trackedClasses;
	
	private AnimationTracker animations = new AnimationTracker();
	
	
	private Match current_match = null;
	private Match old_match = null;
		
	private static int attacker = 0;
	private static int target = 1;
	
	public Extraction(int id, Database db){
		this.db = db;
		replay = new ReplayData(id, db);
		wroteTeams = false;
		wrotePlayers = false;
		
		trackedClasses = new HashSet<String>();
		/*for(String unit : Constants.unitTypes.keySet()){
			trackedClasses.add(ConstantMapper.DTClassForName(unit));
		}*/
		trackedClasses.add(ConstantMapper.DTClassForName("Zeus"));
	}
	
	public void analyseTick(Match match, Match match_old) {
		db.startTransaction();
		if(!wroteTeams)
			wroteTeams = replay.tryWriteTeams(match);
		
		if(wroteTeams && !wrotePlayers){
			wrotePlayers = replay.writePlayers(match, db);
		}
		
		//System.out.println(ConstantMapper.formatTime(match.getReplayTime())+" Tick");
		current_match = match;
		old_match = match_old;
		
		//if(true)
		//	return;
		doPrints();

		
		
		animations.updateAnimations(match);
		
		for(Animation a : animations.getStartedAnimations()){
			//System.out.println("Started "+a.toString());
		}
		for(Animation a : animations.getCastedAnimations()){
			//System.out.println("Casted "+a.toString());
		}
		for(Animation a : animations.getCancelledAnimations()){
			//System.out.println("Cancelled "+a.toString());
		}
		for(Animation a : animations.getStoppedAnimations()){
			//System.out.println("Stopped "+a.toString());
		}
		
		updateUnits();
		
		processCombatLog();
		 
		db.stopTransaction();
	}
	
	private boolean doPrints(){
		/*Iterator<Entity> it = current_match.getEntities().getAllByDtName("DT_DOTAPlayer");
		System.out.println("Tick");
		while(it.hasNext()){
			Entity e = it.next();
			System.out.println(e.toString());
			//System.out.println("creep "+(Integer)e.getProperty("m_hOwnerEntity"));
		}
		*/
		
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
	
	private void updateUnits(){
		for(String dtClass : trackedClasses){
			Iterator<Entity> it = current_match.getEntities().getAllByDtName(dtClass);
			while(it.hasNext()){
				Entity e = it.next();
				if(!units.containsKey(e.getHandle()))
					units.put(e.getHandle(), new TrackedUnit(e, replay, db));
			}
		}
		
		for(TrackedUnit u : units.values())
			u.update(current_match, old_match);
	}
	
	private void processCombatLog(){
		GameEventDescriptor combatlog_descriptor = current_match.getGameEventDescriptors().forName("dota_combatlog");
		if(combatlog_descriptor == null)
			return;
		CombatLogEntry.init(
                current_match.getStringTables().forName("CombatLogNames"),
                combatlog_descriptor
            );
		
		List<LinkedList<CombatLogEntry>> grouped_cles = new LinkedList<LinkedList<CombatLogEntry>>();
				for (GameEvent g : current_match.getGameEvents()) {
					 if (g.getEventId() != combatlog_descriptor.getEventId()) {
						 //System.out.println("Strange event id "+g.getEventId()+" "+g.getName());
						 continue;
		            }
		            CombatLogEntry cle = new CombatLogEntry(g);
		            //System.out.println(cle.toString());
		            if(true)
		            	continue;
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
		//	units.put(handle, new TrackedUnit(handle, ConstantMapper.unitName(combatlog_name)));
			System.out.println("Reference to unknown unit?");
		}
		//System.out.println("Found unit");
		return units.get(handle);
	}
	
	public void finish() {
		// TODO Auto-generated method stub
		//Globals.print_names();
		Globals.print_percent();
		//Globals.print_classes();
	}
}
