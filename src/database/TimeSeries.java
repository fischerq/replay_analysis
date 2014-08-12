package database;

import java.util.List;

public class TimeSeries {
	public String type;
	public List<TimeSeriesNode> nodes;
	
	public TimeSeries(String t, List<TimeSeriesNode> n){
		type = t;
		nodes = n;
	}
	
	public double[][] getData(){
		double[][] data = new double[nodes.size()][2];
		int i = 0;
		for(TimeSeriesNode node : nodes){
			data[i][0] = node.time;
			data[i][1] = node.value;
			i++;
		}
		return data;
	}
}
