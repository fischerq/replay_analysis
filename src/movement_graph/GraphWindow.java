package movement_graph;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;

import database.Database;

import path_viewer.PathWindow;
import utils.DisplayWindow;

class MyDispatcher implements KeyEventDispatcher {
	private GraphWindow window;
	public MyDispatcher(GraphWindow graphWindow) {
		this.window = graphWindow;
	}
    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {

        if (e.getID() == KeyEvent.KEY_PRESSED && e.getKeyCode() == KeyEvent.VK_ENTER) {
        	window.save();
        }
        return false;
    }
}

public class GraphWindow extends DisplayWindow{
	public GraphWindow(){
		super();
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new MyDispatcher(this));
        
	}	

	public void save(){
		display.save(getTitle()+".png");
	}
}
