package data_extraction;

import skadistats.clarity.match.Match;
import skadistats.clarity.model.Entity;
import utils.ConstantMapper;
import utils.Utils;

public class Animation {
	public Entity entity;
	public int type;
	public int sequence_index;
	public int activity;
	public double time_cast;
	public double cast_point;
	
	public int eventID;
	public TrackedUnit target;
	
	public Animation(Entity temp_ent, Match match){
		if(temp_ent.getProperty("m_iType") == null)
			type = 0;
		else
			type = temp_ent.getProperty("m_iType");
		if(temp_ent.getProperty("m_iSequenceIndex") == null)
			sequence_index = 0;
		else
			sequence_index = temp_ent.getProperty("m_iSequenceIndex");
		if(temp_ent.getProperty("m_Activity") == null)
			activity = 0;
		else
			activity = temp_ent.getProperty("m_Activity");
		
		entity = match.getEntities().getByHandle((Integer)temp_ent.getProperty("m_hEntity"));
		if(temp_ent.getProperty("m_flCastPoint") == null){
			time_cast = match.getGameTime();
			cast_point = 0;
		}
		else{
			time_cast = match.getGameTime() + (Float)temp_ent.getProperty("m_flCastPoint") - ConstantMapper.replay_tick/4;
			cast_point = (Float)temp_ent.getProperty("m_flCastPoint");
		}
		
		eventID = -1;
		target = null;
    }
	
	public String toString(){
		return "Animation: "+entity.getDtClass().getDtName()+"("+entity.getHandle()+") "+ConstantMapper.animationAction(activity)+" "+type+" "+sequence_index+" "+cast_point+ " "+ConstantMapper.formatTime(Utils.computeTime(time_cast));
	}
}
