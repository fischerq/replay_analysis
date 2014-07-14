package utils;


import javax.vecmath.Vector2f;

import skadistats.clarity.model.Entity;


public class Utils {

	static int MAX_COORD_INTEGER = 16384;
	
	public static float[] getPosition(Entity e){
		int cell_x = e.getProperty("m_cellX");
		int cell_y = e.getProperty("m_cellY");
		//System.out.println(e.toString());
	    Vector2f offset = e.getProperty("m_vecOrigin");
	    int cellbits = e.getProperty("m_cellbits");
	    		
	    int cellwidth = 1 << cellbits;
	    float[] pos = new float[2];
	    pos[0] = ((cell_x * cellwidth) - MAX_COORD_INTEGER) + offset.x;
	    pos[1] = ((cell_y * cellwidth) - MAX_COORD_INTEGER) + offset.y;
	    return pos;
	}
}
