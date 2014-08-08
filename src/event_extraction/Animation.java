package event_extraction;

import skadistats.clarity.match.Match;
import skadistats.clarity.model.Entity;

public class Animation {
	public Entity entity;
	public int type;
	public int sequence_index;
	public int activity;
	public double time_cast;
	
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
		if(temp_ent.getProperty("m_flCastPoint") == null)
			time_cast= match.getGameTime();
		else
			time_cast = match.getGameTime() + (Float)temp_ent.getProperty("m_flCastPoint");
		
    //	String time = "["+ (int)(match.getGameTime()/60)+":"+(int)(match.getGameTime()%60)+"."+(int)((match.getGameTime()*1000)%1000)+ "]";

	//	System.out.println(time+" Created Animation: "+entity.getDtClass().getDtName()+"("+(Integer)temp_ent.getProperty("m_hEntity")+") "+activity+" "+type+" "+sequence_index+" "+(Float)temp_ent.getProperty("m_flCastPoint"));
	}
}
