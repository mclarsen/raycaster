import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public  class TransferFunction  {
	private int resolution;
	
	public TransferFunction(int res){
		resolution=res;
	}
	
	public class pegCompare implements Comparator<PegPoint> {

		@Override
		public int compare(PegPoint o1, PegPoint o2) {
			PegPoint pone= o1;
			PegPoint ptwo= o2;
			if(pone.scalarBegin<ptwo.scalarBegin) return -1;
			else if (pone.scalarBegin==ptwo.scalarBegin) return 0;
			else return 1;
		}
	}
	private ArrayList<PegPoint> pegs= new ArrayList<PegPoint>();
	
	/**
	 * @param sBegin scalar begin
	 * @param sEnd scalar end
	 * @param cBegin beginning color
	 * @param cEnd	ending color
	 */
	public void addPegPoint(float sBegin, float sEnd, float[] cBegin, float[] cEnd){
		pegs.add(new PegPoint(sBegin,sEnd, cBegin, cEnd));
	}
	
	private float lerp(float cBegin, float pCurrent, float pB,float pE, float cEnd)
	{
		
		return cBegin+((pCurrent-pB)/(pE-pB)*(cEnd-cBegin));
	}
	
	public  float [] getTransferArray() {
		Collections.sort(pegs, new pegCompare());
		float[] data= new float[resolution*4];
		float step=1/(float)resolution;
		int currentPeg=0;
		float currentStep=0;
		boolean noMorePegs=false;
		for(int i=0; i<resolution;i++)
		{
			if( noMorePegs|| pegs.get(currentPeg).scalarBegin>=currentStep )
			{
				data[i*4]=0;
				data[i*4+1]=0;
				data[i*4+2]=0;
				data[i*4+3]=0;
			}
			else if(pegs.get(currentPeg).scalarBegin<currentStep && pegs.get(currentPeg).scalarEnd>=currentStep)
			{
				data[i*4]=lerp(pegs.get(currentPeg).colorBegin[0],currentStep,pegs.get(currentPeg).scalarBegin,pegs.get(currentPeg).scalarEnd,pegs.get(currentPeg).colorEnd[0]);
				data[i*4+1]=lerp(pegs.get(currentPeg).colorBegin[1],currentStep,pegs.get(currentPeg).scalarBegin,pegs.get(currentPeg).scalarEnd,pegs.get(currentPeg).colorEnd[1]);
				data[i*4+2]=lerp(pegs.get(currentPeg).colorBegin[2],currentStep,pegs.get(currentPeg).scalarBegin,pegs.get(currentPeg).scalarEnd,pegs.get(currentPeg).colorEnd[2]);
				data[i*4+3]=lerp(pegs.get(currentPeg).colorBegin[3],currentStep,pegs.get(currentPeg).scalarBegin,pegs.get(currentPeg).scalarEnd,pegs.get(currentPeg).colorEnd[3]);
			}
			else
			{
				currentPeg++;
				if(currentPeg==pegs.size())
					noMorePegs=true;
				
					
			}
			currentStep+=step;
		
		}
		return data;
	}
	
	
	
	public class PegPoint{
		public float scalarBegin=0f;
		public float scalarEnd=1f;
		public float alpha=1f;
		public float[] colorBegin= new float[4];
		public float[] colorEnd=new float[4];
		
		public PegPoint(float sBegin, float sEnd,  float[] cBegin, float[] cEnd){
			scalarBegin=sBegin;
			scalarEnd=sEnd;
			colorBegin= cBegin;
			colorEnd=cEnd;
		}
		
		
	}
	
	
}
