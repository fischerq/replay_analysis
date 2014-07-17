package movement_graph;

import utils.Timed;

public class Stay implements Timed{
	public double time; //Midpoint of stay interval
	public double duration; //total length of interval
	
	public Stay(double t, double r){
		time = t;
		duration = r;
	}
	
	@Override
	public double getTime(){
		return time;
	}
}
