package utils;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.JFrame;

import path_recognition.Path;


class MyDispatcher implements KeyEventDispatcher {
	private DisplayWindow window;
	public MyDispatcher(DisplayWindow window) {
		this.window = window;
	}
    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        if (e.getID() == KeyEvent.KEY_PRESSED && e.getKeyCode() == KeyEvent.VK_SPACE) {
        	window.changePathDisplayed();
        }
        return false;
    }
}

public class DisplayWindow extends JFrame {

	public Display display;

	private List<Path> paths_recognized;
	private int current_path_displayed;
	
    public DisplayWindow() {
        display = new Display();
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new MyDispatcher(this));
        
        paths_recognized = null;
        current_path_displayed = -1;
    }

    public void open(String title) {
        setTitle(title);
        add(display);
        pack();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

	public void addPathResults(List<Path> results) {
		paths_recognized = results;
		current_path_displayed = -1;
		changePathDisplayed();
	}
	
	public void changePathDisplayed(){
		if(paths_recognized == null)
			return;
		current_path_displayed = (current_path_displayed+1)%paths_recognized.size();
		display.setText("Path "+current_path_displayed+"/"+paths_recognized.size());
		display.reset();
		paths_recognized.get(current_path_displayed).draw(display);
		repaint();
		
		display.save("out.png");
	}
}