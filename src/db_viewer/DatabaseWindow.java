package db_viewer;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import database.Database;
import database.Event;
import database.Replay;
import database.Team;
import database.Unit;

@SuppressWarnings("serial")
public class DatabaseWindow extends JFrame{

	private static final String font = "Verdana";
	private static final int big_size = 18;
	private static final int normal_size = 14;

	
	private JPanel contentPane;
	private Database db;
	
	private JList<String> listTeams;
	private JList<String> listUnits;
	private JList<String> listEvents;
	
	private JPanel panelDisplay;
	private JPanel panelSelection;
	
	private Replay replay;
	private List<Unit.UnitSummary> units;
	private List<Event> events;
		
	public DatabaseWindow(Database db){

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.db = db;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0, 0, 0};
		gbl_contentPane.rowHeights = new int[]{30, 0, 0};
		gbl_contentPane.columnWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		JPanel panelHeader = new JPanel();
		GridBagConstraints gbc_panelHeader = new GridBagConstraints();
		gbc_panelHeader.gridwidth = 2;
		gbc_panelHeader.insets = new Insets(0, 0, 5, 0);
		gbc_panelHeader.fill = GridBagConstraints.BOTH;
		gbc_panelHeader.gridx = 0;
		gbc_panelHeader.gridy = 0;
		contentPane.add(panelHeader, gbc_panelHeader);
		
		JLabel lblReplay = new JLabel("Replay");
		lblReplay.setFont(new Font(font, Font.BOLD, big_size));
		panelHeader.add(lblReplay);
		
		List<Integer> replays = db.getReplays();
		Integer[] replayData = new Integer[replays.size()];
		int i = 0;
		for(Integer id : replays){
			replayData[i] = id;
			i++;
		}		
		
