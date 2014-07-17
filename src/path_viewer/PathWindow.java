package path_viewer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.util.List;

import utils.ConstantMapper;
import utils.DisplayWindow;

import database.Database;
import database.Path;
import database.PathNode;


class MyDispatcher implements KeyEventDispatcher {
	private PathWindow window;
	public MyDispatcher(PathWindow window) {
		this.window = window;
	}
    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        if (e.getID() == KeyEvent.KEY_PRESSED && e.getKeyCode() == KeyEvent.VK_SPACE) {
        	window.changePathDisplayed();
        }
        
        if (e.getID() == KeyEvent.KEY_PRESSED && e.getKeyCode() == KeyEvent.VK_ENTER) {
        	window.save();
        }
        return false;
    }
}

public class PathWindow extends DisplayWindow {

	private List<Integer> path_ids;
	private int current_path_displayed;
	private String db_file;
	
	public PathWindow(String database_file){
		super();
		db_file = database_file;
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new MyDispatcher(this));
        
        path_ids = null;
        current_path_displayed = -1;
        
        java.util.logging.Logger.getLogger("com.almworks.sqlite4java").setLevel(java.util.logging.Level.OFF); 
        
        Database db = new Database(db_file);
        db.open();
		path_ids = db.getPathIDs();
		db.close();
	    open("PathViewer");
		changePathDisplayed();
	}	
	

	public void changePathDisplayed(){
		if(path_ids == null)
			return;
		current_path_displayed = (current_path_displayed+1)%path_ids.size();
		setTitle("PathViewer "+current_path_displayed+"/"+path_ids.size());

        Database db = new Database(db_file);
        db.open();
		draw(db.loadPath(path_ids.get(current_path_displayed)));
		db.close();
		repaint();
	}
	
	private void draw(Path path){
		double time_start = path.nodes.get(0).time;
		double time_end = path.nodes.get(path.nodes.size()-1).time + path.nodes.get(path.nodes.size()-1).duration;
		display.setText("Player: "+path.player+
				"\n Hero: "+ConstantMapper.heroName(path.unit_id)+
				"\n Timeframe: "+ConstantMapper.formatTime(time_start)+" - "+ConstantMapper.formatTime(time_end));
		display.reset();
		int radius = 5;
		Graphics2D g2d = display.getGraphics();
		g2d.setColor(Color.WHITE);
				
		g2d.setStroke(new BasicStroke(2));
		
		FontMetrics fm = g2d.getFontMetrics();
		
		int[] last_pos = null;
		for(PathNode n : path.nodes){
			int[] position = display.convertCoords(n.position);
			g2d.setColor(Color.WHITE);
			if(last_pos != null)
			{
				g2d.drawLine(last_pos[0], last_pos[1], position[0], position[1]);
			}
			last_pos = position;
			g2d.fillOval(position[0]-radius, position[1]-radius, 2*radius+1, 2*radius+1);
			if(n.duration > 0 ){
				g2d.setColor(Color.GREEN);
				String duration = String.format("%.3g%n", n.duration);
		        Rectangle2D r = fm.getStringBounds(duration, g2d);
		        int x = position[0] + (int)(r.getWidth() / 2);
		        int y = position[1] + (int)(r.getHeight() / 2);
		        g2d.drawString(duration, x, y);
			}
		}
	}
	
	public void save(){
		display.save(current_path_displayed+".png");
	}
}
