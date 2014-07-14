package replay_analysis;

import java.io.IOException;


import path_recognition.PathRecognition;

import skadistats.clarity.Clarity;
import skadistats.clarity.match.Match;
import skadistats.clarity.parser.Peek;
import skadistats.clarity.parser.PeekIterator;
import skadistats.clarity.parser.Profile;
import utils.DisplayWindow;


public class Main {

	
	public static void main(String[] args) {
	    String file_name = "data/replays/771088857.dem";
	    
	    DisplayWindow window = new DisplayWindow();
	    window.open(file_name);
	    
        Match match = new Match();        
        PathRecognition paths = new PathRecognition();
        
        window.display.setText("Processing");
        window.display.reset();
        window.repaint();
        
        PeekIterator iter;
		try {
			iter = Clarity.peekIteratorForFile(file_name, Profile.ENTITIES);
		
	        while (iter.hasNext()) {
	            Peek p = iter.next();
	            System.out.println(match.getGameTime());
	        	float lastTime = match.getGameTime();
	            p.apply(match);
	            if(lastTime != match.getGameTime()){
	            	
	            	paths.analyseTick(match);
	            }
	        }
	        paths.postProcess();
	        window.addPathResults(paths.getResults());
	        System.out.println("Finished parsing");
		} catch (IOException e) {
			System.out.println("opening file failed");
		}
	}

	
	public static void parseTick(Match m)
	{
		
	}
}
