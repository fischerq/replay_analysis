package zera.movement_analysis;

import java.io.IOException;

import skadistats.clarity.Clarity;
import skadistats.clarity.match.Match;
import skadistats.clarity.parser.Peek;
import skadistats.clarity.parser.PeekIterator;
import skadistats.clarity.parser.Profile;


public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		System.out.println("Hello world");
	    String file = "data/replays/771088857.dem";
	    
	    MapWindow window = new MapWindow();
	    
		// Match is a container for all the data clarity provides.
        Match match = new Match();
        // set up an iterator for reading all packets from the file
        PeekIterator iter;
		try {
			iter = Clarity.peekIteratorForFile(file, Profile.ALL);
		
	        while (iter.hasNext()) {
	            // read the next Peek from the iterator
	            Peek p = iter.next();
	            // and apply it to the match, changing it's state
	            p.apply(match);
	            // now, it's your turn to do something with match here.
	        }
	        System.out.println("Finished parsing");
		} catch (IOException e) {
			System.out.println("opening file failed");
		}
		
		window.map.save("out.png");
		window.dispose();
	}

}
