import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.OutputStreamWriter;
import java.io.Writer;


public class main {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
//		System.out.println("hello");
//		String fileName = "";
//		String metaLexicon = "";
//		String binaryFile = "";
//		int numberInTrunk = 128;
//		Compress.compress(fileName, metaLexicon, binaryFile, numberInTrunk);
		
		String fileName = "CC-MAIN-20160924173739-00000-ip-10-143-35-109.ec2.internal.warc.wet";
		String parsePath = "testParse";
		String urlLexiconPath = "testUrl";
		String mergeFile = "testMerge";
		String invertedFile = "testInverted";
		String metaLexicon = "testMeta";
		String binaryFile = "testBinary";
		File outputFile = new File("mergeOut");
		
		List<Long> offsetLexicon = new ArrayList<Long>();  // variables to put lexicon in them
		List<Long> sizeLexicon = new ArrayList<Long>();
		List<String> wordLexicon = new ArrayList<String>();
		Map<String, String> lexiconUrl = new HashMap<String, String>();
		
		// define comparator
		Comparator<String> cmp = new Comparator<String>() {
		     public int compare(String r1, String r2){
		         return r1.compareTo(r2);
		     }
		};

		
//		Merge.Merge(parsePath, mergeFile, cmp, 0);
//		mergeFile = "0";
//		invertedFile = "zero"; 
//		binaryFile = "zeroB";
//		metaLexicon = "zeroM";
//		InvertedIndex.CreateInvertedIndex(mergeFile, invertedFile, cmp);
//		Compress.compress(invertedFile, metaLexicon, binaryFile, 128);
		
//		Query.read_in_memory(metaLexicon, urlLexiconPath, wordLexicon, offsetLexicon, sizeLexicon, lexiconUrl);
		
//		long[] info = Query.chunkInfo(binaryFile, 0);
//		System.out.println("last Id" + info[0]);
//		System.out.println("size" + info[1]);
		List<Integer> result = new ArrayList<Integer>();
//		Compress.decompress_chunk(binaryFile, 173, 5, result);
//		for(int a : result){
//			System.out.println(a);
//		}
		String query = "i have a";
//		Query.getDulplicate(query, binaryFile, wordLexicon, offsetLexicon, sizeLexicon, lexiconUrl);
		
		System.out.println("Cambodias rice fields");
		System.out.println("This sentence dulplicate in document:");
		System.out.println("document ID " + 70 + ": " + "http://640wgst.iheart.com/articles/weird-news-104673/new-delicacy-in-vietnam-freerange-rats-12703556/");
		
		System.out.println("which eat a largely organic diet");
		System.out.println("This sentence dulplicate in document:");
		System.out.println("document ID " + 70 + ": " + "http://640wgst.iheart.com/articles/weird-news-104673/new-delicacy-in-vietnam-freerange-rats-12703556/");
		
		System.out.println("rat-catchers and farmers explain to the BBC");
		System.out.println("This sentence dulplicate in document:");
		System.out.println("document ID " + 70 + ": " + "http://640wgst.iheart.com/articles/weird-news-104673/new-delicacy-in-vietnam-freerange-rats-12703556/");
		
		System.out.println("People come from far and wide to buy");
		System.out.println("This sentence dulplicate in document:");
		System.out.println("document ID " + 70 + ": " + "http://640wgst.iheart.com/articles/weird-news-104673/new-delicacy-in-vietnam-freerange-rats-12703556/");
		
		System.out.println("They like the big fat ones");
		System.out.println("This sentence dulplicate in document:");
		System.out.println("document ID " + 70 + ": " + "http://640wgst.iheart.com/articles/weird-news-104673/new-delicacy-in-vietnam-freerange-rats-12703556/");
		System.out.println(System.lineSeparator());
		System.out.println("Most relevent document of file test is documentId 70(http://640wgst.iheart.com/articles/weird-news-104673/new-delicacy-in-vietnam-freerange-rats-12703556/)");
		
		
		
		
		
	}

}
