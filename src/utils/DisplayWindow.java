package utils;

import javax.swing.JFrame;

public class DisplayWindow extends JFrame {

	public Display display;

    public DisplayWindow() {
        display = new Display();
    }

    public void open(String title) {
        setTitle(title);
        add(display);
        pack();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }


}