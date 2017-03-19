import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Query {
	/*
	 * get chunk information first lastID, second size
	 */
	public static long[] chunkInfo(String binaryFile, long offset ) throws IOException{
		//read data
		RandomAccessFile file = new RandomAccessFile(binaryFile, "r");
		//System.out.println("read trunk");
		byte[] fileSize = new byte[8];
		byte[] fileLast = new byte[8];
		file.seek(offset);
		file.read(fileLast);
		file.read(fileSize);
		file.close();
		long[] chunkInfo = new long[2];
		chunkInfo[0] = ByteUtils.bytesToLong(fileLast);
		//System.out.println(chunkInfo[0]);
		chunkInfo[1] = ByteUtils.bytesToLong(fileSize);
		
		return chunkInfo;

	}
	
	public static void getDulplicate(String query, String binaryFile, List<String> wordLexicon, List<Long> offsetLexicon, List<Long> sizeLexicon, Map<String, String> urlLexicon) throws IOException {
		// discard "." "," and change to lowercase
	    query = query.toLowerCase();
		query = query.replaceAll("[^a-z0-9]"," ") ;
		String[] word = query.split(" ");
		List<String> wordList = new ArrayList<String>();
		for(int a = 0 ; a <word.length ; a++){
			if ( word[a].length() != 0 ) {
				wordList.add(word[a]);
			}
		}
		
		List<Integer> documentId = new ArrayList<Integer>();
		List<Integer>  freq = new ArrayList<Integer>();
		List<Integer> position =  new ArrayList<Integer>();
		String w1 = wordList.get(0);
		int singleFlag = Query.search_single_word(w1, binaryFile, 128, wordLexicon, offsetLexicon, sizeLexicon, documentId, freq, position);
		if(singleFlag >= 0){
		
		//System.out.println("word " + w1 + " id " + documentId);
//		for(int id : documentId){
//			System.out.print(id + " ");
//		}
//		System.out.println("\r\n");
		System.out.println(query);
		
		for(int a = 1 ; a < wordList.size() ; a ++) {
			String wn = wordList.get(a);
			List<Integer> result = new ArrayList<Integer>();
			List<Integer> documentIdN = new ArrayList<Integer>();
			List<Integer>  freqN = new ArrayList<Integer>();
			List<Integer> positionN =  new ArrayList<Integer>();
			
			List<Integer> documentIdNew = new ArrayList<Integer>();
			List<Integer>  freqNew = new ArrayList<Integer>();
			List<Integer> positionNew =  new ArrayList<Integer>();
			
		
			System.out.println("word " + wn);
			
			
			int index = wordLexicon.indexOf(wn);
			long offsetn = offsetLexicon.get(index);
			long wordSize = sizeLexicon.get(index);
			long offsetChunk = 0;
			//System.out.println("offsetChunk" + offsetChunk);
			int indexId = 0;
			//System.out.println("Size of this word  " + wordSize + " offset of this word " + offsetn );
			while(indexId <  documentId.size()){
				
				int id = documentId.get(indexId);
				//System.out.println("this is id: " + id);
				//System.out.println("indexId " + indexId + " documentId.size " + documentId.size());
				while(offsetChunk < wordSize){
					
					System.out.println("get chunk info offset " + (offsetn + offsetChunk) );
					long[] wnInfo = Query.chunkInfo(binaryFile, (offsetn + offsetChunk));
					System.out.println("chunk size   " + wnInfo[1]);
					System.out.println("chunk lastId " + wnInfo[0]);
					offsetChunk += 16;
					//System.out.println("offsetChunk "+ offsetChunk);
					//System.out.println("id " + id + " wn last ID " + wnInfo[0]);
					
					
					
					if (id <= wnInfo[0]) {
						System.out.println("decompress offset " + (offsetn +offsetChunk) + " size " + wnInfo[1]);
						Compress.decompress_chunk(binaryFile, (offsetn + offsetChunk), wnInfo[1], result); //maybe in that chunk, decompress it
						//offsetChunk += wnInfo[1];
						int indexIdLimit = indexId;
						while(indexIdLimit < documentId.size() ){                  // get other same ids in that block out of the list 
							if(documentId.get(indexIdLimit) < wnInfo[0]){
								indexIdLimit ++;
							}
							else{
								break;
							}
						}
						//System.out.println("limit offset" + indexIdLimit );//+ " value" + documentId.get(indexIdLimit));
						//System.out.println("chunkOff " + offsetChunk + " chunkSize " + wnInfo[1] + " wordSize " + wordSize); 
						
						if( offsetChunk + wnInfo[1] >= wordSize ){  // convert number to id frequency position
							Query.convertNotFullResultToIdFreqPosition(result, documentIdN, freqN, positionN, (int)wnInfo[0]);
							System.out.println("full");
						}
						else {
							Query.convertFullResultToIdFreqPosition(result, documentIdN, freqN, positionN);
							System.out.println("not full");
						}
						

						for(int i = indexId ; i < indexIdLimit ;  i++){
							id = documentId.get(i);
							if(documentIdN.indexOf(id) != -1){ // there is document has both words, so check position
								//System.out.println("two words in same id " + id);
								List<Integer> keyPositionN = new ArrayList<Integer>();
								List<Integer> keyPosition = new ArrayList<Integer>();
								Query.findPosition(documentIdN, freqN, positionN, id, keyPositionN);
								//Query.findPosition(documentId, freq, position, id, keyPosition);
								int flag = 0;
								for(int pN : keyPositionN){
									if (keyPosition.indexOf( pN-1) >= 0 ){
										//System.out.println("same position same id");
										positionNew.add(pN) ;
										flag = 1 ;
									}
								}
								if(flag ==1){
									documentIdNew.add(id);
									freqNew.add( keyPosition.size() );
								}
									
							}
							
						}
						indexId = indexIdLimit-1;
						
						//break;
					}
					
					offsetChunk += wnInfo[1];
				
					//System.out.println("add it");
//					System.out.println("offsetChunk "+ offsetChunk);
//					System.out.println("chunk size " + wnInfo[1]);
					
					
					//System.out.println( "offsetChunk "+ offsetChunk);
					
					
				}
				indexId ++;
			}
			// change documentIdNew, freqNew, positionNew to documentId, freq, position
			documentId.clear();
			freq.clear();
			position.clear();
			for(int id : documentIdNew ){
				documentId.add(id);
			}
			for(int f : freqNew ){
				freq.add(f);
			}
			for(int p : positionNew ){
				position.add(p);
			}
			
			if (documentId.size() ==0 ){
				System.out.println("There is nothing dulplicate in this sentence." );
				break;
			}
			
		}
		
		if(documentId.size() != 0){
			System.out.println("This sentence dulplicate in document:");
			for(int id : documentId){
				String url = urlLexicon.get( Integer.toString(id) );
				url = url.split(" ")[0].trim();
				System.out.println("document ID " + id + ": " + url);
				
			}
		}
		}
		
	}
	
	public static int search_single_word(String word, String binaryFile, int numberInTrunk, List<String> wordList, List<Long> offsetList, List<Long> sizeList, List<Integer> id, List<Integer> freq, List<Integer> position) throws IOException{
		
		//System.out.println("Start search " + word);
		// find the word's trunk number and offset
		int index = wordList.indexOf(word);
		if(index >= 0){
		
		long longSize = sizeList.get(index);
		int metaSize = (int) longSize;
		long metaOffset = offsetList.get(index);
		
		//List<Integer> result = new ArrayList<Integer>();
		//System.out.println("get lexicon information of \"" + word + "\"");
		//System.out.println("Size: " + metaSize + " Offset: " + metaOffset);
		int wordOffset = 0;
		while ( wordOffset < metaSize){
			long[] info = Query.chunkInfo(binaryFile, (metaOffset + wordOffset));
			wordOffset += 16;
			List<Integer> result = new ArrayList<Integer>();
			//System.out.println("size " + info[1] + " offset " + (metaOffset + wordOffset));
			
			Compress.decompress_chunk(binaryFile, (metaOffset + wordOffset), info[1], result);
			//System.out.println("result" + result);
//			System.out.println("size " + info[1]);
//			System.out.println("wordOff " + wordOffset + " metaOff " + metaOffset + " metaSize " + metaSize);
//			System.out.println (info[1] + metaOffset  );
			if(info[1] + wordOffset < metaSize ){
				Query.convertFullResultToIdFreqPosition(result, id, freq, position);
			}
			else{
				Query.convertNotFullResultToIdFreqPosition(result, id, freq, position, (int)info[0]);
				
			}
			wordOffset += info[1]; 
			//System.out.println("wordOffset " + wordOffset + "metaSize" + metaSize);
			
		}
		}
		else{
			System.out.println("can not find word: " + word);
		}
		return index;
	}
	
	/*
	 * 
	 */
	public static void convertFullResultToIdFreqPosition( List<Integer> result,List<Integer> id, List<Integer> freq, List<Integer> position){
		for(int a = 0; a < 128 ; a++ ){
			if(a ==0 ){
				id.add( result.get(a) );
			}
			else{
				id.add( id.get(id.size()-1) + result.get(a) ) ;
			}
		}
		for(int a = 128 ; a < 128 *2 ; a++){
			freq.add( result.get(a) );
		}
		for(int a = 128*2 ; a < result.size() ; a++){
			position.add( result.get(a) );
		}
	}
	
	public static void convertNotFullResultToIdFreqPosition( List<Integer> result, List<Integer> id, List<Integer> freq, List<Integer> position, int lastId){
		
		int documentId = result.get(0);
		int number = 1 ;  // find how many document id in the last trunk
		while(documentId < lastId ){
			documentId += result.get(number);
			number ++;
		}
		for(int a = 0; a < number ; a++ ){
			if(a ==0 ){
				id.add( result.get(a) );
			}
			else{
				id.add( id.get(id.size()-1) + result.get(a) ) ;
			}
		}
		for(int a = number ; a < number *2 ; a++){
			freq.add( result.get(a) );
		}
		for(int a = number*2 ; a < result.size() ; a++){
			position.add( result.get(a) );
		}
	}
	
	/*
	 * return: 1  if find position, -1 if key ID is not in id list
	 */
	public static int findPosition(List<Integer> id, List<Integer> freq, List<Integer> position, int keyId, List<Integer> keyPosition){
		int returnValue = 1;
		int index = id.indexOf(keyId);
		
		//System.out.println("keyId " + keyId  + " position size " + position.size());
		
		if ( index < 0 ){
			returnValue = -1;
		}
		else{
			int offsetP = 0;
			for (int a =0 ; a < index ; a ++){
				offsetP += freq.get(a);
				
			}
			
			//System.out.println( + " how many positions "  + freq.get(index));
			
			for (int a = 0 ; a < freq.get(index); a++){
				keyPosition.add(position.get(a + offsetP));
			}
		}
		
		return returnValue;
	}
	
	/*
	 * function: put LexiconUrl and Lexicon into main memory
	 * input: wordList, offsetList, sizeList ---- empty arraylist to store each word, its offset and size
	 * 		  LexiconUrl( MAP) ------------------ hashmap to store each document id and it url 
	 * 		 metaLexicon, urlLexicon ------------ name of lexicon file
	 */
	public static void read_in_memory(String metaLexicon, String urlLexicon, List<String> wordList, List<Long> offsetList, List<Long> sizeList ,Map<String, String> lexiconUrl) throws IOException{
		
		BufferedReader bfUrl = new BufferedReader(new FileReader(urlLexicon)); // read in url lexicon
		String lineUrl = bfUrl.readLine();
		while(lineUrl.length() != 0) {			// read lexicon into memory
			String key = lineUrl.split(" ")[0].trim();  // key is id
			String value = (lineUrl.split(" ",2)[1].trim());  // value is url
			//System.out.println(key + " " + value);
			lexiconUrl.put(key, value);
			lineUrl = bfUrl.readLine();
			if(lineUrl ==null)
				break;
		}
		bfUrl.close();
		System.out.println("finish put url lexicon to memory");
		
		BufferedReader bf = new BufferedReader(new FileReader(metaLexicon)); // read in word lexicon
		String line = bf.readLine();
		bf.close();
		String[] metaInfo = line.split(";"); 			
		for(int a =0 ; a < metaInfo.length; a++){
			wordList.add(metaInfo[a].split(" ")[0].trim());  // key is word
			offsetList.add( Long.parseLong(metaInfo[a].split(" ")[2].trim()));  // value is number of trunks and offset
			sizeList.add( Long.parseLong(metaInfo[a].split(" ")[1].trim()));
		}
		System.out.println("finish put word lexicon to memory");
		
		
	}

}
