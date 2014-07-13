package zera.movement_analysis;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;



public class MapWindow extends JFrame {

	public Map map;
	
    public MapWindow() {

        initUI();
        setVisible(true);
    }

    private void initUI() {

        setTitle("Map");

        map = new Map();
        add(map);
        
        pack();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

}