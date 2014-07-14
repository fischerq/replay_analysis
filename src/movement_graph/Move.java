package movement_graph;

public class Move {
	public Position start;
	public Position end;
	public double time;
	public double duration;
	
	public double speed(){
		return ((start.position[0]-end.position[0]) *(start.position[1]-end.position[1]) + (start.y-end.y)*(start.y-end.y))/duration;
	}
}
