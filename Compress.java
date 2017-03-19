import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;



public class Compress {
	/*
	 * input: fileName ------- name of inverted index
	 * 	      metaLexicon ---- lexicon for search, put it into main memory
	 *        binaryFile ----- binaryFile to store 
	 *        metaTrunk ------ metadata file of trunk
	 *        numberInTrunk -- how many number in one trunk, usually 128
	 */
	public static void compress(String fileName, String metaLexicon, String binaryFile, int numberInTrunk) throws IOException{
		// set buffer size
		BufferedReader fbr = new BufferedReader(new FileReader(fileName));
        long blocksize =  (long) Math.pow(2, 20) ;// in bytes
        long sizeInBinaryFile = 0;
        long offsetInBinaryFile = 0;
        String everything = null;
		String line = "";
		try {
		    while(line != null) {
		    	StringBuilder fsb = new StringBuilder();
		        long currentblocksize = 0;// in bytes
		        // read part of file
		        line = fbr.readLine();
		        // as long as you have 10MB
		        while((currentblocksize < blocksize) &&(   (line != null) )){ 
		        	fsb.append(line);
				 	fsb.append(System.lineSeparator());
					line = fbr.readLine();
					try{
						currentblocksize = currentblocksize + line.length(); // 2 + 40; // java uses 16 bits per character + 40 bytes of overhead (estimated)
					}
					catch(Exception e){
						//e.printStackTrace();
					}
		        }
		        everything = fsb.toString();
		        fsb.setLength(0);
		       
		      
		        // get inverted index of each word
		        String[] eachIndex = everything.split(System.getProperty("line.separator"));
		      
		        // compress each index and get Id number of each word
		        int wordSize = 0;
		        String word =null;
		        StringBuilder sb = new StringBuilder();
		                            
		        for(int a = 1; a < eachIndex.length; a++){ 
		            if (eachIndex[a].length()==0){
		        		continue;
		        	}
		        	word = eachIndex[a].split(" = ")[0];
		        	String content = eachIndex[a].split(" = ")[1];
		        	//System.out.println("start One word " + System.nanoTime());
		        	sizeInBinaryFile = Compress.compress_one_word(numberInTrunk, offsetInBinaryFile,content, binaryFile,metaLexicon);
		        	//System.out.println("end One word   " + System.nanoTime() + "\r\n");
		        	
		        	String info = word + " " + sizeInBinaryFile + " " + offsetInBinaryFile + ";" ;
		        	sb.append(info);
		        	offsetInBinaryFile += sizeInBinaryFile;  
		        	
		        }
		        
		        // write metadata of word into file
		        String contentLexicon = sb.toString();
		        sb.setLength(0);
		        BufferedWriter writerL= null;
		        writerL = new BufferedWriter( new FileWriter(metaLexicon,true));
			    writerL.write(contentLexicon);
			    writerL.close();
			    //System.out.println("finish " + eachIndex.length + " words");
			    //System.out.println(System.nanoTime() + "\r\n");
		    }
			
      }
      catch ( IOException e)
   {
		e.printStackTrace();
   }
}
		
	
	/*
	 * input: numberInTrunk---how many document ID to merge in one trunk
	 * 	      c --------------content of inverted index of one word, like "Did freq; Did freq ...."
	 *        binaryFile------binary file path
	 *        metaFile -------metadata file path
	 *        offsetInBinaryFile 
	 *        
	 * return: size of this word in binary file      
	 * 
	 * function: save inverted index of one word into binary file and create its trunk information
	 */
	public static long compress_one_word(int numberInTrunk ,long offsetInBinaryFile, String c, String binaryFile, String metaFile) throws IOException{
		String[] content= c.split("; ");
		// in one inverted index , separate them into "mark" trunks, and compress them  
		int offset = 0;
		long wordSize = 0 ;
		List<Integer> compressPosition = new ArrayList<Integer>();
		
		//int trunkNumber = (int)Math.ceil( ((double)content.length)   /  ((double)numberInTrunk)) ;
		while(content.length - offset  > 0 ){
			int arrayLength = Math.min(numberInTrunk, (content.length-offset));
			int[] compressDid = new int[arrayLength];
			int[] compressFreq = new int[arrayLength];
			
			// get one trunk data : ID, frequency, position
			for(int a = 0 ; a < arrayLength ; a++){
				if(a+ offset >= content.length ){
					break;
				}
				
				//System.out.println(content[a + offset].split(" ")[0]);
				compressDid[a] =Integer.parseInt(content[a + offset].split(" ")[0].trim());
				//System.out.println("a="+ a + " value" + compressDid[a]);
				compressFreq[a] =Integer.parseInt(content[a + offset].split(" ")[1].trim());
				String aa = content[a + offset].trim().split(" " , 3)[2];
				for(int b = 0; b < aa.split(" ").length ; b++){
					compressPosition.add( Integer.parseInt( aa.split(" ")[b].trim() ) );
				}
				
				
				
			} 
			// calculate all trunk size 
			//System.out.println("start One trunk" + System.nanoTime());
        	
			wordSize += (Compress.trunk_Compress_and_Metadata(compressDid, compressFreq, compressPosition, binaryFile));
			//System.out.println("end One chunk  " + System.nanoTime());
        	
			offset = offset + numberInTrunk;
		}
		
		return wordSize;
		
	}
	
//	/*
//	 * function to save trunk metadata into a file
//	 */
//	public static long meta_save(metadata[] md, String fileName) throws IOException{
//		String content = null;
//		for(int a=0; a <md.length; a++ ){
//			String mdContent = null;
//			mdContent = (md[a].lastId) + " " + Long.toString(md[a].size) + " " + Long.toString(md[a].offset); 
//			mdContent = mdContent + System.lineSeparator();
//			if(a==0){
//				content = mdContent;
//			}
//			else{
//				content = content + mdContent;
//			}
//		}
//		BufferedWriter writer = null;
//	    writer = new BufferedWriter( new FileWriter(fileName,true));
//	    writer.write(content);
//	    writer.close();
//	    return content.length();        
//	}
	
