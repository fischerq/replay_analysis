package zera.movement_analysis;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;


class Map extends JPanel {
	static String minimap_file = "data/Minimap.jpg";
	
    private BufferedImage image;
    private Graphics2D g2d;
    public Map() {
        
        loadImage();
        setSurfaceSize();
    }

    private void loadImage() {
        try {
        	image = ImageIO.read(new File(minimap_file));
        	g2d = image.createGraphics();
        } catch (IOException e) {
        }
    }
    
    private void setSurfaceSize() {
        
        Dimension d = new Dimension();
        d.width = image.getWidth(null);
        d.height = image.getHeight(null);
        setPreferredSize(d);        
    }

    public void save(String filename){
    	try {

            File file = new File(filename);
            ImageIO.write(image, "png", file);
        } catch(IOException exc) {
            System.out.println("problem saving");
        }
    }
    private void doDrawing(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(image, 1, 1, null);
    }

    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        doDrawing(g);
    }
}