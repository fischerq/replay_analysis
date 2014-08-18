package data_extraction;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Globals {
	public static Map<Integer, TreeMap<String, Integer>> found_unit_names = new TreeMap<Integer, TreeMap<String, Integer>>();
	public static Map<Integer, Integer> foundInts = new TreeMap<Integer, Integer>();
	public static Map<String, Integer> foundStrings = new TreeMap<String, Integer>();
	public static int counter = 0;
	
	public static void countString(String string){
		if(foundStrings.containsKey(string))
			foundStrings.put(string, foundStrings.get(string)+1);
		else
			foundStrings.put(string, 1);
	}
 	public static void countInt(int percent){
		if(foundInts.containsKey(percent))
			foundInts.put(percent, foundInts.get(percent)+1);
		else
			foundInts.put(percent, 1);
	}
	
 	public static void count(){
 		counter++;
 	}
 	
 	public static void printCounter(){
 		System.out.println("Counter: "+counter);
 	}
 	
 	
	public static void printCountedInts(){
		for(Map.Entry<Integer, Integer> e : foundInts.entrySet()){
			System.out.println(e.getKey()+": "+e.getValue());
		}
	}
	
	public static void printCountedStrings(){
		for(Map.Entry<String, Integer> e : foundStrings.entrySet()){
			System.out.println(e.getKey()+": "+e.getValue());
		}
	}
	
	public static boolean add(int index, String name){
		if(found_unit_names.containsKey(index)){
			TreeMap<String, Integer> map = found_unit_names.get(index);
			if(map.containsKey(name))
				map.put(name, map.get(name)+1);
			else{
				map.put(name, 1);
				return map.size() == 1;
			}
		}
		else{
			TreeMap<String, Integer> map = new TreeMap<String, Integer>();
			map.put(name, 1);
			found_unit_names.put(index, map);
		}
		return true;
	}
	
	public static void print_names(){
		for(Map.Entry<Integer,TreeMap<String, Integer>> entry : found_unit_names.entrySet()){
			System.out.print(entry.getKey()+": [");
			for(Map.Entry<String, Integer> e : entry.getValue().entrySet()){
				System.out.print("<"+e.getKey()+", "+e.getValue()+">");
			}
			System.out.print("]\n");
		}
	}
}
