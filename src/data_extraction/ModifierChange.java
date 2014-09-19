package data_extraction;

import skadistats.clarity.model.ModifierTableEntry;

public class ModifierChange {
	public Type type;
	public ModifierTableEntry entry;
	public static enum Type{
		CREATE,
		TIMEOUT,
		REMOVE,
	};
	
	public ModifierChange(Type type,  ModifierTableEntry entry){
		this.type = type;
		this.entry = entry;
	}
	
	public String toString(){
		return type+" "+entry.toString();
	}
}
