package replay_analysis;

import utils.Statistics;
import database.Database;
import database.TimeSeries;
import database.Unit;

public class DBViewer {
	private static String dbFile = "data/analysis_mine.sqlite";
	public static void main(String[] args) {
		Database db = new Database(dbFile);
		db.open_read();
		Unit u1 = db.getUnit(1);
		
		for(TimeSeries s : u1.timeSeries){
			Statistics.displayLine(s.type, s.type, "Time", "Value", s.getData());
		}
	}
}
