package event_extraction;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import database.Database;
import skadistats.clarity.match.Match;
import skadistats.clarity.model.Entity;
import skadistats.clarity.model.GameEvent;
import skadistats.clarity.model.GameEventDescriptor;
import skadistats.clarity.model.StringTable;
import utils.ConstantMapper;


public class EventRecognition {

	
	private Database db = null;
	
	private Map<Integer, TrackedUnit> units = new HashMap<Integer, TrackedUnit>();
	
	private Match last_state = null;
	private Match current_match = null;
	
	public void analyseTick(Match match) {		
		last_state = current_match;
		current_match = match;
		
		//DT_DOTAFogOfWarWasVisible
		//System.out.println(player_resource.toString());
		doPrints();
		
		GameEventDescriptor combatlog_descriptor = match.getGameEventDescriptors().forName("dota_combatlog");
		CombatLogEntry.init(
                match.getStringTables().forName("CombatLogNames"),
                combatlog_descriptor
            );
		
		//System.out.println("Time "+match.getGameTime()+"\n");
		 for (GameEvent g : match.getGameEvents()) {
			 //System.out.println(g.getEventId()+" "+match.getGameEventDescriptors().forId(g.getEventId()).getName()+"  "+g.toString());
			 if (g.getEventId() != combatlog_descriptor.getEventId()) {
				 System.out.println("Strange event id "+g.getEventId());
				 continue;
             }
             CombatLogEntry cle = new CombatLogEntry(g);
             Event e = new Event();
             e.type = cle.getType();
             
             String time = "["+ (int)(cle.getTimestampRaw()/60)+":"+(int)(cle.getTimestampRaw()%60)+"."+(int)((cle.getTimestampRaw()*1000)%1000)+ "]";
             switch(cle.getType()) {
             case 0:
             /*    System.out.format(MessageFormat.format("{0} {1} hits {2}{3} for {4} damage{5}\n",
                     time,
                     cle.getAttackerNameCompiled(),
                     cle.getTargetNameCompiled(),
                     cle.getInflictorName() != null ? String.format(" with %s", cle.getInflictorName()) : "",
                     cle.getValue(),
                     cle.getHealth() != 0 ? String.format(" (%s->%s)", cle.getHealth() + cle.getValue(), cle.getHealth()) : ""
                 ));*/
                 e.acting_unit = findUnit(cle.getAttackerName(), cle.isAttackerIllusion());
                 e.affected_unit = findUnit(cle.getTargetName(), cle.isTargetIllusion());
                 e.action = cle.getInflictorName();
                 e.value = cle.getValue();
                 break;
             case 1:
    /*        	 System.out.format(MessageFormat.format("{0} {1}'s {2} heals {3} for {4} health ({5}->{6})\n",
                     time,
                     cle.getAttackerNameCompiled(),
                     cle.getInflictorName(),
                     cle.getTargetNameCompiled(),
                     cle.getValue(),
                     cle.getHealth() - cle.getValue(),
                     cle.getHealth()
                 ));*/
            	 e.acting_unit = findUnit(cle.getAttackerName(), cle.isAttackerIllusion());
                 e.affected_unit = findUnit(cle.getTargetName(), cle.isTargetIllusion());
                 e.action = cle.getInflictorName();
                 e.value = cle.getValue();
                 break;
             case 2:
            /*	 System.out.format(MessageFormat.format("{0} {1} receives {2} buff/debuff from {3}\n",
                     time,
                     cle.getTargetNameCompiled(),
                     cle.getInflictorName(),
                     cle.getAttackerNameCompiled()
                 ));*/
            	 e.acting_unit = findUnit(cle.getAttackerName(), cle.isAttackerIllusion());
                 e.affected_unit = findUnit(cle.getTargetName(), cle.isTargetIllusion());
                 e.action = cle.getInflictorName();
                 //e.value = cle.getValue();
                 break;
             case 3:
            /*	 System.out.format(MessageFormat.format("{0} {1} loses {2} buff/debuff\n",
                     time,
                     cle.getTargetNameCompiled(),
                     cle.getInflictorName()
                 ));*/
            	 e.acting_unit = findUnit(cle.getAttackerName(), cle.isAttackerIllusion());
                 e.affected_unit = findUnit(cle.getTargetName(), cle.isTargetIllusion());
                 e.action = cle.getInflictorName();
                 //e.value = cle.getValue();
                 break;
             case 4:
            /*	 System.out.format(MessageFormat.format("{0} {1} is killed by {2}\n",
                     time,
                     cle.getTargetNameCompiled(),
                     cle.getAttackerNameCompiled()
                 ));*/
            	 e.acting_unit = findUnit(cle.getAttackerName(), cle.isAttackerIllusion());
                 e.affected_unit = findUnit(cle.getTargetName(), cle.isTargetIllusion());
                 //e.action = cle.getInflictorName();
                 //e.value = cle.getValue();
                 break;
             case 5:
            /*	 System.out.format(MessageFormat.format("{0} {1} uses skill {2} with target {3}\n",
                 time,
                 cle.getAttackerNameCompiled(),
                 cle.getInflictorName(),
                 cle.getTargetNameCompiled()
             ));*/
            	 e.acting_unit = findUnit(cle.getAttackerName(), cle.isAttackerIllusion());
                 e.affected_unit = findUnit(cle.getTargetName(), cle.isTargetIllusion());
                 e.action = cle.getInflictorName();
                 //e.value = cle.getValue();
            	 break;
             case 6:
            /*	 System.out.format(MessageFormat.format("{0} {1} uses item {2}\n",
                 time,
                 cle.getAttackerNameCompiled(),
                 cle.getInflictorName()                 
             ));*/
            	 e.acting_unit = findUnit(cle.getAttackerName(), cle.isAttackerIllusion());
                 //e.affected_unit = findUnit(cle.getTargetName(), cle.isTargetIllusion());
                 e.action = cle.getInflictorName();
                 //e.value = cle.getValue();
            	 break;
             case 8:
            /*	 System.out.format(MessageFormat.format("{0} {1} gains {2} gold\n",
                 time,
                 cle.getTargetNameCompiled(),
                 cle.getValue()
             ));*/
            	 //e.acting_unit = findUnit(cle.getAttackerName(), cle.isAttackerIllusion());
                 e.affected_unit = findUnit(cle.getTargetName(), cle.isTargetIllusion());
                 //e.action = cle.getInflictorName();
                 e.value = cle.getValue();
            	 break;
             case 9:
            	 System.out.format(MessageFormat.format("{0} Gamestate changes to {1}\n",
                 time,
                 cle.getValue()
             ));
            	 //e.acting_unit = findUnit(cle.getAttackerName(), cle.isAttackerIllusion());
                 //e.affected_unit = findUnit(cle.getTargetName(), cle.isTargetIllusion());
                 //e.action = cle.getInflictorName();
                 e.value = cle.getValue();
            	 break;
             case 10:
            /*	 System.out.format(MessageFormat.format("{0} {1} gained {2} exp\n",
                 time,
                 cle.getTargetNameCompiled(),
                 cle.getValue()
             ));*/
            	 //e.acting_unit = findUnit(cle.getAttackerName(), cle.isAttackerIllusion());
                 e.affected_unit = findUnit(cle.getTargetName(), cle.isTargetIllusion());
                 //e.action = cle.getInflictorName();
                 e.value = cle.getValue();
            	 break;
             case 11:
            /*	 System.out.format(MessageFormat.format("{0} {1} purchases {2}\n",
                 time,
                 cle.getTargetNameCompiled(),
                 cle.getValueName()
             ));*/
            	 //e.acting_unit = findUnit(cle.getAttackerName(), cle.isAttackerIllusion());
                 e.affected_unit = findUnit(cle.getTargetName(), cle.isTargetIllusion());
                 //e.action = cle.getInflictorName();
                 e.action = cle.getValueName();
            	 break;
             default:
            	 System.out.format(MessageFormat.format("\nUNKNOWN: {0}\n", g));
                 break;
             }
		 }
		 
		
	}
	
