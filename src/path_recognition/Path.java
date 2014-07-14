package path_recognition;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import utils.Display;

import javax.vecmath.Vector3f;

class Node{
	public float time;
	public float[] position = new float[2];
}

class ProcessedNode{
	public float time;
	public float[] position = new float[2];
	public float duration;
}


public class Path {
	private List<Node> nodes;
	private List<ProcessedNode> processed;
	public Path(){
		nodes = new LinkedList<Node>();
		processed = new LinkedList<ProcessedNode>();
	}
	
	public void add(float time, float[] position){
		Node n = new Node();
		n.position = position.clone();
		n.time = time;
		nodes.add(n);
	}
	
	public void process() {
		float tolerance = 150;
		float time_factor = 522.0f;
		System.out.println("Original nodes: "+nodes.size());
		List<Integer> results = new LinkedList<Integer>();
		results.add(0);
		results.add(nodes.size()-1);
		
		int current_result = 0;
		
		while(current_result != results.size()-1){

			Node start = nodes.get(results.get(current_result));
			Node end = nodes.get(results.get(current_result+1));
			//System.out.println("Start: "+results.get(current_result));
			//System.out.println("End: "+results.get(current_result+1));
			
			Vector3f p = new Vector3f();
			Vector3f d = new Vector3f();
			
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

				Node node = nodes.get(index);

				Vector3f n = new Vector3f();
				n.x = node.position[0];
				n.y = node.position[1];
				n.z = time_factor*node.time;
				//System.out.println("n: "+n);
				n.sub(p);
				//System.out.println("n: "+n);
				float dot_prod = n.dot(d);
				//System.out.println("dot: "+dot_prod);
				Vector3f projected = new Vector3f();
				projected.scale(dot_prod, d);
				
				Vector3f distance_vec = new Vector3f();
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
		Node last = nodes.get(it.next());

		ProcessedNode finished_node = new ProcessedNode();
		finished_node.position[0] = last.position[0];
		finished_node.position[1] = last.position[1];
		finished_node.time = last.time;
		finished_node.duration = 0;
		
		processed = new LinkedList<ProcessedNode>();

		while(it.hasNext()){
			Integer index = it.next();
			Node next = nodes.get(index);
			float distance = (last.position[0]-next.position[0])*(last.position[0]-next.position[0]) + (last.position[1]-next.position[1])*(last.position[1]-next.position[1]);
			if(distance < tolerance){
				finished_node.duration = next.time - finished_node.time;
			}
			else{
				processed.add(finished_node);
				finished_node = new ProcessedNode();
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
		Iterator<Node> it = nodes.iterator();
		while(it.hasNext()){
			Node n = it.next();
			int[] position = display.convertCoords(n.position);
			g2d.fillOval(position[0]-radius, position[1]-radius, 2*radius+1, 2*radius+1);
		}
				
		FontMetrics fm = g2d.getFontMetrics();
		
		radius = 4;
		Iterator<ProcessedNode> it2 = processed.iterator();
		int[] last_pos = null;
		while(it2.hasNext()){
			ProcessedNode n = it2.next();
			int[] position = display.convertCoords(n.position);
			g2d.setColor(Color.BLACK);
			if(last_pos != null)
			{
				g2d.drawLine(last_pos[0], last_pos[1], position[0], position[1]);
			}
			last_pos = position;
			g2d.fillOval(position[0]-radius, position[1]-radius, 2*radius+1, 2*radius+1);
			
			g2d.setColor(Color.GREEN);
			String duration = String.format("%.3g%n", n.duration);
	        Rectangle2D r = fm.getStringBounds(duration, g2d);
	        int x = position[0] + (int)(r.getWidth() / 2);
	        int y = position[1] + (int)(r.getHeight() / 2);
	        //g2d.drawString(duration, x, y);
		}
	}

}
