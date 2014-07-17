package movement_graph;

import graph_evaluation.Evaluation;

import java.util.Iterator;


public class Duration extends Evaluation {

	@Override
	public double eval(Position p) {
		double result = 0;
		Iterator<Stay> it = p.stays.iterator();
		while(it.hasNext()){
			Stay s = it.next();//getFilteredNext(it);
			if(s == null)
				break;
			result += s.duration;
		}
		int n = 0;
		for(Stay s : p.stays){
			if(s.duration > 0)
				n++;
		}
		if(n== 0)
			return 0;
		result = result/n;
		result = scale(result, 2);
		return result;
	}

}
