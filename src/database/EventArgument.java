package database;

public class EventArgument<T> implements EventArgumentInterface{
	private String name;
	private T value;

	public EventArgument(String name, T value){
		this.name = name;
		this.value = value;
	}
	
	public String getName() {
		return name;
	}
	
	public Object getValue() {
		return value;
	}
	
	public String toString(){
		return name+": "+value.toString();
	}
}
