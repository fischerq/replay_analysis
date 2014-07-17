package path_recognition;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import utils.Display;

import javax.vecmath.Vector3d;

import database.PathNode;

class TrackedNode{
	public double time;
	public double[] position = new double[2];
}

public class Path {
	public int unit_id;
	public String name;
	private List<TrackedNode> nodes;
	private List<PathNode> processed;
	public Path(int id, String name){
		unit_id = id;
		this.name = name;
		nodes = new LinkedList<TrackedNode>();
		processed = new LinkedList<PathNode>();
	}
	
	public void add(double time, double[] position){
		TrackedNode n = new TrackedNode();
		n.position = position.clone();
		n.time = time;
		nodes.add(n);
	}
	
	public List<PathNode> getNodes(){
		return processed;
	}
	
	public void process() {
		double tolerance = 1;
		double time_factor = 522.0f;
		System.out.println("Original nodes: "+nodes.size());
		List<Integer> results = new LinkedList<Integer>();
		results.add(0);
		results.add(nodes.size()-1);
		
		int current_result = 0;
		
		while(current_result != results.size()-1){

			TrackedNode start = nodes.get(results.get(current_result));
			TrackedNode end = nodes.get(results.get(current_result+1));
			//System.out.println("Start: "+results.get(current_result));
			//System.out.println("End: "+results.get(current_result+1));
			
			Vector3d p = new Vector3d();
			Vector3d d = new Vector3d();
			
			p.x = start.position[0];
			p.y = start.position[1];
			p.z = time_factor*start.time;
			d.x = end.position[0]-start.position[0];
			d.y = end.position[1]-start.position[1];
			d.z = time_factor*(end.time-start.time);
			d.normalize();
			//System.out.println("p: "+p+" d: "+d);
			double max_distance = 0;
			int max_index = -1;
			for(int index = results.get(current_result)+1; index < results.get(current_result+1); ++index){

				TrackedNode node = nodes.get(index);

				Vector3d n = new Vector3d();
				n.x = node.position[0];
				n.y = node.position[1];
				n.z = time_factor*node.time;
				//System.out.println("n: "+n);
				n.sub(p);
				//System.out.println("n: "+n);
				double dot_prod = n.dot(d);
				//System.out.println("dot: "+dot_prod);
				Vector3d projected = new Vector3d();
				projected.scale(dot_prod, d);
				
				Vector3d distance_vec = new Vector3d();
				distance_vec.sub(n, projected);
				//System.out.println("projected: "+projected+" dist: "+distance_vec);
				
				double distance = distance_vec.lengthSquared();
				//System.out.println("\t"+index+": "+distance);
				if(distance > max_distance)
				{
					max_index = index;
					max_distance = distance;
				}
			}
			//System.out.println("Max:"+max_distance+" index: "+max_index);
			if(max_distance < tolerance*tolerance)
			{
				current_result++;
			}
			else{
				results.add(current_result+1, max_index);
			}
		}
		
		System.out.println("After simplification: "+results.size());
		
		Iterator<Integer> it = results.iterator(); 
		TrackedNode last = nodes.get(it.next());

		PathNode finished_node = new PathNode();
		finished_node.position[0] = last.position[0];
		finished_node.position[1] = last.position[1];
		finished_node.time = last.time;
		finished_node.duration = 0;
		
		processed = new LinkedList<PathNode>();

		while(it.hasNext()){
			Integer index = it.next();
			TrackedNode next = nodes.get(index);
			double distance = (last.position[0]-next.position[0])*(last.position[0]-next.position[0]) + (last.position[1]-next.position[1])*(last.position[1]-next.position[1]);
			if(distance < tolerance*tolerance){
				finished_node.duration = next.time - finished_node.time;
			}
			else{
				processed.add(finished_node);
				finished_node = new PathNode();
				finished_node.position[0] = next.position[0];
				finished_node.position[1] = next.position[1];
				finished_node.time = next.time;
				finished_node.duration = 0; 
			}
			last = next;
		}
		if(finished_node.duration != 0)
			processed.add(finished_node);
		
		System.out.println("After merging: "+processed.size());
	}

	public void draw(Display display) {
		int radius = 5;
		Graphics2D g2d = display.getGraphics();
		g2d.setColor(Color.WHITE);
		Iterator<TrackedNode> it = nodes.iterator();
		while(it.hasNext()){
			TrackedNode n = it.next();
			int[] position = display.convertCoords(n.position);
			g2d.fillOval(position[0]-radius, position[1]-radius, 2*radius+1, 2*radius+1);
		}
				
		FontMetrics fm = g2d.getFontMetrics();
		
		radius = 4;
		Iterator<PathNode> it2 = processed.iterator();
		int[] last_pos = null;
		while(it2.hasNext()){
			PathNode n = it2.next();
			int[] position = display.convertCoords(n.position);
			g2d.setColor(Color.BLACK);
			if(last_pos != null)
			{
				g2d.drawLine(last_pos[0], last_pos[1], position[0], position[1]);
			}
			last_pos = position;
			g2d.fillOval(position[0]-radius, position[1]-radius, 2*radius+1, 2*radius+1);
			if(n.duration > 0 ){
				g2d.setColor(Color.GREEN);
				String duration = String.format("%.3g%n", n.duration);
		        Rectangle2D r = fm.getStringBounds(duration, g2d);
		        int x = position[0] + (int)(r.getWidth() / 2);
		        int y = position[1] + (int)(r.getHeight() / 2);
		        g2d.drawString(duration, x, y);
			}
		}
	}

}
