import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public  class TransferFunction  {
	private int resolution;
	private ArrayList<rgbPegPoint> rgbPegs= new ArrayList<rgbPegPoint>();
	private ArrayList<alphaPegPoint> alphaPegs=new ArrayList<alphaPegPoint>();
	
	public TransferFunction(int res){
		resolution=res;
		rgbPegs.add(new rgbPegPoint(0,0,0,0));
		rgbPegs.add(new rgbPegPoint(1,0,0,0));
		alphaPegs.add(new alphaPegPoint(0,0));
		alphaPegs.add(new alphaPegPoint(1,0));
		
	}
	
	public class rgbPegCompare implements Comparator<rgbPegPoint> {

		@Override
		public int compare(rgbPegPoint o1, rgbPegPoint o2) {
			rgbPegPoint pone= o1;
			rgbPegPoint ptwo= o2;
			if(pone.scalar<ptwo.scalar) return -1;
			else if (pone.scalar==ptwo.scalar) return 0;
			else return 1;
		}
	}
	
	public class alphaPegCompare implements Comparator<alphaPegPoint> {

		@Override
		public int compare(alphaPegPoint o1, alphaPegPoint o2) {
			alphaPegPoint pone= o1;
			alphaPegPoint ptwo= o2;
			if(pone.scalar<ptwo.scalar) return -1;
			else if (pone.scalar==ptwo.scalar) return 0;
			else return 1;
		}
	}
	
	
	public void addRGBPegPoint(float scalar,  float r, float g, float b){
		rgbPegs.add(new rgbPegPoint( scalar,   r,  g,  b));
	}
	
	public void addRGBPegPoint(float scalar,  int r, int g, int b){
		rgbPegs.add(new rgbPegPoint( scalar,   r/255.0f,  g/255.0f,  b/255.0f));
	}
	
	public void addAlphaPegPoint(float scalar,  float a){
		alphaPegs.add(new alphaPegPoint( scalar,   a));
	}
	
	private float lerp(float cBegin, float pCurrent, float pB,float pE, float cEnd)
	{
		
		return cBegin+((pCurrent-pB)/(pE-pB))*(cEnd-cBegin);
	}
	
	public  float [] getTransferArray() {
		Collections.sort(rgbPegs, new rgbPegCompare());
		Collections.sort(alphaPegs, new alphaPegCompare());
		
		float[] data= new float[resolution*4];
		float step=1/(float)resolution;
		int currentRGBPeg=0;
		float[] currentColor={0.0f,0.0f,0.0f};
		float currentColorScalar=0;
		float currentAlpha=0;
		float currentAlphaScalar=0;
		int currentAlphaPeg=0;
		float currentStep=0;
		for(int i=0; i<resolution;i++)
		{
			
			
			if(rgbPegs.get(currentRGBPeg).scalar<currentStep || i==0)
			{
				currentColorScalar=rgbPegs.get(currentRGBPeg).scalar;
				currentColor[0]=rgbPegs.get(currentRGBPeg).color[0];
				currentColor[1]=rgbPegs.get(currentRGBPeg).color[1];
				currentColor[2]=rgbPegs.get(currentRGBPeg).color[2];
				currentRGBPeg++;
			}
			if(alphaPegs.get(currentAlphaPeg).scalar<currentStep || i==0)
			{
				currentAlphaScalar=alphaPegs.get(currentAlphaPeg).scalar;
				currentAlpha=alphaPegs.get(currentAlphaPeg).alpha;
				currentAlphaPeg++;
			}
			if(currentRGBPeg==rgbPegs.size()){
				data[i*4]=0;
				data[i*4+1]=0;
				data[i*4+2]=0;
			}
			else{
				
				data[i*4]=lerp(currentColor[0] , currentStep , currentColorScalar ,rgbPegs.get(currentRGBPeg).scalar, rgbPegs.get(currentRGBPeg).color[0]);
				data[i*4+1]=lerp(currentColor[1] , currentStep , currentColorScalar ,rgbPegs.get(currentRGBPeg).scalar, rgbPegs.get(currentRGBPeg).color[1]);
				data[i*4+2]=lerp(currentColor[2] , currentStep , currentColorScalar ,rgbPegs.get(currentRGBPeg).scalar, rgbPegs.get(currentRGBPeg).color[2]);
				if(data[i*4]>1) {
					System.out.println(currentColor[0] +" "+ currentStep +" "+ currentColorScalar +" "+rgbPegs.get(currentRGBPeg).scalar+" "+ rgbPegs.get(currentRGBPeg).color[0]);
					System.out.println(data[i*4]);
				}
			}
			if(currentAlphaPeg==alphaPegs.size()){
				data[i*4+3]=0;
			}
			else
				data[i*4+3]=lerp(currentAlpha , currentStep , currentAlphaScalar ,alphaPegs.get(currentAlphaPeg).scalar, alphaPegs.get(currentAlphaPeg).alpha);
			
			
			
			
			currentStep+=step;
		
		}
		return data;
	}
	
	
	
	
	
	
}
