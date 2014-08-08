package event_extraction;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Globals {
	public static Map<Integer, TreeMap<String, Integer>> found_unit_names = new HashMap<Integer, TreeMap<String, Integer>>();
	public static Map<Integer, Integer> found_percents = new HashMap<Integer, Integer>();
	public static void add_percent(int percent){
		if(found_percents.containsKey(percent))
			found_percents.put(percent, found_percents.get(percent)+1);
		else
			found_percents.put(percent, 1);
	}
	
	public static void print_percent(){
		for(Map.Entry<Integer, Integer> e : found_percents.entrySet()){
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
