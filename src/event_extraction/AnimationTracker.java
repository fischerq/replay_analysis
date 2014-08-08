package event_extraction;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import skadistats.clarity.match.Match;
import skadistats.clarity.model.Entity;

public class AnimationTracker {
	private Map<Integer, Animation> activeAnimations = new TreeMap<Integer,Animation>();
	private List<Integer> startedAnimations = new ArrayList<Integer>();
	private List<Integer> stoppedAnimations = new ArrayList<Integer>();
	private List<Integer> cancelledAnimations = new ArrayList<Integer>();
	private List<Integer> castedAnimations = new ArrayList<Integer>();
	
	public void updateAnimations(Match match){
		startedAnimations.clear();
		stoppedAnimations.clear();
		cancelledAnimations.clear();
		castedAnimations.clear();
		for(Entity e : match.getTempEntities().tempEntities){
			if(e == null)
				continue;
			int handle = (Integer)e.getProperty("m_hEntity");
			if(e.getDtClass().getDtName().equals("DT_TEUnitAnimation")){
				if(activeAnimations.containsKey(handle))
					System.out.println("Overriding Animation for "+handle);
				activeAnimations.put(handle, new Animation(e, match));
				startedAnimations.add(handle);
			}
			else if(e.getDtClass().getDtName().equals("DT_TEUnitAnimationEnd")){
				//String time = "["+ (int)(match.getGameTime()/60)+":"+(int)(match.getGameTime()%60)+"."+(int)((match.getGameTime()*1000)%1000)+ "]";
				
				if(!activeAnimations.containsKey(handle))
				{
					//System.out.println(time +" Animation cancelling "+match.getEntities().getByHandle(handle).getDtClass().getDtName()+"("+handle+")");
					cancelledAnimations.add(handle);
				}
				else
				{
					//System.out.println(time +" Stopping "+match.getEntities().getByHandle(handle).getDtClass().getDtName()+"("+handle+") "+e.getProperty("m_bSnap"));
					stoppedAnimations.add(handle);
				}
				activeAnimations.remove(handle);
			}
		}
		//String time = "["+ (int)(match.getGameTime()/60)+":"+(int)(match.getGameTime()%60)+"."+(int)((match.getGameTime()*1000)%1000)+ "]";
		
		Iterator<Map.Entry<Integer, Animation>> it = activeAnimations.entrySet().iterator();
		System.out.println(activeAnimations.size());
		while(it.hasNext()){
			Map.Entry<Integer, Animation> anim = it.next();
			if(anim.getValue().time_cast <= match.getGameTime())
			{
				it.remove();
				//System.out.println(time +" Casting "+anim.getValue().entity.getDtClass().getDtName()+"("+anim.getKey()+")");
				castedAnimations.add(anim.getKey());
			}
		}
		System.out.println(activeAnimations.size());
	}
	
	public Animation getAnimation(Entity e){
		return activeAnimations.get(e.getHandle());
	}

	public Animation getAnimation(int handle){
		return activeAnimations.get(handle);
	}
	
	public List<Integer> getStartedAnimations(){
		return startedAnimations;
	}
	
	public List<Integer> getCastedAnimations(){
		return castedAnimations;
	}
	
	public List<Integer> getStoppedAnimations(){
		return stoppedAnimations;
	}
	
	public List<Integer> getCancelledAnimations(){
		return cancelledAnimations;
	}
}
