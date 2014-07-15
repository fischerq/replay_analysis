package replay_analysis;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;


import path_recognition.PathRecognition;

import skadistats.clarity.Clarity;
import skadistats.clarity.match.Match;
import skadistats.clarity.parser.Peek;
import skadistats.clarity.parser.PeekIterator;
import skadistats.clarity.parser.Profile;
import utils.Database;
import utils.DisplayWindow;
import utils.Replay;
import utils.Utils;


public class PathExtraction {

	
	public static void main(String[] args) {
	    String database_file = "data/replay_analysis.sqlite";
	    boolean rebuild_db = false;
	    
	    List<Replay> replays = Utils.findReplays(new File("data/replays"));
	    
	    DisplayWindow window = new DisplayWindow();
	    window.open("Path extraction");
	    Database db = new Database(database_file);
        db.open();
	    
        Match match = new Match();        
        PathRecognition paths;
        
        for(Replay replay : replays){
        	paths = new PathRecognition();
        	if(db.replayExists(replay) && !rebuild_db){
        		System.out.println("Skipping "+replay.id+" (exists)");
        		continue;	
        	}
        	
        	window.display.setText("Processing");
            window.display.reset();
            window.repaint();
            
            PeekIterator iter;
    		try {
    			iter = Clarity.peekIteratorForFile(replay.filename, Profile.ENTITIES);
    		
    	        while (iter.hasNext()) {
    	            Peek p = iter.next();
    	            //System.out.println(match.getGameTime());
    	        	float lastTime = match.getGameTime();
    	            p.apply(match);
    	            if(lastTime != match.getGameTime()){
    	            	
    	            	paths.analyseTick(match);
    	            }
    	        }
    	        paths.finish();
    	        
    	        db.storePaths(replay, paths.getResults());

    	        window.addPathResults(paths.getResults());
    	        System.out.println("Finished parsing");
    		} catch (IOException e) {
    			System.out.println("opening replay failed");
    		}
        }
        db.close();
	}
}
