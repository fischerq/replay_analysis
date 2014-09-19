package data_extraction;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import skadistats.clarity.match.Match;
import skadistats.clarity.model.ModifierTableEntry;
import skadistats.clarity.model.StringTable;
import utils.ConstantMapper;

class ModifierSerialComparator implements Comparator<ModifierTableEntry>{

	public int compare(ModifierTableEntry arg0, ModifierTableEntry arg1) {
		if(!arg0.hasField("creation_time") || !arg1.hasField("creation_time"))
			return (Integer)arg0.getField("serial_num") < (Integer)arg1.getField("serial_num") ? -1 : 1;
		
		float time_a = (Float)arg0.getField("creation_time");
		float time_b = (Float)arg1.getField("creation_time");
		if(time_a < time_b)
			return -1;
		else if(time_a > time_b)
			return 1;
		else{
			return (Integer)arg0.getField("serial_num") < (Integer)arg1.getField("serial_num") ? -1 : 1;
		}
	}
	
	/*public int compare(ModifierTableEntry arg0, ModifierTableEntry arg1) {
		if(!arg0.hasField("creation_time") && !arg1.hasField("creation_time"))
			return 0;
		else if(arg0.hasField("creation_time") && !arg1.hasField("creation_time"))
			return -1;
		else if(!arg0.hasField("creation_time") && arg1.hasField("creation_time"))
			return 1;
		else{
			float time_a = (Float)arg0.getField("creation_time");
			float time_b = (Float)arg0.getField("creation_time");
			if(time_a < time_b)
				return -1;
			else if(time_a > time_b)
				return 1;
			else
				return 0;
		}
	}*/
	
}

class ModifierEndComparator implements Comparator<ModifierTableEntry>{

	public int compare(ModifierTableEntry arg0, ModifierTableEntry arg1) {
		if(!arg0.hasField("creation_time") && !arg1.hasField("creation_time"))
			return 0;
		else if(arg0.hasField("creation_time") && !arg1.hasField("creation_time"))
			return -1;
		else if(!arg0.hasField("creation_time") && arg1.hasField("creation_time"))
			return 1;
		else{
			if(!arg0.hasField("duration") && !arg1.hasField("duration"))
				return 0;
			else if(arg0.hasField("duration") && !arg1.hasField("duration"))
				return -1;
			else if(!arg0.hasField("duration") && arg1.hasField("duration"))
				return 1;
			else{
				float time_a = (Float)arg0.getField("creation_time") + (Float)arg0.getField("duration");
				float time_b = (Float)arg1.getField("creation_time") + (Float)arg1.getField("duration");
				if(time_a < time_b)
					return -1;
				else if(time_a > time_b)
					return 1;
				else
					return 0;
			}
		}
	}
	
}

public class ModifierTracker {
	private PriorityQueue<ModifierTableEntry> storedModifiers;
	private PriorityQueue<ModifierTableEntry> untimedModifiers;
	private PriorityQueue<ModifierTableEntry> durativeModifiers;
	
	private Map<Integer, HashMap<Integer, ModifierTableEntry>> modifiersByEntity;
	
	private List<ModifierChange> modifierChanges;
	
	private int highestSerialNum;
	private Set<Integer> unusedSerials;
	
	private Match currentMatch;
	
	public ModifierTracker(){
		Comparator<ModifierTableEntry> compStart = new ModifierSerialComparator();
		storedModifiers = new PriorityQueue<ModifierTableEntry>(1, compStart);
		
		untimedModifiers = new PriorityQueue<ModifierTableEntry>(1, compStart);
		
		Comparator<ModifierTableEntry> compEnd = new ModifierEndComparator();
		durativeModifiers = new PriorityQueue<ModifierTableEntry>(1, compEnd);
		
		modifiersByEntity = new HashMap<Integer, HashMap<Integer, ModifierTableEntry>>();
		
		modifierChanges = new LinkedList<ModifierChange>();
		
		highestSerialNum = 0;	
		unusedSerials = new HashSet<Integer>();
	}
	
