package path_recognition;

import java.awt.Color;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import skadistats.clarity.match.Match;
import skadistats.clarity.model.Entity;

public class PathRecognition {
	private List<TrackedUnit> units;
	private Set<Integer> tracked_players;
	private List<Path> recorded_paths;
	
	public PathRecognition(){
		units = new LinkedList<TrackedUnit>();
		tracked_players = new TreeSet<Integer>();
		recorded_paths = new LinkedList<Path>();
	}
	
	public void analyseTick(Match m){
		//System.out.println(m.getGameTime());
		
		updateHeroesList(m);
		
		Iterator<TrackedUnit> it = units.iterator();
		while(it.hasNext()){
			TrackedUnit unit = it.next();
			Path p = unit.update(m);
			if(p != null)
			{
				System.out.println("finished path");
				recorded_paths.add(p);
			}
		}
	}
	
	public void updateHeroesList(Match m){
		if(units.size() == 10)
			return;
		
		Entity player_resource;
		if(m.getEntities().getAllByDtName("DT_DOTA_PlayerResource").hasNext())
			player_resource	= m.getEntities().getAllByDtName("DT_DOTA_PlayerResource").next();
		else 
			return;
		Integer[] selected_heroes = player_resource.getArrayProperty(Integer.class, "m_nSelectedHeroID");
		Integer[] hero_handles = player_resource.getArrayProperty(Integer.class, "m_hSelectedHero");
		for(int i = 0; i<selected_heroes.length; ++i){
			if(tracked_players.contains(i))
				continue;
			else if(selected_heroes[i] != -1)
			{
				units.add(new TrackedUnit(hero_handles[i]));
				tracked_players.add(i);
				System.out.println("got"+i+": "+hero_handles[i]);
			}
		}
	}
	
	public void postProcess(){
		Iterator<TrackedUnit> it_u = units.iterator();
		while(it_u.hasNext()){
			TrackedUnit unit = it_u.next();
			Path p = unit.finish();
			if(p != null)
				recorded_paths.add(p);
		}
		
		Iterator<Path> it = recorded_paths.iterator();
		int i = 1;
		while(it.hasNext()){
			Path p = it.next();
			p.process();
			System.out.println("processing path "+i+"/"+recorded_paths.size());
			i++;
			if(i > 9)
				break;
		}
	}
	
	public List<Path> getResults(){
		return recorded_paths;
	}
}
