import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.text.AbstractDocument.Content;

public class Parse {
	public static int[] separate_page(String fileName, String destinationPath, String urlLexiconPath) throws IOException{
		System.out.println("start parsing" + fileName);
		
		// create folder
		File f = new File(destinationPath);
		f.mkdir();
		
		
		// set buffer size
		BufferedReader fbr = new BufferedReader(new FileReader(fileName));
		String everything = null;
		String line = null;
		int aa = 0;
		int did =0;
		int averageLength = 0;
		int numberOfDocuments = 0;
		while(((line = fbr.readLine()) != null)  ) {
			//System.out.println("start");
			//System.out.println(line);
			if (line.length() ==0){
				continue;
		    }
		    if (line.startsWith("WARC-Type: conversion")){
		    	//get url 
		    	line = fbr.readLine();
	    		
		    	if (line == null){
		    		break;
		    	}
		    	if (!line.startsWith("WARC-Target-URI"))
		    		continue;
		    	String url = line.split(":",2)[1].trim();
		    	
		    	//get length of content
		    	line =  fbr.readLine();
		    	while(line != null && !line.startsWith("Content-Length")){
		    		line = fbr.readLine();
		    	}
		    	if (line == null)
		    		break;
		    	//System.out.println(line);
		    	int contentLength = Integer.parseInt(line.split(":")[1].trim());
		    	//System.out.println(contentLength);
		    	// get content of page 
		    	char[] contents = new char[contentLength];
		    	int offset = 0;
		    	try{
		    		while(offset < contentLength){
		    			int tmp = fbr.read(contents,offset,contentLength-offset);
		    			offset += tmp;
		    		}
		    	} catch (Exception e){
		    		e.printStackTrace();
		    	}
		    		
		    	StringBuilder sb = new StringBuilder();
			    for (int a=0 ; a < contents.length; a++){
			    	sb.append(contents[a]);
			    }
			    everything = sb.toString();
			    
			    //create posting file of each page and save them in the folder, and url 
			    
			    if(everything == null)
			    	continue;
			    try{
			    	//creating url lexicon 
			    	BufferedWriter writer = null;
				    String content = did +  " " + url + " " +contentLength +"\r\n";
				    writer = new BufferedWriter( new FileWriter(urlLexiconPath,true));
				    writer.write(content);
				    writer.close( );
				    // write posting
				    Posting.getPosting(everything, did, destinationPath);
			    	numberOfDocuments++;
				}catch (Exception e){
					e.printStackTrace();
				}
			    did ++;
			    averageLength = (averageLength + contentLength)/2 ; 
			    
		    }

		}
		int[] a = {averageLength , numberOfDocuments};
		System.out.println("end parsing" + fileName);
		
		return a;
		
		

	}
		    
		

}
