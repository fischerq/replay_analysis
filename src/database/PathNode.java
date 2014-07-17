package database;

// -----[time ... time+duration ]--------------------[time_next ...]
//		 <Stay, all at position>     <Movement> 
public class PathNode{
	public double time; //Time start
	public double[] position = new double[2]; 
	public double duration; //Duration of stay
}