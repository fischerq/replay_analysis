package replay_analysis;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


import path_recognition.PathNode;

import movement_graph.ColorScale;
import movement_graph.Duration;
import movement_graph.Graph;
import movement_graph.Visits;
import utils.Database;
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
	    List<LinkedList<PathNode> > paths = db.loadPaths();
	    System.out.println("loaded paths");
	    Iterator<LinkedList<PathNode> > it = paths.iterator();
	    
	    int i = 0;
	    while(it.hasNext()){
	    	LinkedList<PathNode> path = it.next();
	    	move_graph.addPath(path);
	    	i++;
	    }
	    System.out.println("added to graph");
	    db.close();
	    DisplayWindow window = new DisplayWindow();
	    window.open("Movement Graph");
	    move_graph.draw(window.display);
	    window.repaint();
	    
		Statistics.displayHistogram("Stay duration", "duration", move_graph.getAllStays(), 100);
		Statistics.displayHistogram("n vists", "n", move_graph.getNVisits(), 100);
		Statistics.displayHistogram("evg duration", "s", move_graph.getMedDuration(), 100);
		System.out.println("did stays");
		
		DisplayWindow window_eval = new DisplayWindow();
	    window_eval.open("VisitsGraph");
	    move_graph.plot_evaluation(window_eval.display, new Visits(), new ColorScale());
	    window_eval.repaint();
	    
	    DisplayWindow window_dur = new DisplayWindow();
	    window_dur.open("DurationGraph");
	    move_graph.plot_evaluation(window_dur.display, new Duration(), new ColorScale());
	    window_dur.repaint();
		
		//Statistics.displayScatter("Move speeds", "All moves" ,"distance", "speed", move_graph.getAllSpeeds());
		System.out.println("did speeds");
		//Statistics.displayHistogram("Distance variances", "variance", move_graph.getDistanceVariances(), 100);
	}
	
}
