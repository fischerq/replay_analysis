package replay_analysis;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import database.Database;
import database.Path;



import movement_graph.Duration;
import movement_graph.Graph;
import movement_graph.Visits;
import utils.ColorScale;
import utils.DisplayWindow;
import utils.Utils;
import utils.Statistics;

public class MovementGraph {

	public static void main(String[] args) {
	    String database_file = "data/replay_analysis.sqlite";
	    Database db = new Database(database_file);
	    db.open();
	    
	    Graph move_graph = new Graph();
	    move_graph.initialize();
	    System.out.println("Initialized");
	    List<Path> paths = db.loadPaths();
	    System.out.println("loaded paths");
	    
	    for(Path path : paths){
	    	move_graph.addPath(path);
	    }
	    
	    System.out.println("added to graph");
	    db.close();
	    
	    DisplayWindow window = new DisplayWindow();
	    window.open("Movement Graph");
	    move_graph.draw(window.display);
	    window.repaint();
	    
		Statistics.displayHistogram("Stay duration", "duration", move_graph.getAllStays(), 100);
		Statistics.displayHistogram("n vists", "n", move_graph.getNVisits(), 100);
		Statistics.displayScatter("duration/distance", "All moves" ,"distance", "duration", move_graph.getAllMoves());
		System.out.println("did stays");
		
		DisplayWindow window_eval = new DisplayWindow();
	    window_eval.open("VisitsGraph");
	    move_graph.plot_evaluation(window_eval.display, new Visits(), new ColorScale());
	    window_eval.repaint();
	    
	    DisplayWindow window_dur = new DisplayWindow();
	    window_dur.open("DurationGraph");
	    move_graph.plot_evaluation(window_dur.display, new Duration(), new ColorScale());
	    window_dur.repaint();
		
	}
	
}
