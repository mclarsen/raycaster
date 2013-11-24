
public class EngineTransferFunction1 implements TransferFunction{
	
	private int size=1000;
	
	public EngineTransferFunction1(){
		
	}
	@Override
	public float[] getAlphaArray(){
		float[] data=new float[1];
		return data;
	}

	@Override
	public float[] getColorArray() {
		
		return null;
	}
	
	private class pegPoint{
		public float scalarBegin=0f;
		public float scalarEnd=1f;
		public float alha=1f;
		public float[] colorBegin= new float[3];
		public float[] colorEnd=new float[3];
	}

}
