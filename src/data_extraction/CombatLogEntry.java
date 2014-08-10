package data_extraction;

// From skadistats/clarity-examples
//https://github.com/skadistats/clarity-examples/blob/master/src/main/java/skadistats/clarity/examples/combatlog/CombatLogEntry.java

import java.text.MessageFormat;

import skadistats.clarity.model.GameEvent;
import skadistats.clarity.model.GameEventDescriptor;
import skadistats.clarity.model.StringTable;
import utils.ConstantMapper;

public class CombatLogEntry {

    private static StringTable combatLogNames;

    private static int typeIdx;
    private static int sourceNameIdx;
    private static int targetNameIdx;
    private static int attackerNameIdx;
    private static int inflictorNameIdx;
    private static int attackerIllusionIdx;
    private static int targetIllusionIdx;
    private static int valueIdx;
    private static int healthIdx;
    private static int timestampIdx;
    private static int timestampRawIdx;
    private static int targetSourceNameIdx;
    
    public static void init(StringTable combatLogNamesTable, GameEventDescriptor descriptor) {
        combatLogNames = combatLogNamesTable;
        typeIdx = descriptor.getIndexForKey("type");
        sourceNameIdx = descriptor.getIndexForKey("sourcename");
        targetNameIdx = descriptor.getIndexForKey("targetname");
        attackerNameIdx = descriptor.getIndexForKey("attackername");
        inflictorNameIdx = descriptor.getIndexForKey("inflictorname");
        attackerIllusionIdx = descriptor.getIndexForKey("attackerillusion");
        targetIllusionIdx = descriptor.getIndexForKey("targetillusion");
        valueIdx = descriptor.getIndexForKey("value");
        healthIdx = descriptor.getIndexForKey("health");
        timestampIdx = descriptor.getIndexForKey("timestamp");
        timestampRawIdx = descriptor.getIndexForKey("timestampraw");
        targetSourceNameIdx = descriptor.getIndexForKey("targetsourcename");
    }
    
    private final GameEvent event;
    
    public CombatLogEntry(GameEvent event) {
        this.event = event;
    }
    
    private String readCombatLogName(int idx) {
        return idx == 0 ? null : combatLogNames.getNameByIndex(idx);
    }
    
    public int getType() {
        return event.getProperty(typeIdx);
    }
    
    public String getSourceName() {
        return readCombatLogName((Integer)event.getProperty(sourceNameIdx));
    }
    
    public String getTargetName() {
        return readCombatLogName((Integer)event.getProperty(targetNameIdx));
    }
    
    public String getTargetNameCompiled() {
        return getTargetName() + (isTargetIllusion() ? " (Illusion)" : "");
    }

    public String getAttackerName() {
        return readCombatLogName((Integer)event.getProperty(attackerNameIdx));
    }
    
    public String getAttackerNameCompiled() {
        return getAttackerName() + (isAttackerIllusion() ? " (Illusion)" : "");
    }

    public String getInflictorName() {
        return readCombatLogName((Integer)event.getProperty(inflictorNameIdx));
    }
    
    public boolean isAttackerIllusion() {
        return event.getProperty(attackerIllusionIdx);
    }
    
    public boolean isTargetIllusion() {
        return event.getProperty(targetIllusionIdx);
    }
    
    public int getValue() {
        return event.getProperty(valueIdx);
    }
    
    public String getValueName() {
        return readCombatLogName((Integer)event.getProperty(valueIdx));
    }
    
    public int getHealth() {
        return event.getProperty(healthIdx);
    }
    
    public float getTimestamp() {
        return event.getProperty(timestampIdx);
    }
    
    public float getTimestampRaw() {
        return event.getProperty(timestampRawIdx);
    }
    
    public String getTargetSourceName() {
        return readCombatLogName((Integer)event.getProperty(targetSourceNameIdx));
    }
    
    public String toString(){
    	String time = ConstantMapper.formatTime(getTimestampRaw());
    	switch(getType()) {
        case 0:
            return MessageFormat.format("{0} {1} hits {2}{3} for {4} damage{5}\n",
                time,
                getAttackerNameCompiled(),
                getTargetNameCompiled(),
                getInflictorName() != null ? String.format(" with %s", getInflictorName()) : "",
                getValue(),
                getHealth() != 0 ? String.format(" (%s->%s)", getHealth() + getValue(), getHealth()) : ""
            );
        case 1:
              return MessageFormat.format("{0} {1}'s {2} heals {3} for {4} health ({5}->{6})\n",
                    time,
                    getAttackerNameCompiled(),
                    getInflictorName(),
                    getTargetNameCompiled(),
                    getValue(),
                    getHealth() - getValue(),
                    getHealth()
                );
        case 2:
            return MessageFormat.format("{0} {1} receives {2} {3} from {4} at health {5}\n",
                     time,
                     getTargetNameCompiled(),
                     getValue() == 0 ? "buff" : "debuff",
                     getInflictorName(),
                     getAttackerNameCompiled(),
                     getHealth()
                 );
        case 3:
        	return MessageFormat.format("{0} {1} loses {2} {3} from {4} at health {5}\n",
                    time,
                    getTargetNameCompiled(),
                    getValue() == 0 ? "buff" : "debuff",
                    getInflictorName(),
                    getAttackerNameCompiled(),
                    getHealth()
                );
        case 4:
        	return MessageFormat.format("{0} {1} is killed by {2}{3}\n",
	            time,
	            getTargetNameCompiled(),
	            getAttackerNameCompiled(),
	            getAttackerName() != getSourceName() ? String.format(" owned by  %s", getSourceName()) : ""
	        );
        case 5:
            return MessageFormat.format("{0} {1} uses skill {2}{3}\n",
                 time,
                 getAttackerNameCompiled(),
                 getInflictorName(),
                 getTargetName() != null ? String.format(" on %s", getTargetName()) : "" 
             );
        case 6:
             return MessageFormat.format("{0} {1} uses item {2}{3}\n",
                 time,
                 getAttackerNameCompiled(),
                 getInflictorName(),
                 getTargetName() != null ? String.format(" on %s", getTargetName()) : ""
             );
        case 8:
            return MessageFormat.format("{0} {1} gains {2} gold\n",
                 time,
                 getTargetNameCompiled(),
                 getValue()
             );
        case 9:
	       	 return MessageFormat.format("{0} Gamestate changes to {1}\n",
	            time,
	            getValue()
	       	 );
        case 10:
        	return MessageFormat.format("{0} {1} gained {2} exp\n",
             time,
             getTargetNameCompiled(),
             getValue()
        	);
        case 11:
            return MessageFormat.format("{0} {1} purchases {2}\n",
                 time,
                 getTargetNameCompiled(),
                 getValueName()
             );
        default:
        	return "Unknown combatlog entry: "+event.toString();
    	}
    }
    
    public String rawString(){
    	return event.toString();
    }
}