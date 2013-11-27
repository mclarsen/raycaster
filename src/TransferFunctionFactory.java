
public class TransferFunctionFactory {

	public static TransferFunction getEngine1(){
		TransferFunction tfunc= new TransferFunction(1000);
		

		//tfunc.addRGBPegPoint(.298f, .447f,colorBegin.clone(),colorEnd.clone());
		tfunc.addRGBPegPoint(.298f, .5f, 0, 0f);
		tfunc.addRGBPegPoint(.447f, 1f, 0, 0f);
		tfunc.addRGBPegPoint(.5f, 0f, 0, 0f);
		tfunc.addRGBPegPoint(.66f, 0f, 0, 0f);
		tfunc.addRGBPegPoint(.67f, 1f, 1f, 1f);
		tfunc.addRGBPegPoint(.766f, 1f, 1f, 1f);
		tfunc.addAlphaPegPoint(.24f, 0f);
		tfunc.addAlphaPegPoint(.298f, .05f);
		tfunc.addAlphaPegPoint(.462f, .05f);
		tfunc.addAlphaPegPoint(.469f, 0f);
		
		
		tfunc.addAlphaPegPoint(.671f, 0f);
		tfunc.addAlphaPegPoint(.672f, 1f);
		tfunc.addAlphaPegPoint(.99f, 1f);
		//tfunc.addAlphaPegPoint(.99f, 0);

		
		return tfunc;
		
	}
	
	public static TransferFunction getEngine2(){
		TransferFunction tfunc= new TransferFunction(1000);
		

		//tfunc.addRGBPegPoint(.298f, .447f,colorBegin.clone(),colorEnd.clone());
		tfunc.addRGBPegPoint(.17f, 0f, 0, 0f);
		tfunc.addRGBPegPoint(.176f, 1f, 0, 0f);
		tfunc.addRGBPegPoint(.4f, 1f, 0, 0f);
		tfunc.addRGBPegPoint(.99f, 1f, 0, 0f);


		tfunc.addAlphaPegPoint(.17f, 0f);
		tfunc.addAlphaPegPoint(.176f, 1f);
		tfunc.addAlphaPegPoint(.4f, 1f);
		tfunc.addAlphaPegPoint(.5f, 0f);
		
		
		

		
		return tfunc;
		
	}
	
	public static TransferFunction getHead1(){
		TransferFunction tfunc= new TransferFunction(1000);
		

		//tfunc.addRGBPegPoint(.298f, .447f,colorBegin.clone(),colorEnd.clone());
		tfunc.addRGBPegPoint(0.49f, 0f, 0f, 0f);
		tfunc.addAlphaPegPoint(.5f, 0f);
		
		
		tfunc.addRGBPegPoint(0.50f, 0f, 0f, 0f);
		tfunc.addRGBPegPoint(.511f, .4f, .4f, .4f);
		tfunc.addRGBPegPoint(0.52f, .5f, .5f, .5f);
		tfunc.addRGBPegPoint(0.55f, 0f, 0f, 0f);
		
		
		tfunc.addAlphaPegPoint(.23f, 0f);
		tfunc.addAlphaPegPoint(.256f, .1f);
		tfunc.addRGBPegPoint(0.256f, .0f, .0f, .4f);
		tfunc.addRGBPegPoint(.3f, .0f, .0f, .4f);
		tfunc.addAlphaPegPoint(.3f, .1f);
		tfunc.addAlphaPegPoint(.31f, 0f);
		
		
		tfunc.addAlphaPegPoint(.511f, .9f);
		tfunc.addAlphaPegPoint(.54f, 1f);
		tfunc.addAlphaPegPoint(.58f, 0.0f);
		
		//tfunc.addAlphaPegPoint(.24f, .1f);
		//tfunc.addAlphaPegPoint(.313f, .0f);
		//tfunc.addAlphaPegPoint(.321f, .3f);
		//tfunc.addAlphaPegPoint(.99f, 1);

		
		return tfunc;
		
	}
	
	public static TransferFunction getHead2(){
		TransferFunction tfunc= new TransferFunction(1000);
		

	
		tfunc.addAlphaPegPoint(.5f, 0f);
		
		
		tfunc.addRGBPegPoint(0.01f, 221, 200, 100);
		tfunc.addRGBPegPoint(0.7f, 255, 255, 255);
		
		
		
		
		tfunc.addAlphaPegPoint(.23f, 0f);
		tfunc.addAlphaPegPoint(.256f, .05f);
		
		tfunc.addAlphaPegPoint(.3f, .05f);
		tfunc.addAlphaPegPoint(.31f, 0f);
		
		
		tfunc.addAlphaPegPoint(.511f, .9f);
		tfunc.addAlphaPegPoint(.57f, 1f);
		tfunc.addAlphaPegPoint(.8f, 0.0f);
		
		//tfunc.addAlphaPegPoint(.24f, .1f);
		//tfunc.addAlphaPegPoint(.313f, .0f);
		//tfunc.addAlphaPegPoint(.321f, .3f);
		//tfunc.addAlphaPegPoint(.99f, 1);

		
		return tfunc;
		
	}
	
	public static TransferFunction getOrange1(){
		TransferFunction tfunc= new TransferFunction(1000);
		
		tfunc.addAlphaPegPoint(.03f,0);
		tfunc.addRGBPegPoint(.058f,255,102,0);
		tfunc.addRGBPegPoint(.279f,255,0,0);
		
		tfunc.addAlphaPegPoint(.058f,.05f );
		tfunc.addAlphaPegPoint(.099f,.05f );
		tfunc.addAlphaPegPoint(.127f,0);
		tfunc.addAlphaPegPoint(.279f,0);
		tfunc.addAlphaPegPoint(.52f,.6f);
		tfunc.addAlphaPegPoint(.6f,0f);
		
		
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
	
	*/
}
