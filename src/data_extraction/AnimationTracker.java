package data_extraction;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import skadistats.clarity.match.Match;
import skadistats.clarity.model.Entity;
import utils.ConstantMapper;
import utils.Utils;

public class AnimationTracker {
	private Map<Integer, Animation> currentAnimations = new TreeMap<Integer,Animation>();
	private Map<Integer, Animation> castedActiveAnimations = new TreeMap<Integer,Animation>();
	private List<Animation> startedAnimations = new ArrayList<Animation>();
	private List<Animation> stoppedAnimations = new ArrayList<Animation>();
	private List<Animation> cancelledAnimations = new ArrayList<Animation>();
	private List<Animation> castedAnimations = new ArrayList<Animation>();
	
	public void updateAnimations(Match match){
		startedAnimations.clear();
		stoppedAnimations.clear();
		cancelledAnimations.clear();
		castedAnimations.clear();
		for(Entity e : match.getTempEntities().getAll()){
			if(e == null || !e.getDtClass().getDtName().equals("DT_TEUnitAnimation")&&!e.getDtClass().getDtName().equals("DT_TEUnitAnimationEnd") )
				continue;
			int handle = (Integer)e.getProperty("m_hEntity");
			if(e.getDtClass().getDtName().equals("DT_TEUnitAnimation")){
				Animation new_anim = new Animation(e, match);
				currentAnimations.put(handle, new_anim);
				startedAnimations.add(new_anim);
			}
			else if(e.getDtClass().getDtName().equals("DT_TEUnitAnimationEnd")){
				//if(e.getProperty("m_bSnap")!=null &&(boolean)e.getProperty("m_bSnap"))
				//	System.out.println("Snap: "+handle+" "+getAnimation(handle)+" "+castedActiveAnimations.get(handle));
				if(castedActiveAnimations.containsKey(handle))
				{
					//System.out.println(time +" Animation cancelling "+match.getEntities().getByHandle(handle).getDtClass().getDtName()+"("+handle+")");
					cancelledAnimations.add(castedActiveAnimations.get(handle));
					castedActiveAnimations.remove(handle);
				}
				else
				{
					//System.out.println(time +" Stopping "+match.getEntities().getByHandle(handle).getDtClass().getDtName()+"("+handle+") "+e.getProperty("m_bSnap"));
					stoppedAnimations.add(getAnimation(handle));
					currentAnimations.remove(handle);
				}
			}
		}
		//String time = "["+ (int)(match.getGameTime()/60)+":"+(int)(match.getGameTime()%60)+"."+(int)((match.getGameTime()*1000)%1000)+ "]";
		
		Iterator<Map.Entry<Integer, Animation>> it = currentAnimations.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<Integer, Animation> anim = it.next();
			if(anim.getValue().time_cast <= match.getGameTime())
			{
				//System.out.println(time +" Casting "+anim.getValue().entity.getDtClass().getDtName()+"("+anim.getKey()+")");
				castedAnimations.add(anim.getValue());
				castedActiveAnimations.put(anim.getKey(), anim.getValue());
				it.remove();
			}
		}
	}
	
	public Animation getAnimation(Entity e){
		return currentAnimations.get(e.getHandle());
	}

	public Animation getAnimation(int handle){
		return currentAnimations.get(handle);
	}
	
	public List<Animation> getStartedAnimations(){
		return startedAnimations;
	}
	
	public List<Animation> getCastedAnimations(){
		return castedAnimations;
	}
	
	public List<Animation> getStoppedAnimations(){
		return stoppedAnimations;
	}
	
	public List<Animation> getCancelledAnimations(){
		return cancelledAnimations;
	}
}
