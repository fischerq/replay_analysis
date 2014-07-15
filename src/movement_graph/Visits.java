package movement_graph;

public class Visits extends Evaluation {

	@Override
	public double eval(Position p) {
		double visits = p.moves.size()/2;
		double result = scale(visits, 250);//Math.log(visits)/Math.log(250);
		return result;
	}

}
