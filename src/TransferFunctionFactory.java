
public class TransferFunctionFactory {

	public static TransferFunction getEngine1(){
		TransferFunction tfunc= new TransferFunction(1000);
		

		//tfunc.addRGBPegPoint(.298f, .447f,colorBegin.clone(),colorEnd.clone());
		tfunc.addRGBPegPoint(.298f, .5f, 0, 0f);
		tfunc.addRGBPegPoint(.447f, 1f, 0, 0f);
		tfunc.addRGBPegPoint(.5f, 0f, 0, 0f);
		tfunc.addRGBPegPoint(.66f, 0f, 0, 0f);
		tfunc.addRGBPegPoint(.67f, .6f, .6f, .6f);
		tfunc.addRGBPegPoint(.766f, 1f, 1f, 1f);
		tfunc.addAlphaPegPoint(.24f, 0f);
		tfunc.addAlphaPegPoint(.298f, .6f);
		tfunc.addAlphaPegPoint(.447f, .6f);
		tfunc.addAlphaPegPoint(.448f, .2f);
		tfunc.addAlphaPegPoint(.67f, .4f);
		tfunc.addAlphaPegPoint(.698f, 0);

		
		return tfunc;
		
	}
	
	public static TransferFunction getHead1(){
		TransferFunction tfunc= new TransferFunction(1000);
		

		//tfunc.addRGBPegPoint(.298f, .447f,colorBegin.clone(),colorEnd.clone());
		tfunc.addRGBPegPoint(0.5f, 0f, 0f, 0f);
		tfunc.addAlphaPegPoint(.5f, 0f);
		
		tfunc.addRGBPegPoint(.511f, .1f, .1f, .1f);
		tfunc.addRGBPegPoint(0.52f, 1f, .1f, .1f);
		
		tfunc.addAlphaPegPoint(.23f, 0f);
		tfunc.addAlphaPegPoint(.256f, .0f);
		tfunc.addRGBPegPoint(0.256f, .6f, .4f, .4f);
		tfunc.addRGBPegPoint(.3f, .6f, .4f, .4f);
		tfunc.addAlphaPegPoint(.3f, .0f);
		tfunc.addAlphaPegPoint(.33f, 0f);
		
		
		tfunc.addAlphaPegPoint(.511f, .9f);
		tfunc.addAlphaPegPoint(.52f, 0.9f);
		tfunc.addAlphaPegPoint(.58f, 0.0f);
		
		//tfunc.addAlphaPegPoint(.24f, .1f);
		//tfunc.addAlphaPegPoint(.313f, .0f);
		//tfunc.addAlphaPegPoint(.321f, .3f);
		//tfunc.addAlphaPegPoint(.99f, 1);

		
		return tfunc;
		
	}
	
	/*public static TransferFunction getEngine2(){
		TransferFunction tfunc= new TransferFunction(1000);
		
		float[] colorBegin= new float[4];
		float[] colorEnd= new float[4];
		colorBegin[0]=1.0f;
		colorBegin[1]=1.0f;
		colorBegin[2]=1.0f;
		colorBegin[3]=.3f; 
		colorEnd[0]=1.0f;
		colorEnd[1]=1f;
		colorEnd[2]=1.0f;
		colorEnd[3]=1f;
		tfunc.addPegPoint(.396f, .498f,colorBegin.clone(),colorEnd.clone());
		
		
		return tfunc;
		
	}
	
	public static TransferFunction getOrange1(){
		TransferFunction tfunc= new TransferFunction(1000);
		
		float[] colorBegin= new float[4];
		float[] colorEnd= new float[4];
		colorBegin[0]=1.0f;
		colorBegin[1]=165/255f;
		colorBegin[2]=0.0f;
		colorBegin[3]=.0f; 
		colorEnd[0]=1.0f;
		colorEnd[1]=165/255f;
		colorEnd[2]=0.0f;
		colorEnd[3]=.1f;
		
		tfunc.addPegPoint(.19f, .210f,colorBegin.clone(),colorEnd.clone());
		colorBegin[3]=.1f;
		colorEnd[3]=.0f;
		tfunc.addPegPoint(.212f, .231f,colorBegin.clone(),colorEnd.clone());
		
		colorBegin[0]=1.0f;
		colorBegin[1]=0f;
		colorBegin[2]=0.0f;
		colorBegin[3]=.9f; 
		
		colorEnd[0]=1.0f;
		colorEnd[1]=0.0f;
		colorEnd[2]=0.0f;
		colorEnd[3]=.9f;
		
		tfunc.addPegPoint(.268f, .291f,colorBegin.clone(),colorEnd.clone());
		
		return tfunc;
		
	}*/
}
