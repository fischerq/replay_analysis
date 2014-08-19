package db_viewer;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;

import database.Database;

public class DatabaseWindow extends JFrame{
	private Database db;
	
	private List<Integer> replays;
	
	private JPanel selection;
	private JPanel detail;
	
	public DatabaseWindow(Database db){

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		this.db = db;
		replays = db.getReplays();
		
		JPanel panel =  new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		Integer[] replayData = new Integer[replays.size()];
		int i = 0;
		for(Integer id : replays){
			replayData[i] = id;
			i++;
		}
			
		JList<Integer> replayList = new JList<Integer>(replayData); //data has type Object[]
		replayList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		replayList.setLayoutOrientation(JList.VERTICAL);
		replayList.setVisibleRowCount(1);
		
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		panel.add(replayList, c);
		
		selection = new JPanel();
		c.gridx = 0;
		c.gridy = 1;
		panel.add(selection, c);
		
		detail = new JPanel();
		c.gridx = 1;
		c.gridy = 1;
		panel.add(detail, c);
		
		setContentPane(panel);
		pack();
		
		setVisible(true);
	}
}
