package replay_analysis;

import java.io.File;
import java.io.IOException;
import java.util.List;

import data_extraction.Extraction;
import database.Database;

import skadistats.clarity.Clarity;
import skadistats.clarity.match.Match;
import skadistats.clarity.parser.Profile;
import skadistats.clarity.parser.Tick;
import skadistats.clarity.parser.TickIterator;
import utils.Replay;
import utils.Utils;


public class DataExtraction {
	private static boolean rebuild_db = true;
	
	public static void main(String[] args) {
	    String database_file = "data/analysis_mine.sqlite";
	    
	    List<Replay> replays = Utils.findReplays(new File("data/replays/meine"));
	    
	    Database db = new Database(database_file, true);
	    
        Match match = new Match();        
        Match match_old = new Match();        

        Extraction extraction;

        
        for(Replay replay : replays){
        	extraction = new Extraction(replay.id, db);
        	if(db.replayExists(replay.id) && !rebuild_db){
        		System.out.println("Skipping "+replay.id+" (exists)");
        		continue;	
        	}
        	else if(db.replayExists(replay.id)){
        		db.deleteReplay(replay.id);
        	}
        	
        	db.createReplay(replay.id);
        	
        	try {
				TickIterator iterMetaData = Clarity.tickIteratorForFile(replay.filename, Profile.ENTITIES);
				Match metaMatch = new Match();
				boolean found = false;
	        	while(!found){
	        		Tick t = iterMetaData.next();
	        		t.apply(metaMatch);
	        		if((double)(Float)metaMatch.getGameRulesProxy().getProperty("dota_gamerules_data.m_flGameStartTime") != 0.0){
	        			Utils.setStartTime((Float)metaMatch.getGameRulesProxy().getProperty("dota_gamerules_data.m_flGameStartTime"));
	        			found = true;
	        		}
	        	}
	        	
        	} catch (IOException e1) {
				e1.printStackTrace();
			}
        	
        	
            TickIterator iter;
            TickIterator iter_old;
    		try {
    			iter = Clarity.tickIteratorForFile(replay.filename, Profile.ALL);//Profile.ENTITIES, Profile.USERMESSAGE_CONTAINER, Profile.COMBAT_LOG, Profile.TEMP_ENTITIES);
    			iter_old = Clarity.tickIteratorForFile(replay.filename, Profile.ALL);//Profile.ENTITIES, Profile.USERMESSAGE_CONTAINER, Profile.COMBAT_LOG, Profile.TEMP_ENTITIES);
    	        while (iter.hasNext()) {
    	            Tick p = iter.next();
    	            p.apply(match);

	            	extraction.analyseTick(match, match_old);
    	            
    	            Tick p2 = iter_old.next();
    	            p2.apply(match_old);
    	        }
    	        extraction.finish();

    	        System.out.println("Finished parsing "+replay.id);
    		} catch (IOException e) {
    			System.out.println("opening replay failed");
    		}
        }
        db.close();
	}
}
