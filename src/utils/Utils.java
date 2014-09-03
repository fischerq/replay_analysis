package utils;


import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.vecmath.Vector2f;

import data_extraction.Animation;


import skadistats.clarity.match.Match;
import skadistats.clarity.model.Entity;


public class Utils {

	private static int MAX_COORD_INTEGER = 16384;
	private static double gameStartTime = 0;
	
	public static boolean isAlive(Entity e){
		int lifeState = e.getProperty("m_iLifeState");
		return lifeState == 0; 
	}
	
	public static boolean isIllusion(Entity e){
		return e.getDtClass().getPropertyIndex("m_hReplicatingOtherHeroModel") != null && (Integer)e.getProperty("m_hReplicatingOtherHeroModel") != 2097151;
	}
	
	public static boolean isAttack(Animation a){
		return a.activity == 424 || a.activity == 425 || a.activity == 426;
	}
	
	public static void setStartTime(double time){
		gameStartTime = time;
	}
	public static double getTime(Match m){
		return m.getGameTime() - gameStartTime;
	}
	
	public static double computeTime(float time){
		return time - gameStartTime;
	}
	
	public static Vector2f getPosition(Entity e){
		int cell_x = e.getProperty("m_cellX");
		int cell_y = e.getProperty("m_cellY");
		//System.out.println(e.toString());
	    Vector2f offset = e.getProperty("m_vecOrigin");
	    int cellbits = e.getProperty("m_cellbits");
	    		
	    int cellwidth = 1 << cellbits;
	    Vector2f pos = new Vector2f();
	    pos.x = (float)(((cell_x * cellwidth) - MAX_COORD_INTEGER) + (double)offset.x);
	    pos.y = (float) (((cell_y * cellwidth) - MAX_COORD_INTEGER) + (double)offset.y);
	    return pos;
	}
	
	public static Vector2f getDirection(double angle){
		double radian = angle /360.0 *2*Math.PI;
		Vector2f vec = new Vector2f();
		vec.x = (float) Math.cos(radian);
		vec.y = (float) Math.sin(radian);
		return vec;
	}
	public static List<Replay> findReplays(File root)
	{
		List<Replay> results = new LinkedList<Replay>();
		
	    File[] files = root.listFiles(); 
	    if(files == null)
	    	return results;
	    
	    for (File file : files) {
	        if (file.isFile()) {
	            if(file.getName().endsWith(".dem"))
	            {
	            	Replay r = new Replay();
	            	r.id = Integer.parseInt(file.getName().substring(0,file.getName().length()-4));
	            	r.filename = file.getAbsolutePath();
	            	results.add(r);
	            }
	        } else if (file.isDirectory()) {
	            results.addAll(findReplays(file));
	        }
	    }
	    
	    return results;	    
	}
	
	public static List<String> findFiles(File root, String extension)
	{
		List<String> results = new LinkedList<String>();
		
	    File[] files = root.listFiles(); 
	    if(files == null)
	    	return results;
	    
	    for (File file : files) {
	        if (file.isFile()) {
	            if(file.getName().endsWith(extension))
	            {
	            	results.add(file.getAbsolutePath());
	            }
	        } else if (file.isDirectory()) {
	            results.addAll(findFiles(file, extension));
	        }
	    }
	    
	    return results;	    
	}
}