		JComboBox<Integer> comboBox = new JComboBox<Integer>(replayData);
		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				@SuppressWarnings("unchecked")
				JComboBox<Integer> cb = (JComboBox<Integer>) arg0.getSource();
				loadReplay((Integer)cb.getSelectedItem());
			}
		});
		panelHeader.add(comboBox);
		
		panelSelection = new JPanel();
		GridBagConstraints gbc_panelSelection = new GridBagConstraints();
		gbc_panelSelection.insets = new Insets(0, 0, 0, 5);
		gbc_panelSelection.fill = GridBagConstraints.BOTH;
		gbc_panelSelection.gridx = 0;
		gbc_panelSelection.gridy = 1;
		contentPane.add(panelSelection, gbc_panelSelection);
		GridBagLayout gbl_panelSelection = new GridBagLayout();
		gbl_panelSelection.columnWidths = new int[]{0, 0};
		gbl_panelSelection.rowHeights = new int[]{0, 0, 0, 0};
		gbl_panelSelection.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_panelSelection.rowWeights = new double[]{1.0, 1.0, 1.0, Double.MIN_VALUE};
		panelSelection.setLayout(gbl_panelSelection);
		
		listTeams = new JList<String>();
		listTeams.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				displayTeam(replay.teams.get(arg0.getFirstIndex()));
			}
		});
		listTeams.setVisibleRowCount(2);
		listTeams.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		GridBagConstraints gbc_listTeams = new GridBagConstraints();
		gbc_listTeams.insets = new Insets(0, 0, 5, 0);
		gbc_listTeams.fill = GridBagConstraints.BOTH;
		gbc_listTeams.gridx = 0;
		gbc_listTeams.gridy = 0;
		panelSelection.add(listTeams, gbc_listTeams);
		
		listUnits = new JList<String>();
		listUnits.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
			}
		});
		listUnits.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		GridBagConstraints gbc_listUnits = new GridBagConstraints();
		gbc_listUnits.insets = new Insets(0, 0, 5, 0);
		gbc_listUnits.fill = GridBagConstraints.BOTH;
		gbc_listUnits.gridx = 0;
		gbc_listUnits.gridy = 1;
		panelSelection.add(listUnits, gbc_listUnits);
		
		listEvents = new JList<String>();
		listEvents.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
			}
		});
		listEvents.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		GridBagConstraints gbc_listEvents = new GridBagConstraints();
		gbc_listEvents.fill = GridBagConstraints.BOTH;
		gbc_listEvents.gridx = 0;
		gbc_listEvents.gridy = 2;
		panelSelection.add(listEvents, gbc_listEvents);
		
		panelDisplay = new JPanel();
		GridBagConstraints gbc_panelDisplay = new GridBagConstraints();
		gbc_panelDisplay.fill = GridBagConstraints.BOTH;
		gbc_panelDisplay.gridx = 1;
		gbc_panelDisplay.gridy = 1;
		contentPane.add(panelDisplay, gbc_panelDisplay);
		
		setVisible(true);
	}

	private void loadReplay(Integer id) {
		replay = db.getReplay(id);
		System.out.println("Loading replay");
		
		String[] teamNames = new String[replay.teams.size()];
		int i = 0;
		for(Team t : replay.teams){
			teamNames[i] = t.name;
			i++;
		}
		System.out.println(Arrays.toString(teamNames));
		listTeams.setListData(teamNames);
			
		units = db.getUnits(replay);
		String[] unitNames = new String[units.size()];
		i = 0;
		for(Unit.UnitSummary u : units){
			unitNames[i]= u.type+(u.illusion ? "(Illusion)" : "");
			i++;
		}
		System.out.println("Finished units");
		listUnits.setListData(unitNames);
		
		events = db.getEvents(replay);
		System.out.println(events.size());
		Set<String> types = new HashSet<String>();
		for(Event e : events){
			types.add(e.type);
		}
		String[] eventTypes = events.toArray(new String[1]);
			System.out.println("Done events");	
		listEvents.setListData(eventTypes);
		
		panelSelection.revalidate();
		panelSelection.repaint();
		System.out.println("Finished");
	}

	
	private void displayTeam(Team team) {
		panelDisplay.removeAll();
		
		GridBagLayout gbl_displayTeam = new GridBagLayout();
		gbl_displayTeam.columnWidths = new int[]{0, 0, 0};
		gbl_displayTeam.rowHeights = new int[]{30, 30, 0};
		gbl_displayTeam.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_displayTeam.rowWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		panelDisplay.setLayout(gbl_displayTeam);
		
		GridBagConstraints gbc_Title = new GridBagConstraints();
		gbc_Title.gridwidth = 2;
		gbc_Title.insets = new Insets(0, 0, 5, 0);
		gbc_Title.fill = GridBagConstraints.BOTH;
		gbc_Title.gridx = 0;
		gbc_Title.gridy = 0;
		JLabel title = new JLabel("Team");
		title.setFont(new Font(font, Font.BOLD, big_size));
		panelDisplay.add(title, gbc_Title);
		
		JLabel name = new JLabel("Team");
		title.setFont(new Font(font, Font.PLAIN, normal_size));
		GridBagConstraints gbc_Name = new GridBagConstraints();
		gbc_Name.insets = new Insets(0, 0, 0, 5);
		gbc_Name.fill = GridBagConstraints.BOTH;
		gbc_Name.gridx = 0;
		gbc_Name.gridy = 1;
		panelDisplay.add(name, gbc_Name);
		
		JLabel nameValue = new JLabel(team.name);
		title.setFont(new Font(font, Font.PLAIN, normal_size));
		GridBagConstraints gbc_NameValue = new GridBagConstraints();
		gbc_NameValue.insets = new Insets(0, 0, 0, 5);
		gbc_NameValue.fill = GridBagConstraints.BOTH;
		gbc_NameValue.gridx = 1;
		gbc_NameValue.gridy = 1;
		panelDisplay.add(nameValue, gbc_NameValue);
		
		panelDisplay.revalidate();
		panelDisplay.repaint();
	}

}
