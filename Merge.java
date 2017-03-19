import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;


public class Merge {

	public static int mergeSortedFiles(List<File> files, File outputfile, final Comparator<String> cmp) throws IOException {
		
		PriorityQueue<BinaryFileBuffer> pq = new PriorityQueue<BinaryFileBuffer>(20, 
	            new Comparator<BinaryFileBuffer>() {
	              public int compare(BinaryFileBuffer i, BinaryFileBuffer j) {
//	            	  String a =""; 
//	            	  String b = "";
//	            	  try{
//	            		  a = i.peek().split("=")[0];
//	            	  }
//	            	  catch(Exception e){
//	            		  a = null;
//	            	  }
//	            	  try{
//	            		  b = i.peek().split("=")[0];
//	            	  }
//	            	  catch(Exception e){
//	            		  b = null;
//	            	  }
	            	  
		            	  if ((cmp.compare(i.peek().split("=")[0], j.peek().split("=")[0])) == 0){
		            		  String iString = i.peek().split("=")[1];
		            		  String jString = j.peek().split("=")[1];
		            		  int result = (Integer.parseInt(iString.split(" ")[1])- Integer.parseInt(jString.split(" ")[1]));
		            		  //int result = cmp.compare(iString.split(" ")[1], (jString.split(" ")[1]));
		            		  //System.out.print("This is return " + result);
		            		  
		            		  return (result);
		            	  }
		            	  else{
		            		  return cmp.compare(i.peek().split("=")[0], j.peek().split("=")[0]);
		            	  }
	            	  
	            	  
	            	 // return cmp.compare(i.peek().split("=")[0], j.peek().split("=")[0]);
	            	  
	              }
	            }
	        );
		
				
		
        for (File f : files) {
        	if(f.length() ==0){
        		continue;
        	}
        	BinaryFileBuffer bfb = new BinaryFileBuffer(f);
        	pq.add(bfb);
        	
        	
//            try{
//            	BinaryFileBuffer bfb = new BinaryFileBuffer(f);
//            	if ( bfb.empty())
//                	continue;
//            	bfb.pop();
//                pq.add(bfb);
//            }catch(Exception e){
//            	e.printStackTrace();
//            }
        }
        
        BufferedWriter fbw = new BufferedWriter(new FileWriter(outputfile));
        int rowcounter = 0;
        try {
            while(pq.size()>0) {
                BinaryFileBuffer bfb = pq.poll();
                String r = bfb.pop();
                fbw.write(r);
                //System.out.println(r);
                fbw.newLine();
                ++rowcounter;
                if(bfb.empty()) {
                    bfb.fbr.close();
                  //  System.out.println("delete " + bfb.originalfile.getName());
                  // bfb.originalfile.delete();
                    
                } else {
                    pq.add(bfb); // add it back
                }
            }
        } finally { 
            fbw.close();
            for(BinaryFileBuffer bfb : pq ) {
            	bfb.close();
            }
            
        }
        return rowcounter;
	}
	
	public static void Merge(String sourcePath, String destinationFile, final Comparator<String> cmp,int flag) throws IOException{
		System.out.println("start merging");
		
		
		//create temperate folder for merge
		flag++;
	    String tmpPath = "D:/tempForMerge"+ flag;
		File f = new File(tmpPath);
		boolean bool = f.mkdir();
	    		
		// get filename of all posting files
		String[] postingFileName = new File(sourcePath).list();
		System.out.println("length "+postingFileName.length);
		
		List<File> fileList = new ArrayList<File>();
		
		// merge them when the file number is less than 400
		if (postingFileName.length < 400 ){
			for(int a=0; a < postingFileName.length; a++){
				fileList.add(new File(sourcePath + "/" + postingFileName[a]));
	        	
				}
			File destFile = new File(destinationFile);
			
			Merge.mergeSortedFiles(fileList, destFile, cmp);
			
		}else{ // if larger than 400, merge them into tmp file and merge tmp file again
			int offset = 0;
			//sourcePath = sourcePath + flag;
			while(offset < postingFileName.length ){
				fileList.clear();
				for(int a=0; a< 200; a++){
					if ((a+offset) >= postingFileName.length){
						break;
					}
					fileList.add(new File(sourcePath + "/" +postingFileName[a+ offset]));
				}
				File tmpFile = new File(tmpPath + "/" + offset);
				Merge.mergeSortedFiles(fileList, tmpFile, cmp);
				System.out.println("finish merge" + offset);
				
				offset += 200;
			}
			Merge.Merge(tmpPath, destinationFile, cmp, flag);
			
		}
		
		System.out.println("end merging" );
		
	}

	

 
   
}
	
	
	



