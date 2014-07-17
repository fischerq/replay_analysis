package movement_graph;

import utils.Timed;

public class Move implements Timed{
	public Position start;
	public Position end;
	public double time;
	public double duration;
	
	public Move next = null;
	public Move last = null;
	
	public double distance(){
		return Math.sqrt((start.position[0]-end.position[0]) *(start.position[1]-end.position[1]) + (start.position[1]-end.position[1])*(start.position[1]-end.position[1]));
	}
	
	public double speed(){
		return distance()/duration;
	}

	@Override
	public double getTime() {
		return time;
	}
}
