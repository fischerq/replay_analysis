package path_recognition;

import skadistats.clarity.match.Match;
import skadistats.clarity.model.Entity;
import utils.Utils;

public class TrackedUnit {
	private int handle;
	private int unit_id;
	private String name;
	private Path current_path;
	private boolean tracking;
	
	public TrackedUnit(int handle, int id, String name){
		this.handle = handle;
		unit_id = id;
		this.name = name;
		tracking = false;
	}
	
	public Path update(Match match){
		Path result = null;
		Entity entity = match.getEntities().getByHandle(handle);
		int alive = entity.getProperty("m_lifeState");
		double[] position = Utils.getPosition(entity);
		if(alive == 0){
			if(!tracking){
				tracking = true;
				current_path = new Path(unit_id, name);
			}
			
			current_path.add(match.getGameTime(), position);
		}
		else{
			if(tracking)
			{
				current_path.add(match.getGameTime(), position);
				result = current_path;
				tracking = false;
				current_path = null;
			}
		}
		return result;
	}
	
	public Path finish(){
		Path result = current_path;
		current_path = null;
		return result;
	}
}