	public void updateModifiers(Match match){
		currentMatch = match;
		
		modifierChanges.clear();
		
		List<ModifierTableEntry> modifierEntries = match.getModifiers().getAll();

		for(ModifierTableEntry modifier : modifierEntries){
			//Globals.countString((String)modifier.getField("entry_type"));
			int serial = (Integer)modifier.getField("serial_num");
			if(serial <= highestSerialNum){
				if(unusedSerials.contains(serial))
					unusedSerials.remove(serial);
				else{
					//System.out.println("Skipping doubled serial "+modifier.toString());
					continue;
				}
			}
			else{
				for(int i = highestSerialNum +1; i < serial; ++i)
					unusedSerials.add(i);
				highestSerialNum = serial;
			}
			
			//Globals.countInt(serial);
			
			if(modifier.getField("entry_type").equals("DOTA_MODIFIER_ENTRY_TYPE_ACTIVE") && modifier.hasField("duration")){
				//System.out.println("Duration");
				durativeModifiers.add(modifier);
			}
			//StringTable modifierNames = match.getStringTables().forName("ModifierNames");
			//System.out.println(ConstantMapper.formatTime(match.getGameTime())+" "+(modifier.hasField("modifier_class")? modifierNames.getNameByIndex((Integer)modifier.getField("modifier_class")) :"")+modifier.toString());
			if(modifier.hasField("creation_time"))
				storedModifiers.add(modifier);
			else
				untimedModifiers.add(modifier);
		}		
		
		ModifierTableEntry next = storedModifiers.peek(); 
		ModifierTableEntry nextUntimed = untimedModifiers.peek(); 

		/*if(next != null){
			System.out.println("Queue: top serial "+next.getField("serial_num")+" time "+(next.getField("creation_time")!= null ?next.getField("creation_time"): "")+" length "+storedModifiers.size());
		}
		System.out.println(match.getGameTime());*/
		
		float roundedTime = (float)((long) (match.getGameTime() * 1e4) / 1e4);
		
		while(next != null && ((Float)next.getField("creation_time")).compareTo(roundedTime) < 0){
			while(nextUntimed != null && (Integer)nextUntimed.getField("serial_num") < (Integer)next.getField("serial_num")){
				executeEntry(nextUntimed);
				untimedModifiers.poll();
				nextUntimed = untimedModifiers.peek(); 
			}
			
			executeEntry(next);

			int nextSerial = (Integer)next.getField("serial_num") +1;
			while(nextUntimed != null && nextSerial == (Integer)nextUntimed.getField("serial_num")){
				executeEntry(nextUntimed);
				untimedModifiers.poll();
				nextUntimed = untimedModifiers.peek(); 
			}
			
			storedModifiers.poll();
			next = storedModifiers.peek(); 
		}
		
		next = durativeModifiers.peek(); 
		while(next != null && ((Float)((Float)next.getField("creation_time")+(Float)next.getField("duration"))).compareTo(roundedTime) < 0){
			int entityIndex = (Integer)next.getField("parent") & 0x7FF;
			//int entityHandle = (Integer)next.getField("parent");
			int index = (Integer)next.getField("index");
			
			if(modifiersByEntity.containsKey(entityIndex) && modifiersByEntity.get(entityIndex).containsKey(index)){
				modifierChanges.add(new ModifierChange(ModifierChange.Type.TIMEOUT, modifiersByEntity.get(entityIndex).get(index)));
				modifiersByEntity.get(entityIndex).remove(index);
				if(modifiersByEntity.get(entityIndex).size() == 0)
					modifiersByEntity.remove(entityIndex);
			}
			else{
				//Entity does not exist anymore
				//System.out.println("Timeout on unknown entity "+next.toString());
				//modifierChanges.add(new ModifierChange(ModifierChange.Type.TIMEOUT, next));
			}
			
			durativeModifiers.poll();
			next = durativeModifiers.peek(); 
		}
	}
	
	private void executeEntry(ModifierTableEntry entry){
		int entityIndex = (Integer)entry.getField("parent") & 0x7FF;
		//int entityHandle = (Integer)entry.getField("parent");
		int index = (Integer)entry.getField("index");
		
		if(entry.getField("entry_type").equals("DOTA_MODIFIER_ENTRY_TYPE_ACTIVE")){
			if(modifiersByEntity.containsKey(entityIndex)){
				HashMap<Integer, ModifierTableEntry> mods = modifiersByEntity.get(entityIndex);
				if(mods.containsKey(index)){

					/*StringTable modifierNames = currentMatch.getStringTables().forName("ModifierNames");
					
					ModifierTableEntry old = mods.get(index);
					System.out.println("Change Modifier "+mods.get(index)+" -> "+entry);
					System.out.println(""+modifierNames.getNameByIndex((Integer)old.getField("modifier_class"))+" -> "+modifierNames.getNameByIndex((Integer)entry.getField("modifier_class")));
					mods.put(index, entry);*/
					modifierChanges.add(new ModifierChange(ModifierChange.Type.REMOVE, mods.get(index)));
					mods.put(index, entry);
					modifierChanges.add(new ModifierChange(ModifierChange.Type.CREATE, entry));

				}
				else{
					mods.put(index, entry);
					modifierChanges.add(new ModifierChange(ModifierChange.Type.CREATE, entry));
				}
					
			}
			else{
				HashMap<Integer, ModifierTableEntry> mods = new HashMap<Integer, ModifierTableEntry>();
				mods.put(index, entry);
				modifiersByEntity.put(entityIndex, mods);
				modifierChanges.add(new ModifierChange(ModifierChange.Type.CREATE, entry));
			}

		}
		else if(entry.getField("entry_type").equals("DOTA_MODIFIER_ENTRY_TYPE_REMOVED")){
			if(modifiersByEntity.containsKey(entityIndex) && modifiersByEntity.get(entityIndex).containsKey(index)){
				modifierChanges.add(new ModifierChange(ModifierChange.Type.REMOVE, modifiersByEntity.get(entityIndex).get(index)));
				modifiersByEntity.get(entityIndex).remove(index);
				if(modifiersByEntity.get(entityIndex).size() == 0)
					modifiersByEntity.remove(entityIndex);
			}
			else{
				//System.out.println("Omitting unknown entity removal "+entry.toString());
				//modifierChanges.add(new ModifierChange(ModifierChange.Type.REMOVE, next));
			}
		}
	}
		
	
	public List<ModifierChange> modifierChanges(){
		return modifierChanges;
	}
}
