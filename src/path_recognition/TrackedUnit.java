package path_recognition;

import skadistats.clarity.match.Match;
import skadistats.clarity.model.Entity;
import utils.Utils;

public class TrackedUnit {
	private int handle;
	private Path current_path;
	private boolean tracking;
	
	public TrackedUnit(int handle){
		this.handle = handle;
		tracking = false;
	}
	
	public Path update(Match match){
		Path result = null;
		Entity entity = match.getEntities().getByHandle(handle);
		int alive = entity.getProperty("m_lifeState");
		float[] position = Utils.getPosition(entity);
		if(alive == 0){
			if(!tracking){
				tracking = true;
				current_path = new Path();
			}
			
			current_path.add(match.getGameTime(), position);
		}
		else{
			if(tracking)
			{
				current_path.add(match.getGameTime(), position);
				System.out.println("finished_path");
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
