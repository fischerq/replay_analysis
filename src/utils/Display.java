package utils;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;


public class Display extends JPanel {
	static String minimap_file = "data/Minimap.jpg";
	
	private String display_text;
	private Image background;
    private BufferedImage image;
    private Graphics2D g2d;
    
    public Display() {
    	try {
        	background = ImageIO.read(new File(minimap_file));
          } catch (IOException e) {
        }
    	
    	image = new BufferedImage(background.getWidth(null), background.getHeight(null),BufferedImage.TYPE_INT_RGB);
    	g2d = image.createGraphics();
    	setSurfaceSize();
        
    	display_text = "---";
        reset();
    }

    public void setText(String s){
    	display_text = s;
    }
    
    public void reset() {
        g2d.drawImage(background, 0, 0, null);
        g2d.setColor(Color.WHITE);
        int x = 10;
        int y = 10;
        for (String line : display_text.split("\n"))
            g2d.drawString(line, x, y += g2d.getFontMetrics().getHeight());
    }
    
    private void setSurfaceSize() {
        
        Dimension d = new Dimension();
        d.width = image.getWidth(null);
        d.height = image.getHeight(null);
        setPreferredSize(d);        
    }
    
    public Graphics2D getGraphics(){
    	return g2d;
    }
    
    public int[] convertCoords(double[] pos){
    	int[] result = new int[2];
    	//x: [-8200, 7930.0] Y: [-8400.0, 8080.0]
    	result[0] = (int)((pos[0]+8200)/(double)(8200+7930)*(double)(image.getWidth()));
    	result[1] = (int)((8080-pos[1])/(double)(8400+8080)*(double)(image.getHeight()));
    	return result;
    }

    public void save(String filename){
    	try {
            File file = new File(filename);
            ImageIO.write(image, "png", file);
        } catch(IOException exc) {
            System.out.println("problem saving");
        }
    }

    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(image, 0, 0, null);
    }
}