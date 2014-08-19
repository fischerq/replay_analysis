package data_extraction;

import javax.vecmath.Vector2f;

public class Projectile {
	public int projectileIndex;
	public int handleSource;
	public Vector2f position;
	public double speed;
	
	public Projectile(int index, int hSource){
		projectileIndex = index;
		handleSource = hSource;
	}
}
