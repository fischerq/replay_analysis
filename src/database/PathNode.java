package database;

// -----[time]-------------------------------------------------[time_next]--- ...... ---[last_time]
//              <linear movement of time, position, rotation> 							<data at death tick>
public class PathNode{
	public double time; //Time start
	public double[] position = new double[2]; 
	public double duration; //Duration of stay
}