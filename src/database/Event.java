package database;

import java.util.List;

public class Event {
	public double time;
	public String type;
	public List<EventArgumentInterface> arguments;
	
	public Event(double time, String type, List<EventArgumentInterface> arguments){
		this.time = time;
		this.type = type;
		this.arguments = arguments;
	}
}
