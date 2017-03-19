import java.io.BufferedReader;


import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class InvertedIndex {
	public static void CreateInvertedIndex(String path, String destination, Comparator<String> cmp) throws IOException {
		// set buffer size
		System.out.println("starting inverted index");
		BufferedReader fbr = new BufferedReader(new FileReader(path));
        long blocksize =  (long) Math.pow(2, 20) ;// in bytes
        try{
        	List<String> tmplist =  new ArrayList<String>();
        	String everything = null;
            String line = "";
            try {
                while(line != null) {
                	StringBuilder fsb = new StringBuilder();
                    long currentblocksize = 0;// in bytes
                    // read part of file
                    line = fbr.readLine();
                    while((currentblocksize < blocksize) 
                    &&(   (line != null) )){ // as long as you have 10MB
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
                    //tmplist.add( sb.toString() );
                    everything = fsb.toString();
                    //System.out.println(currentblocksize);
                
            		// same word in one line
            		String mergeString = null; 
            		String lexicon = null;
            		String[] eachLine = everything.split(System.getProperty("line.separator"));
            		StringBuilder sb = new StringBuilder();
            		StringBuilder sbLexicon = new StringBuilder();
            		sb.append(System.lineSeparator());
            		sb.append(eachLine[0]);
            		for (int i = 1; i < eachLine.length -1 ; i++){
            			if ( eachLine[i].split("=")[0].compareTo( eachLine[i-1].split("=")[0]) <0)
            				continue;
            			
            			if ( eachLine[i].split("=")[0].compareTo( eachLine[i-1].split("=")[0]) ==0){
            				sb.append(";" + eachLine[i].split("=")[1]);
            				
            			}
            			else {
            				sb.append(System.lineSeparator());
            				sb.append(eachLine[i]);
            				sbLexicon.append(eachLine[i].split("=")[0]);
            				sbLexicon.append(System.lineSeparator());
            			}
            		}
            		mergeString = sb.toString();
            		//System.out.println(mergeString);
            		lexicon = sbLexicon.toString();
            			
            		// save inverted file
            		String file_name =destination;
            	    BufferedWriter writer = null;
            	    try
            	    {
            	        writer = new BufferedWriter( new FileWriter(file_name,true));
            	        
            	        writer.write( mergeString);

            	    }
            	    catch ( IOException e)
            	    {
            	    	e.printStackTrace();
            	    }
            	    finally
            	    {
            	        try
            	        {
            	            if ( writer != null)
            	            writer.close( );
            	        }
            	        catch ( IOException e)
            	        {
            	        	e.printStackTrace();
            	        }
            	    }
                    

            	 
            	    
            	    
                
                }
            } catch(EOFException oef) {
                if(everything.length()>0) {
                   everything = null;
                }
            }
        } finally {
            fbr.close();
        }
		

	    System.out.println("end inverted index");
	}

}
