
public class TransferFunctionFactory {

	public static TransferFunction getEngine1(){
		TransferFunction tfunc= new TransferFunction(1000);
		
		float[] colorBegin= new float[4];
		float[] colorEnd= new float[4];
		colorBegin[0]=1.0f;
		colorBegin[1]=0.0f;
		colorBegin[2]=0.0f;
		colorBegin[3]=.3f; 
		colorEnd[0]=0.0f;
		colorEnd[1]=0f;
		colorEnd[2]=0.0f;
		colorEnd[3]=.2f;
		tfunc.addPegPoint(.298f, .447f,colorBegin.clone(),colorEnd.clone());
		colorBegin[0]=0.0f;
		colorBegin[1]=0.0f;
		colorBegin[2]=1.0f;
		colorBegin[3]=.5f; 
		colorEnd[0]=0.0f;
		colorEnd[1]=102/255.0f;
		colorEnd[2]=1.0f;
		colorEnd[3]=.5f;
		tfunc.addPegPoint(.67f, .766f,colorBegin.clone(),colorEnd.clone());
		
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
		
	}
}
