package movement_graph;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ags.utils.dataStructures.trees.thirdGenKD.KdTree;
import ags.utils.dataStructures.trees.thirdGenKD.SquareEuclideanDistanceFunction;

import path_recognition.PathNode;
import utils.Display;

public class Graph {
	private KdTree<Position> nodes;
	private LinkedList<Position> nodes_list;
	private List<Move> moves;
	
	public Graph(){
		moves = new LinkedList<Move>();
		nodes = new KdTree<Position>(2);
		nodes_list = new LinkedList<Position>();
	}
	
	public void initialize(){
		//x: [-8200, 7930.0] Y: [-8400.0, 8080.0]
		float radius = 150;
		int shift = 0;
		for(float y =-8400+radius; y < 8080; y+=2*radius ){
			for(float x =-8200+radius-shift*radius; x < 7930;x+=2*radius ){
				double[] p = new double[2];
				p[0] = x;
				p[1] = y;
				Position node = new Position(p);
				nodes.addPoint(p, node);
				nodes_list.add(node);
			}
			if(shift == 0)
				shift = 1;
			else shift = 0;
		}
	}
	
	public void addPath(List<PathNode> nodes){
		Iterator<PathNode> it = nodes.iterator();
		if(!it.hasNext())
			return;
		
		PathNode node = it.next();
		Position pos_last = insertPosition(node.position);
		double last_end_time = node.time + node.duration;
		if(node.duration > 0)
			pos_last.stays.add(new Stay(node.time + node.duration/2, node.duration));
		
		while(it.hasNext()){
			node = it.next();
			
			Position pos_next = insertPosition(node.position);
			if(node.duration > 0)
				pos_next.stays.add(new Stay(node.time + node.duration/2, node.duration));
			
			Move m = new Move();
			m.start = pos_last;
			m.end = pos_next;
			m.duration = node.time - last_end_time;
			m.time = (node.time + last_end_time) / 2;
			
			moves.add(m);
			pos_last.moves.add(m);
			pos_next.moves.add(m);
			pos_last = pos_next;
			last_end_time = node.time + node.duration;
			
		}
	}
	
	public Position insertPosition(double[] pos){
		return nodes.findNearestNeighbors(pos, 1, new SquareEuclideanDistanceFunction()).getMax();
	}
	
	public void draw(Display display){
		Graphics2D g2d = display.getGraphics();
		g2d.setColor(Color.WHITE);
		int radius = 3;
		
		
		Iterator<Position> it_n = nodes_list.iterator();
		while(it_n.hasNext()){
			Position node = it_n.next();
			if(node.moves.size() == 0 && node.stays.size() == 0)
				continue;
			int[] position = display.convertCoords(node.position);
			g2d.fillOval(position[0]-radius, position[1]-radius, 2*radius+1, 2*radius+1);
		}
		
		Iterator<Move> it_m = moves.iterator();
		while(it_m.hasNext()){
			Move m = it_m.next();
			int[] pos1 = display.convertCoords(m.start.position);
			int[] pos2 = display.convertCoords(m.end.position);
			g2d.drawLine(pos1[0], pos1[1], pos2[0], pos2[1]);
		}
	}
}
