import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;


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
        else if(bitSize==32){
        	System.out.println("32 bit");
        	int numBytes=xDim*yDim*zDim;
        	byte[] temp=new byte[4];
        
        	for(int i=0;i<numBytes;i++){
        		temp[0]=result[i*2];
        		temp[1]=result[i*2+1];
        		temp[2]=result[i*2+2];
        		temp[3]=result[i*2+3];
        		ByteBuffer buffer = ByteBuffer.wrap(temp);
        		if (isBigEndian)buffer=buffer.order(ByteOrder.BIG_ENDIAN);  
        		else buffer=buffer.order(ByteOrder.LITTLE_ENDIAN);  
        		float t = buffer.getFloat();
        		 System.out.println(t);
        		data[i]=t;/// 2147483647.0f;
        		byte[] ba={temp[3],temp[2],temp[1],temp[0]};
        		DataInputStream dis = new DataInputStream(new 
        				ByteArrayInputStream(ba)); 
        				try {
							float x = dis.readFloat();
							System.out.println("New method: "+ x);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 
        	
        	}
        }
		return data;
	}
	
	public static float[] readAscii(int xDim, int yDim, int zDim, String filename){
		int size=xDim*yDim*zDim;
		float[] floatData= new float[size];
		
		Path path = Paths.get(filename);
	    int line=0;
	    int i=0;
	    String lineData="";
	    String[] data;
	    Scanner scanner;
		try {
			scanner = new Scanner(path);
			 while (scanner.hasNextLine()){
			        //process each line in some way
			    	 // System.out.println("Processing Line : "+ line);
			    	  lineData=scanner.nextLine();
			  
			    	  data=lineData.split(" ");
			    		  for(int j=0;j<data.length;j++){
			    			 floatData[i]=Float.parseFloat(data[j]);
			    			 
			    			 i++;
			    			 if(i==16777216){
			    				 System.out.print("here");
			    			 }
			    		  }
			    	
			    	  line++;
			    }
			 scanner.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	     
		
		
		
		return floatData;
		
	}
}