	public void doPrints(){
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
	}

	public TrackedUnit findUnit(String combatlog_name, boolean isIllusion){
		if(combatlog_name == "null")
			return null;
		List<Entity> candidates = new LinkedList<Entity>();
		Iterator<Entity> it = current_match.getEntities().getAllByDtName(ConstantMapper.DTClassForName(ConstantMapper.unitName(combatlog_name)));
		while(it.hasNext()){
			Entity e = it.next();
			if(!isIllusion){
				if(e.getDtClass().getPropertyIndex("m_hReplicatingOtherHeroModel") == null || (Integer)e.getProperty("m_hReplicatingOtherHeroModel") == 2097151)
					candidates.add(e);
			}
			else{
				if(e.getDtClass().getPropertyIndex("m_hReplicatingOtherHeroModel") != null && (Integer)e.getProperty("m_hReplicatingOtherHeroModel") != 2097151)
					candidates.add(e);
			}
		}
		if(candidates.size() == 1)
		{
			int handle = candidates.get(0).getHandle();
			if(!units.containsKey(handle)){
				units.put(handle, new TrackedUnit(handle, ConstantMapper.unitName(combatlog_name)));
			}
			//System.out.println("Found unit");
			return units.get(handle);
		}
		else if(candidates.size() == 0){
			//System.out.println("No candidates found");
		}
		else{
			//System.out.println("Multiple candidates: "+candidates.size());
			/*for(Entity e: candidates){
				System.out.println(e.getDtClass().getDtName());
			}*/
		}
		return null;
	}
	
	public void finish() {
		// TODO Auto-generated method stub
		
	}


	public void setDatabase(Database db) {
		this.db = db;
	}

}
