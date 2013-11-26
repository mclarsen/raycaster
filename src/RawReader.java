import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;


public class RawReader {

	
	public static float[] ReadRaw(int bitSize, int xDim, int yDim, int zDim, String filename, boolean isBigEndian){
		float[] data= new float[xDim*yDim*zDim];
		File file = new File(filename);
		System.out.println("FileSize : " +file.length());
		
		InputStream input = null;
		byte[] result = new byte[(int)file.length()];
		
		int totalBytesRead = 0;
        try {
			input = new BufferedInputStream(new FileInputStream(file));
			while(totalBytesRead < result.length){
		          int bytesRemaining = result.length - totalBytesRead;
		          int bytesRead = input.read(result, totalBytesRead, bytesRemaining); 
		          if (bytesRead > 0){
		            totalBytesRead = totalBytesRead + bytesRead;
		          }
		        }
			
		} catch (FileNotFoundException e) {
			System.out.println("File "+filename+" not found");
			e.printStackTrace();
			System.exit(1);
		}
        catch (IOException e){
        	System.out.println("Error occurred during the reading of "+filename);
        	e.printStackTrace();
			System.exit(1);
        }
        finally{
        	try {
				input.close();
			} catch (IOException e) {
				System.out.println("Error occurred during the closing of "+filename);
	        	e.printStackTrace();
				System.exit(1);
			}
        }
        
        if(bitSize==8){
        	int numBytes=xDim*yDim*zDim;
        	int temp=0;
        	for(int i=0;i<numBytes;i++){
        		temp=result[i]&0xff;
        		//if (temp>0) System.out.println(temp);
        		data[i]=temp/255.0f;
        	}
        	
        }
        else if(bitSize==16){
        	int numBytes=xDim*yDim*zDim;
        	byte[] temp=new byte[2];
        
        	for(int i=0;i<numBytes;i++){
        		temp[0]=result[i*2];
        		temp[1]=result[i*2+1];
        		ByteBuffer buffer = ByteBuffer.wrap(temp);
        		if (isBigEndian) buffer.order(ByteOrder.BIG_ENDIAN);  
        		else buffer.order(ByteOrder.LITTLE_ENDIAN);  
        		int t = buffer.getShort();
        		//if (t>0) System.out.println(t);
        		data[i]=t/65535.0f;
        	
        	}
        }
		return data;
	}
}
