import java.util.ArrayList;
import java.util.Comparator;


public  class TransferFunction  {
	
	private ArrayList<PegPoint> pegs= new ArrayList<PegPoint>();
	
	public void addPegPoint(float sBegin, float sEnd, float a, float[] cBegin, float[] cEnd){
		pegs.add(new PegPoint(sBegin,sEnd,a, cBegin, cEnd));
	}
	public  float [] getAlphaArray() {
		return null;
	}
	public  float [] getColorArray() {
		return null;
	}
	
	private class PegPoint implements Comparator{
		public float scalarBegin=0f;
		public float scalarEnd=1f;
		public float alha=1f;
		public float[] colorBegin= new float[3];
		public float[] colorEnd=new float[3];
		public PegPoint(float sBegin, float sEnd, float a, float[] cBegin, float[] cEnd){
			scalarBegin=sBegin;
			scalarEnd=sEnd;
			alha=a;
			colorBegin= cBegin;
			colorEnd=cEnd;
		}
		@Override
		public int compare(Object one, Object two) {
			PegPoint pone= (PegPoint)one;
			PegPoint ptwo= (PegPoint)two;
			if(pone.scalarBegin<ptwo.scalarBegin) return -1;
			else if (pone.scalarBegin==ptwo.scalarBegin) return 0;
			else return 1;
		}
	}
	
	
}
