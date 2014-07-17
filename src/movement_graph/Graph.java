package movement_graph;

import graph_evaluation.ColorScale;
import graph_evaluation.Evaluation;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import database.Path;
import database.PathNode;

import ags.utils.dataStructures.trees.thirdGenKD.KdTree;
import ags.utils.dataStructures.trees.thirdGenKD.SquareEuclideanDistanceFunction;

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
	
	public void addPath(Path path){
		Iterator<PathNode> it = path.nodes.iterator();
		if(!it.hasNext())
			return;
		
		PathNode node = it.next();
		Position pos_last = insertPosition(node.position);
		
		double last_start = node.time;
		double last_end = node.time + node.duration;
		
		Move last_move = null;
		
		while(it.hasNext()){
			node = it.next();
			
			Position pos_next = insertPosition(node.position);
			
			if(pos_next == pos_last){
				last_end = node.time+node.duration;
			}
			else{
				Stay s = new Stay((last_start+last_end)/2, last_end - last_start);
				pos_last.stays.add(s);
				Move m = new Move();
				m.start = pos_last;
				m.end = pos_next;
				m.duration = node.time - last_end;
				m.time = (node.time + last_end) / 2;
				
				m.last = last_move;
				if(last_move != null)
					last_move.next = m;
				last_move = m;
				
				moves.add(m);
				pos_last.moves.add(m);
				pos_next.moves.add(m);
				
				last_start = node.time;
				last_end = node.time + node.duration;
				pos_last = pos_next;
			}	
		}
	}
	
	public Position insertPosition(double[] pos){
		return nodes.findNearestNeighbors(pos, 1, new SquareEuclideanDistanceFunction()).getMax();
	}
	
	public void draw(Display display){
		Graphics2D g2d = display.getGraphics();
		g2d.setColor(Color.WHITE);
		int radius = 3;
	
		for(Position node : nodes_list){
			if(node.moves.size() == 0 && node.stays.size() == 0)
				continue;
			int[] position = display.convertCoords(node.position);
			g2d.fillOval(position[0]-radius, position[1]-radius, 2*radius+1, 2*radius+1);
		}
		
		for(Move m : moves){
			int[] pos1 = display.convertCoords(m.start.position);
			int[] pos2 = display.convertCoords(m.end.position);
			g2d.drawLine(pos1[0], pos1[1], pos2[0], pos2[1]);
		}
	}
	
	public void plot_evaluation(Display display, Evaluation e, ColorScale cs){
		
		Graphics2D g2d = display.getGraphics();
		for(int i = 0; i<= 10; ++i){
			g2d.setColor(cs.map((double)i/10));
			g2d.fillOval(20+i*10, 20, 10, 10);
		}
		int radius = 7;
	
		for(Position node : nodes_list){
			if(node.moves.size() == 0 && node.stays.size() == 0)
				continue;
			int[] position = display.convertCoords(node.position);
			double value = e.eval(node);
			if(value < 0)
				continue;
			g2d.setColor(cs.map(value));
			g2d.fillOval(position[0]-radius, position[1]-radius, 2*radius+1, 2*radius+1);
		}
	}
	
	public double[] getAllStays(){
		int total_num = 0;
		for(Position p : nodes_list){
			for(Stay s: p.stays)
				if(s.duration > 0)
					total_num++;
		}
		double[] array = new double[total_num];
		int i=0;
		for(Position p : nodes_list){
			for(Stay s: p.stays){
				if(s.duration == 0)
					continue;
				array[i] = s.duration;
				if(array[i] > 20)
					array[i] = 20;
				i++;
			}
		}
		return array;
	}
	
	public double[] getNVisits(){
		int num = 0;
		for(Position p : nodes_list){
			if(p.moves.size() > 0)
				num++;
		}
		
		double[] array = new double[num];
		int i=0;
		for(Position p : nodes_list){
			if(p.moves.size() == 0)
				continue;
			array[i] = p.moves.size()/2;
			if(array[i] > 250)
				array[i] = 250;
			i++;
		}
		return array;
	}
	
	public double[] getDurations(){
		int num = 0;
		for(Move m : moves){
			if(m.duration > 0)
				num++;
		}
		
		double[] array = new double[num];
		int i=0;
		for(Move m : moves){
			if(m.duration == 0)
				continue;
			array[i] = m.duration;
			if(array[i] > 5)
				array[i] = 5;
			i++;
		}
		return array;
	}
	
	
	public double[][] getAllMoves(){
		double[][] array = new double[moves.size()][2];
		for(int i = 0; i < moves.size(); i++){
			array[i][0] = moves.get(i).distance();
			array[i][1] = moves.get(i).duration;
			if(array[i][1] > 5)
				array[i][1] = 5;
			//if(array[i][0] > 5000)
			//	array[i][0] = 5000;
		}
		return array;
	}
	
	public double[] getDistanceVariances(){
		double[] variances = new double[nodes_list.size()];

		int i= 0;
		for(Position p : nodes_list){
			double variance;
			
		    int n = 0;
			double mean = 0;
			double M2 = 0;
			for(Move move : p.moves){
				n = n + 1;
				double delta = move.distance() - mean;
		        mean = mean + delta/n;
				M2 = M2 + delta*(move.distance() - mean);
			}
			
			if (n < 2)
		        variance =  0;
			else
				variance = M2/(n - 1);
		    variances[i] = variance;
		    ++i;
		}
		
		return variances;
	}
}
