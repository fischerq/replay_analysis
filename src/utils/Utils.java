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
	
	public static double computeTime(double time){
		return time - gameStartTime;
	}
	
	public static int getHealth(Entity e){
		if(e.getDtClass().getPropertyIndex("m_iHealthPercentage") != null){
			double healthPercent = (Integer)e.getProperty("m_iMaxHealth")/127.0;
			return (int) Math.floor((Integer)e.getProperty("m_iHealthPercentage")*healthPercent);
		}
		else
			return (Integer)e.getProperty("m_iHealth");
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
	
	public static Vector2f getDirection(Entity entity){
		if(entity.getDtClass().getPropertyIndex("m_angRotation[1]") == null)
			return null;
		float angle = (Float)entity.getProperty("m_angRotation[1]");
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
	
	public static boolean isPlayerControlled(Entity e){
		return (Integer)e.getProperty("m_hOwnerEntity") != 2097151;
	}
	
	public static boolean isDenieable(Entity e){
		int unitNameIndex = e.getProperty("m_iUnitNameIndex");
		if(unitNameIndex <= 115 ){//Heroes
			return true;
		}
		else if(unitNameIndex >= 124 && unitNameIndex <= 157){//Buildings
			if(getHealth(e) < (int)e.getProperty("m_iMaxHealth")/10)
				return true;
			else
				return false;
		}
		else if(unitNameIndex >= 116 && unitNameIndex <= 123 || unitNameIndex >= 160 && unitNameIndex <= 163){
			if(getHealth(e) < (int)e.getProperty("m_iMaxHealth")/2)
				return true;
			else
				return false;
		}
		return true;
	}
	
	public static int getAttackRange(Entity e){
		switch(ConstantMapper.nameForIndex((int) e.getProperty("m_iUnitNameIndex"))){
		case "Sand King":
		case "Magnus":
		case "Kunkka":
		case "Bounty Hunter":
		case "Alchemist":
		case "Faceless Void":
			return 128;
			
		case "Mirana":
			return 600;
		case  "Shadow Fiend":
			return 500;

		case "Vengeful Spirit": 
			return 400;
		case "Windranger": 
			return 600;
		case "Zeus":
			return 350;


		case "Viper":
			return 575;
		case "Dragon Knight":
			return 500;
		case "Nature's Prophet":
			return 600;

		case "Batrider":
			return 375;
		case "Chen":
			return 600;

		case "Outworld Devourer":
			return 450;
		case "Rubick":
			return 600;

		
		case "Dire Ranged Creep":
		case "Dire Mega Ranged Creep":
		case "Radiant Ranged Creep":
		case "Radiant Mega Ranged Creep":
			return 500;
		case "Dire Melee Creep":
		case "Dire Mega Melee Creep":
		case "Radiant Melee Creep":
		case "Radiant Mega Melee Creep":
			return 100;
		case "Radiant Tower T1 Top":
		case "Radiant Tower T1 Middle":
		case "Radiant Tower T1 Bottom":
		case "Radiant Tower T2 Top":
		case "Radiant Tower T2 Middle":
		case "Radiant Tower T2 Bottom":
		case "Radiant Tower T3 Top":
		case "Radiant Tower T3 Middle":
		case "Radiant Tower T3 Bottom":
		case "Radiant Tower T4":
		case "Dire Tower T1 Top":
		case "Dire Tower T1 Middle":
		case "Dire Tower T1 Bottom":
		case "Dire Tower T2 Top":
		case "Dire Tower T2 Middle":
		case "Dire Tower T2 Bottom":
		case "Dire Tower T3 Top":
		case "Dire Tower T3 Middle":
		case "Dire Tower T3 Bottom":
		case "Dire Tower T4":
			return 700;

		case "Radiant Siege Creep":
		case "Radiant Mega Siege Creep":
		case "Dire Siege Creep":
		case "Dire Mega Siege Creep":
			return 690;
		case "Kobold":
		case "Kobold Tunneler":
		case "Kobold Foreman":
		case "Fell Spirit":
		case "Mud Golem":
		case "Ogre Bruiser":
		case "Ogre Frostmage":
		case "Centaur Outrunner":
		case "Centaur Khan":
		case "Hellbear":
		case "Hellbear Smasher":
		case "Satyr Mindstealer":
		case "Satyr Tormenter":
			return 100;


		case "Giant Wolf":
		case "Alpha Wolf":
			return 90;
		case "Wildwing":
		case "Wildwing Ripper":
			return 128;

		case "Satyr Banisher":
			return 600;
		case "Rock Golem":
			return 100;
		case "Granite Golem":
			return 128;
		case "Thunderhide":
		case "Rumblehide":
			return 300;
		case "Vhoul Assassin":
			return 500;
		case "Ghost":
			return 300;
		case "Dark Troll":
		case "Dark Troll Summoner":
			return 500;

		case "Hill Troll Berserker":
			return 500;
		case "Hill Troll Priest":
			return 600;
		case "Harpy Scout":
			return 300;
		case "Harpy Stormcrafter":
			return 450;
		case "Black Drake":
		case "Black Dragon":
			return 300;
		case "Necronomicon Warrior Lvl 1":
		case "Necronomicon Warrior Lvl 2":
		case "Necronomicon Warrior Lvl 3":
			return 100;
		case "Necronomicon Archer Lvl 1":
			return 350;
		case "Necronomicon Archer Lvl 2":
			return 450;
		case "Necronomicon Archer Lvl 3":
			return 550;


		case "Treant":
			return 100;
		default:
			System.out.println("Unaccounted attack range for unit: "+ConstantMapper.nameForIndex((int) e.getProperty("m_iUnitNameIndex")));
			return 1000;
		}
	}
}
