package graph_evaluation;

import java.util.Iterator;

import movement_graph.Position;
import utils.Timed;

public class Evaluation {
	private double time_start = -1;
	private double time_end = -1;
	
	public double eval(Position p){
		return 1;
	}
	
	protected double scale(double in, double max){
		if(in > max)
			return 0;
		else 
			return in/max;
	}
	
	protected void setTimeFilter(double start, double end){
		time_start = start;
		time_end = end;
	}
	
	protected Timed getFilteredNext(Iterator<Timed> it){
		while(it.hasNext())
		{
			Timed next = it.next();
			if(next.getTime() >= time_start && next.getTime() < time_end)
				return next;
		}
		return null;
	}
	
}
