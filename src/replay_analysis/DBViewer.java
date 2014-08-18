package replay_analysis;

import java.io.File;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import utils.Statistics;
import utils.Utils;
import database.Database;
import database.TimeSeries;
import database.Unit;
import db_viewer.DBSelectionWindow;
import db_viewer.DatabaseWindow;

public class DBViewer {
	//private static String dbFile = "data/analysis_mine.sqlite";
	public static void main(String[] args) {
		
		List<String> databaseFiles = Utils.findFiles(new File("."), ".sqlite");

		String dbFile;

		if(databaseFiles.size() == 0){
			JOptionPane.showMessageDialog(null, "No databases found");
			return;
		}
		else if(databaseFiles.size() == 1){
			dbFile = databaseFiles.get(0);
		}
		else{
			Object[] possibilities = new Object[databaseFiles.size()];
			int i = 0;
			for(String file : databaseFiles){
				possibilities[i] = file; 
				i++;
			}
			
			String choice = (String)JOptionPane.showInputDialog(
			                    new JFrame(),
			                    "Select a database file",
			                    "Multiple Databases",
			                    JOptionPane.PLAIN_MESSAGE,
			                    null,
			                    possibilities,
			                    possibilities[0]);

			//If a string was returned, say so.
			if ((choice != null) && (choice.length() > 0)) {
			    dbFile = choice;
			}
			else
				return;
		}
			
		
		
		Database db = new Database(dbFile, false);
		
		DatabaseWindow dbWindow = new DatabaseWindow(db);
		/*Unit u1 = db.getUnit(1);
		
		for(TimeSeries s : u1.timeSeries){
			Statistics.displayLine(s.type, s.type, "Time", "Value", s.getData());
		}*/
	}
}
