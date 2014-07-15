package movement_graph;

import java.awt.Color;

public class ColorScale {
	private double min;
	private double range;
	
	private int[] color_min = {255, 255, 255};
	private int[] color_max = {255, 0, 0};
	private int[] color_diff;
	
	public ColorScale(){
		min = 0;
		range = 1;
		setupColors();
	}
	
	public ColorScale(double min, double max){
		this.min = min;
		range = max-min;
		setupColors();
	}
	
	private void setupColors(){
		color_diff =  new int[3];
		color_diff[0] = color_max[0] - color_min[0];
		color_diff[1] = color_max[1] - color_min[1];
		color_diff[2] = color_max[2] - color_min[2];
	}
	
	public Color map(double value){
		if(value < min || value > min+range){
			return new Color(0,0,0);
		}
		double factor = (value-min)/range;
		
		return new Color(
						(int)(color_min[0]+factor*color_diff[0]),
						(int)(color_min[1]+factor*color_diff[1]),
						(int)(color_min[2]+factor*color_diff[2]));
		
	}
}
