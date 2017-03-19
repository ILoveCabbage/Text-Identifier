import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Posting {
	int frequence;
	int did;
	List<Integer> position = new ArrayList<Integer>() ;
	
public static void getPosting(String text, int did, String sortPath) throws FileNotFoundException, IOException{
		
		// discard "." "," and change to lowercase
	    text = text.toLowerCase();
		String new_text = text.replaceAll("[^a-z0-9]"," ") ;
		//System.out.println(new_text);
		String[] words = new_text.split(" ");
		int position = 0 ;
		// find word position, and frequency, put them into a hashmap
		Map<String, Posting> map = new HashMap<>();
	    for (String w : words) {
	    	//w.length()
	    	if ( (w == null) || (w == " ") || (w == "")  || (w.length() ==0) ){
	    		continue;
	    	}
	    	Posting itemN = map.get(w);
	    		    	
	        if(itemN == null){
	        	Posting itemA = new Posting();
	        	itemA.frequence = 1;
	        	itemA.did = did;
	        	itemA.position.add(position);
	        	map.put(w, itemA);  

	        	
	        }
	        else{
	        	itemN.frequence =itemN.frequence +1;
	        	itemN.position.add(position);
	        	map.put(w, itemN);

	        	
	        }
	        position ++ ;
	       
	    }
	    // change it to <string string> hashmap
	    Map<String, String> StringMap = new HashMap<>();
	    for (String w: map.keySet()){
	    	String positionString = "";
	    	for(int a : map.get(w).position){
	    		positionString = positionString + " " + Integer.toString(a);
	    	}
	    	StringMap.put(w, Integer.toString(map.get(w).did)+ " " + Integer.toString(map.get(w).frequence) + positionString );
	    	
	    }
	    
	    // sort it
	    Map<String, String> treeMap = new TreeMap<String, String>(StringMap);
	    
	    // change it into a string
	    String posting = "";
	    for(String key: treeMap.keySet()){
	    	
	    	posting = posting + key + " = " + treeMap.get(key);
	    	posting = posting + System.getProperty("line.separator");
	    }
	    
	  
 
	    
	    // save to file
	    if(posting == null){
	    	System.out.println("null posting");
	    	
	    }else{
	    	String file_name =sortPath  + "/"+ Integer.toString(did);
		    BufferedWriter writer = null;
		    try
		    {
		        writer = new BufferedWriter( new FileWriter(file_name,true));
		        writer.write( posting);

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
		
    
    

	    // clear
	    map.clear();
	    StringMap.clear();
	    
	   new_text = null;
	   words = null;
	   
		
	}

}
