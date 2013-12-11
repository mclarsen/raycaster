
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
	public static TransferFunction getEnzo1(){
		TransferFunction tfunc= new TransferFunction(10000);
		


		tfunc.addAlphaPegPoint(.069f, .0f);
		tfunc.addAlphaPegPoint(.07f, 0.01f);
		tfunc.addRGBPegPoint(0.069f, .0f, .0f, .0f);
		tfunc.addRGBPegPoint(0.07f, .9f, .0f, .0f);
		tfunc.addRGBPegPoint(.99f, .9f, .9f, .0f);
		tfunc.addAlphaPegPoint(.9f, .5f);
		tfunc.addAlphaPegPoint(.999f, .00f);
		
		tfunc.addAlphaPegPoint(.00f, .000f);
		tfunc.addAlphaPegPoint(.055f, 0.00515f);
		tfunc.addRGBPegPoint(0.055f, .0f, .0f, .0f);
		tfunc.addRGBPegPoint(.062f, .0f, .9f, .0f);
		tfunc.addRGBPegPoint(.063f, .0f, .0f, .0f);
		tfunc.addAlphaPegPoint(.062f, .00649f);
		tfunc.addAlphaPegPoint(.063f, .0f);


		return tfunc;
	}
	
	public static TransferFunction getEnzo2(){
		TransferFunction tfunc= new TransferFunction(10000);
		
		tfunc.addAlphaPegPoint(.00f, .000f);
		tfunc.addAlphaPegPoint(.2f, 0.005f);
		tfunc.addRGBPegPoint(0.2f, .0f, .0f, .9f);
		tfunc.addAlphaPegPoint(.23f, .011f);
		tfunc.addRGBPegPoint(.25f, .0f, .9f, .9f);
		tfunc.addRGBPegPoint(.26f, .0f, .0f, .0f);
		tfunc.addAlphaPegPoint(.25f, .005f);
		tfunc.addAlphaPegPoint(.26f, .0f);
		
		tfunc.addAlphaPegPoint(.45f, .000f);
		tfunc.addAlphaPegPoint(.46f, 0.01f);
		tfunc.addRGBPegPoint(0.46f, .9f, .9f, .0f);
		//tfunc.addRGBPegPoint(0.55f, .0f, .9f, .0f);
		tfunc.addRGBPegPoint(.7f, .0f, .9f, .0f);
		tfunc.addRGBPegPoint(.71f, .0f, .0f, .0f);
		tfunc.addAlphaPegPoint(.7f, .005f);
		tfunc.addAlphaPegPoint(.71f, .0f);

		
		tfunc.addAlphaPegPoint(.79f, .000f);
		tfunc.addAlphaPegPoint(.8f, 0.008f);
		tfunc.addRGBPegPoint(0.8f, .9f, .0f, .0f);
		tfunc.addAlphaPegPoint(.999f, 0.1f);
		tfunc.addRGBPegPoint(0.9999f, .9f, .0f, .0f);
		//tfunc.addAlphaPegPoint(.99f, .0f);
		//tfunc.addAlphaPegPoint(.12f, .9f);
		//tfunc.addRGBPegPoint(.12f, 1f, 1f, .0f);
		//tfunc.addRGBPegPoint(.99f, 1f, 0f, .0f);
		
		//tfunc.addAlphaPegPoint(.99f, .949f);
//		tfunc.addAlphaPegPoint(.00f, .000f);
//		tfunc.addAlphaPegPoint(.055f, 0.00515f);
//		tfunc.addRGBPegPoint(0.055f, .0f, .0f, .0f);
//		tfunc.addRGBPegPoint(.062f, .0f, .9f, .0f);
//		tfunc.addRGBPegPoint(.063f, .0f, .0f, .0f);
//		tfunc.addAlphaPegPoint(.062f, .00649f);
//		tfunc.addAlphaPegPoint(.063f, .0f);


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
		TransferFunction tfunc= new TransferFunction(10000);
		

	
		tfunc.addAlphaPegPoint(.5f, 0f);
		
		
		tfunc.addRGBPegPoint(0.01f, 221, 200, 100);//234, 221,161
		tfunc.addRGBPegPoint(0.7f, 255, 255, 255);
		
		tfunc.addRGBPegPoint(0.284f, 234, 221, 161);
		tfunc.addRGBPegPoint(0.285f, 234, 0, 0);
		//tfunc.addRGBPegPoint(0.297f, 234, 0, 0);
		//tfunc.addRGBPegPoint(0.298f, 234, 221, 161);
		tfunc.addRGBPegPoint(0.332f, 234, 0, 0); //test
		tfunc.addRGBPegPoint(0.334f, 234, 221, 161);
		// 285-29 red
		tfunc.addAlphaPegPoint(.284f, .050f);
		tfunc.addAlphaPegPoint(.285f, .05f);
		
		tfunc.addAlphaPegPoint(.297f, .05f);
		tfunc.addAlphaPegPoint(.298f, .05f);
		
		
		tfunc.addAlphaPegPoint(.23f, 0f);
		tfunc.addAlphaPegPoint(.256f, .05f);
		
		//tfunc.addAlphaPegPoint(.3f, .05f);  
		//tfunc.addAlphaPegPoint(.31f, 0f);
		tfunc.addAlphaPegPoint(.335f, .05f); //test
		tfunc.addAlphaPegPoint(.336f, 0f);
		
		tfunc.addAlphaPegPoint(.55f, .9f);
		tfunc.addAlphaPegPoint(.57f, 1f);
		tfunc.addAlphaPegPoint(.8f, 0.0f);
		
		//tfunc.addAlphaPegPoint(.24f, .1f);
		//tfunc.addAlphaPegPoint(.313f, .0f);
		//tfunc.addAlphaPegPoint(.321f, .3f);
		//tfunc.addAlphaPegPoint(.99f, 1);

		
		return tfunc;
		
	}
	
	public static TransferFunction getHead4(){
		TransferFunction tfunc= new TransferFunction(1000);
		

	
		
		tfunc.addAlphaPegPoint(.144f, 0f);
		tfunc.addAlphaPegPoint(.145f, .1f);
		
		tfunc.addRGBPegPoint(0.145f, .5f, 0f, 0f);
		
		tfunc.addAlphaPegPoint(.165f, .2f);
		
		tfunc.addRGBPegPoint(0.19f, 0.9f, .0f, .0f);
		tfunc.addAlphaPegPoint(.19f, .1f);
		tfunc.addAlphaPegPoint(.191f, .0f);
		
		
		return tfunc;
		
	}
	
	
	public static TransferFunction getHead3(){
		TransferFunction tfunc= new TransferFunction(1000);
		

	
		tfunc.addAlphaPegPoint(.5f, 0f);
		
		
		tfunc.addRGBPegPoint(0.01f, 221, 230, 230);//234, 221,161

		tfunc.addRGBPegPoint(0.334f, 234, 234, 234);
		// 285-29 red\
		tfunc.addAlphaPegPoint(.21f, 0f);
		tfunc.addAlphaPegPoint(.22f, .04f);
		tfunc.addAlphaPegPoint(.284f, .050f);
		tfunc.addAlphaPegPoint(.285f, .05f);
		
		tfunc.addAlphaPegPoint(.297f, .005f);
		tfunc.addAlphaPegPoint(.298f, .05f);
		tfunc.addAlphaPegPoint(.3f, .0f);
		

		tfunc.addAlphaPegPoint(.331f, .0f);
		//tfunc.addAlphaPegPoint(.3f, .05f);  
		//tfunc.addAlphaPegPoint(.31f, 0f);
		tfunc.addAlphaPegPoint(.332f, .1f);
		tfunc.addAlphaPegPoint(.335f, .1f); //test
		tfunc.addAlphaPegPoint(.36f, 0f);
		
		//tfunc.addAlphaPegPoint(.55f, .9f);
		//tfunc.addAlphaPegPoint(.57f, 1f);
		//tfunc.addAlphaPegPoint(.8f, 0.0f);
		
		//tfunc.addAlphaPegPoint(.24f, .1f);
		//tfunc.addAlphaPegPoint(.313f, .0f);
		//tfunc.addAlphaPegPoint(.321f, .3f);
		//tfunc.addAlphaPegPoint(.99f, 1);

		
		return tfunc;
		
	}
	
	public static TransferFunction getOrange1(){
		TransferFunction tfunc= new TransferFunction(1000);
		
		tfunc.addAlphaPegPoint(.03f,0);
		
		tfunc.addRGBPegPoint(.279f,255,0,0);
		tfunc.addRGBPegPoint(.058f,255,102,0);
		
		
		tfunc.addAlphaPegPoint(.058f,.05f );
		tfunc.addAlphaPegPoint(.099f,.05f );
		tfunc.addAlphaPegPoint(.127f,0);
		tfunc.addAlphaPegPoint(.279f,0);
		tfunc.addAlphaPegPoint(.52f,.3f);
		tfunc.addAlphaPegPoint(.6f,0f);
		
		
		return tfunc;
		
	}
	
	public static TransferFunction getOrange2(){
		TransferFunction tfunc= new TransferFunction(1000);
		
		tfunc.addAlphaPegPoint(.044f, 0f);
		tfunc.addAlphaPegPoint(.045f, .009f);
		
		tfunc.addRGBPegPoint(0.045f, .5f, 0f, 0f);
		
		tfunc.addAlphaPegPoint(.7f, .3f);
		
		tfunc.addRGBPegPoint(0.11f, 0.9f, 0f, .0f);
		
		tfunc.addAlphaPegPoint(.11f, .009f);
		tfunc.addAlphaPegPoint(.12f, .0f);
		tfunc.addRGBPegPoint(0.12f, 0.0f, .0f, .0f);
		
		
		
		
		
		tfunc.addAlphaPegPoint(.24f, 0f);
		tfunc.addAlphaPegPoint(.245f, .005f);
		
		tfunc.addRGBPegPoint(0.245f, .9f, .9f, 0f);
		
		tfunc.addAlphaPegPoint(.27f, .007f);
		
		tfunc.addRGBPegPoint(0.31f, 0.9f, .9f, .0f);
		
		tfunc.addAlphaPegPoint(.31f, .005f);
		tfunc.addAlphaPegPoint(.32f, .0f);
		tfunc.addRGBPegPoint(0.32f, 0.0f, .0f, .0f);
		
		
		
		
		
		tfunc.addAlphaPegPoint(.51f, 0f);
		tfunc.addAlphaPegPoint(.52f, .01f);
		
		tfunc.addRGBPegPoint(0.52f, 0,191,255);
		
		//tfunc.addAlphaPegPoint(.27f, .01f);
		
		tfunc.addRGBPegPoint(0.0f, 0,191,255);
		
		tfunc.addAlphaPegPoint(.99f, .01f);
		//tfunc.addAlphaPegPoint(.99f, .0f);
		//tfunc.addRGBPegPoint(0.32f, 0.0f, .0f, .0f);
		
		return tfunc;
		
	}
	
	public static TransferFunction getFuel1(){
		TransferFunction tfunc= new TransferFunction(1000);
		
		tfunc.addAlphaPegPoint(.001f, 0f);
		tfunc.addAlphaPegPoint(.002f, .005f);
		
		tfunc.addRGBPegPoint(0.002f, .0f, .0f, .9f);
		
		tfunc.addAlphaPegPoint(.01f, .01f);
		
		tfunc.addRGBPegPoint(0.02f, 0.0f, .5f, .9f);
		
		tfunc.addAlphaPegPoint(.02f, .005f);
		tfunc.addAlphaPegPoint(.021f, .0f);
		tfunc.addRGBPegPoint(0.21f, 0.0f, .0f, .0f);
		
		
		
		
		tfunc.addAlphaPegPoint(.039f, 0f);
		tfunc.addAlphaPegPoint(.04f, .005f);
		
		tfunc.addRGBPegPoint(0.04f, .0f, .5f, .9f);
		
		tfunc.addAlphaPegPoint(.055f, .01f);
		
		tfunc.addRGBPegPoint(0.07f, 0.0f, .9f, .0f);
		
		tfunc.addAlphaPegPoint(.07f, .005f);
		tfunc.addAlphaPegPoint(.071f, .0f);
		tfunc.addRGBPegPoint(0.7f, 0.0f, .0f, .0f);
	
		
		tfunc.addAlphaPegPoint(.09f, 0f);
		tfunc.addAlphaPegPoint(.091f, .005f);
		
		tfunc.addRGBPegPoint(0.091f, .9f, .9f, .0f);
		
		tfunc.addAlphaPegPoint(.1f, .01f);
		
		tfunc.addRGBPegPoint(0.15f, 0.9f, .0f, .0f);
		
		tfunc.addAlphaPegPoint(.15f, .005f);
		tfunc.addAlphaPegPoint(.161f, .0f);
		tfunc.addRGBPegPoint(0.16f, 0.0f, .0f, .0f);
		
		
//		tfunc.addAlphaPegPoint(.2f, 0f);
//		tfunc.addAlphaPegPoint(.21f, .005f);
//		
//		tfunc.addRGBPegPoint(0.21f, .9f, .9f, .9f);
//		
//		tfunc.addAlphaPegPoint(.1f, .01f);
//		
//		tfunc.addRGBPegPoint(0.15f, 0.9f, .0f, .0f);
//		
//		tfunc.addAlphaPegPoint(.15f, .005f);
//		tfunc.addAlphaPegPoint(.161f, .0f);
//		tfunc.addRGBPegPoint(0.16f, 0.0f, .0f, .0f);
		
		return tfunc;
		
	}
	
	
	public static TransferFunction me1(){
		TransferFunction tfunc= new TransferFunction(1000);
		
		tfunc.addAlphaPegPoint(.01f,.02f);
		tfunc.addRGBPegPoint(.001f,255,102,0);
		tfunc.addRGBPegPoint(.999f,255,0,0);
		tfunc.addAlphaPegPoint(.99f,0.02f);
		
		
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