	/*
	 * input: number ------- content needed to be saved as binary file
	 * fileName ------------ binary file name
	 * freq ---------------- frequency in id
	 * 
	 * return: size of this trunk
	 * 
	 * function: save content as binary file and return its metadata
	 */
	public static long trunk_Compress_and_Metadata(int[] number, int[] freq , List<Integer> position,String fileName) throws IOException{
		long trunkSizeByte = 0;
		int lastId = number[number.length-1];
		
		List<Integer> differNumber = new ArrayList<Integer>(number.length);
		List<Integer> differPosition = new ArrayList<Integer>();
		
		// take differences
		for (int a=0 ; a < number.length; a++ ){
			if (a ==0){
				differNumber.add(number[a]);
			}
			else{
				differNumber.add( number[a] - number[a-1]);	
			}
		}
		for (int a=0 ; a < freq.length; a++ ){
			differNumber.add( freq[a]);
		}
		for(int a : position){
			differNumber.add(a);
		}
		
		
		// write trunk meta and binary files
		byte[] binaryNumber = Compress.encode(differNumber);
		trunkSizeByte = binaryNumber.length;
		//System.out.println("size: " + trunkSizeByte + " last: " + lastId  );
		
		byte[] size = ByteUtils.longToBytes(trunkSizeByte);
		byte[] last = ByteUtils.longToBytes(lastId);
		
		
		OutputStream outputStream = new FileOutputStream(fileName,true);
		outputStream.write(last);
		outputStream.write(size);
		outputStream.write(binaryNumber);
		outputStream.close();
		//outputStreamWriter.close();
		
		
		// return trunksize with meta infomation
		return (trunkSizeByte + 16) ;
		
	}
	
	

	/*
	 * decompress binary file to document id and frequency
	 */
	
	public static void decompress_chunk(String sourceFile, long offset, long size, List<Integer> result) throws IOException{
	    
	    RandomAccessFile file = new RandomAccessFile(sourceFile, "r");
		//read data
		//System.out.println("read trunk");
	    byte[] data = new byte[(int) (size) ];
		//offset += 16;
		
		file.seek(offset);
		file.read(data);
		
	    List<Integer> decodedArray =  Compress.decode(data);
	    for(Integer i : decodedArray){
	    	result.add(i);
	    }
//	    System.out.println("fileData " );
//	    for(int a = 0 ; a < fileData.length;a++){
//	    	System.out.print(fileData[a] + " ");
//	    }
//	    System.out.println("data ");
//	    for(int a = 0 ; a < data.length;a++){
//	    	System.out.print(data[a] + " ");
//	    }
	    
//		System.out.println("decoded");
//		for(int i : decodedArray){
//			System.out.print(i + " ");
//		}
		
		
	}
	
	
	
	
//	static int mask8bit = (1 << 8) - 1;
	
	
	private static void innerEncode(int num, List<Byte> resultList) {
		int headNum = resultList.size();
		while (true) {
			byte n = (byte) (num % 128);
			resultList.add(headNum, n);
			if (num < 128)
				break;
			num = num >>> 7;
		}

		int lastIndex = resultList.size() - 1;
		Byte val = resultList.get(lastIndex);
		val = (byte) (val.byteValue() - 128);
		resultList.remove(lastIndex);
		resultList.add(val);
	}

	
	public static byte[] encode(List<Integer> list) {
		List<Byte> resultList = new ArrayList<Byte>();
		for (Integer num:list) {
			innerEncode(num.intValue() , resultList);
		}
		int listNum = resultList.size();
		byte[] resultArray = new byte[listNum ];
		int num = list.size();

//		resultArray[0] = (byte) ((num >> 24) & mask8bit);
//		resultArray[1] = (byte) ((num >> 16) & mask8bit);
//		resultArray[2] = (byte) ((num >> 8) & mask8bit);
//		resultArray[3] = (byte) (num & mask8bit);

		for (int i = 0; i < listNum; i++)
			resultArray[i] = resultList.get(i);

		return resultArray;
	}

	
	public static List<Integer> decode(byte[] encodedArray) {
		ArrayList<Integer> decodedArray = new ArrayList<Integer>();
		int n = 0;
		for (int i = 0; i < encodedArray.length; i++) {

			if (0 <= encodedArray[i])
				n = (n << 7) + encodedArray[i];
			else {
				n = (n << 7) + (encodedArray[i] + 128);
				decodedArray.add(n);
				n = 0;
			}
		}
		return decodedArray;

	}
	
	
	
	
	
	
	
	
	
	
}
