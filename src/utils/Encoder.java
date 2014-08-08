package utils;

public class Encoder {
	public static double percentFromInt(int percent){
		double factor = 0.5d;
		double result = 0;
		double current_frac = factor;
		//System.out.println(percent);
		for(int i = 6; i >= 0; --i){
			
			int bit = (percent & (1 <<i)) >> i;
			//System.out.println(i+" "+bit);
			result += bit*current_frac;
			current_frac *= factor;
		}
		return result;
	}
	
	public static double stdPercent(int percent){
		return percent*1/127.0;
	}
	public static void main (String[] args){
		for(int i = 127; i >= 0; --i)
			System.out.println(i+" "+percentFromInt(i)+" "+i*1/127.0);
	}
}
