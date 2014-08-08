package replay_analysis;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import database.Database;
import event_extraction.EventRecognition;
import path_recognition.PathRecognition;

import skadistats.clarity.Clarity;
import skadistats.clarity.match.Match;
import skadistats.clarity.model.GameEventDescriptor;
import skadistats.clarity.parser.Peek;
import skadistats.clarity.parser.PeekIterator;
import skadistats.clarity.parser.Profile;
import utils.DisplayWindow;
import utils.Replay;
import utils.Utils;


public class EventExtraction {
	private static boolean rebuild_db = false;
	
	public static void main(String[] args) {
	    String database_file = "data/analysis_mine.sqlite";
	    
	    List<Replay> replays = Utils.findReplays(new File("data/replays/meine"));
	    
	    DisplayWindow window = new DisplayWindow();
	    //window.open("Event extraction");
	    Database db = new Database(database_file);
        db.open();
	    
        Match match = new Match();        
        Match match_old = new Match();        

        EventRecognition events;

        
        for(Replay replay : replays){
        	events = new EventRecognition();
            events.setDatabase(db);
        	if(db.replayExists(replay) && !rebuild_db){
        		System.out.println("Skipping "+replay.id+" (exists)");
        		continue;	
        	}
        	
        	window.display.setText("Processing");
            window.display.reset();
            window.repaint();
            
            PeekIterator iter;
            PeekIterator iter_old;
    		try {
    			iter = Clarity.peekIteratorForFile(replay.filename, Profile.ENTITIES, Profile.USERMESSAGE_CONTAINER, Profile.COMBAT_LOG);
    			iter_old = Clarity.peekIteratorForFile(replay.filename, Profile.ENTITIES, Profile.USERMESSAGE_CONTAINER, Profile.COMBAT_LOG);
    	        while (iter.hasNext()) {
    	            Peek p = iter.next();
    	            //System.out.println(match.getGameTime());
    	        	float lastTime = match.getGameTime();
    	            p.apply(match);

    	            if(lastTime != match.getGameTime()){
    	            	
    	            	events.analyseTick(match, match_old);
    	            }
    	            
    	            Peek p2 = iter_old.next();
    	            p2.apply(match_old);
    	        }
    	        events.finish();

    	        System.out.println("Finished parsing "+replay.id);
    		} catch (IOException e) {
    			System.out.println("opening replay failed");
    		}
        }
        db.close();
	}
}
