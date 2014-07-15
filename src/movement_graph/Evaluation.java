package movement_graph;

public class Evaluation {
	public double eval(Position p){
		return 1;
	}
	
	protected double scale(double in, double max){
		if(in > max)
			return 0;
		else 
			return in/max;
	}
	
}
