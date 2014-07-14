package movement_graph;

import java.util.LinkedList;
import java.util.List;

public class Position {
	public double[] position;
	public float y;
	
	public List<Move> moves = new LinkedList<Move>();
	public List<Stay> stays = new LinkedList<Stay>();
	
	public Position(double[] position_){
		position = position_.clone();
	}
}
