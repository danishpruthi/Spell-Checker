import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Utils {
	// key = word, case sensitive; value = unigram count
	static Map<String, Long> unigram_count = new HashMap<String, Long>();
	
	// key = bigram, case sensitive; value = bigram count
	static Map<String, Long> bigram_count = new HashMap<String, Long>();
	
	// key : word, not case sensitive; value = true (always)
	static Map<String,Boolean> wordlist = new HashMap<String,Boolean>();

	
	public static List<String> getPossibleCandidates(String s) {
		// Edit Distance 1 and 2
		
		return new ArrayList<String>();
	}
	
	public static void loadModule() {
		populateWordList();
		populateUnigrams();
		populateBigrams();
		
	}
	private static void populateWordList()
	{
		try {
			for (String line : Files.readAllLines(Paths.get(Constants.WORDS_FILE))) {
				wordlist.put(line.toLowerCase(),true);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private static void populateUnigrams() {
		try {
			for (String line : Files.readAllLines(Paths.get(Constants.UNIGRAM_FILE))) {
				String words[] = line.split("	");
				if (words.length == 2) {
					String key = words[0];
					Long value = Long.parseLong(words[1]);
					unigram_count.put(key, value);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void populateBigrams() {
		try {
			for (String line : Files.readAllLines(Paths.get(Constants.BIGRAM_FILE))) {
				String words[] = line.split("	");
				if (words.length == 2) {
					String key = words[0];
					long value = Long.parseLong(words[1]);
					bigram_count.put(key, value);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static Boolean isValidWord(String word){
		word = word.toLowerCase();
		if(wordlist.containsKey(word)){
			return true;
		}
		return false;
	}
	public static long getUnigramCount(String unigram) {
		if (unigram_count.containsKey(unigram)) {
			return (long)unigram_count.get(unigram);
		} else {
			
			// TODO(danish): smooth the model. Returning zero as of now.
			return 0;
		}
	}
	
	public static long getBigramCount(String bigram) {
		if (bigram_count.containsKey(bigram)) {
			return (long)bigram_count.get(bigram);
		} else {
			
			// TODO(danish): smooth the model. Returning zero as of now.
			return 0;
		}
	}
	
	public static double getTotalScore(String canddiate) {
		return 0.0;
	}
	
	
	// gets the language model score for a given candidate
	public static double getLMScore(String candidate) {
		return 0.0;
	}
	
	
}
