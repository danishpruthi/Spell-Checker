import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;




public class Utils {
	// key = word, case sensitive; value = unigram count
	static Map<String, Long> unigram_count = new HashMap<String, Long>();
	
	// key = bigram, case sensitive; value = bigram count
	static Map<String, Long> bigram_count = new HashMap<String, Long>();
	
	// key : word, not case sensitive; value = true (always)
	static Map<String,Boolean> word_list = new HashMap<String,Boolean>();
	
	// key : word, case sensitive; value = number of unique words that can precede it
	static Map<String, Long> continuation_count = new HashMap<String, Long>();
	
	// key : word, case sensitive; value = number of unique words that can follow it
	static Map<String, Long> first_word_count = new HashMap<String, Long>();
	
	static ArrayList<String> words = new ArrayList<String>();

	static long total_bigrams = 0;
	static long total_unigrams = 0;
	
	// experimental value... usually taken 0.5 or 0.75
	static double discount = 0.75;
	
	private static Boolean Is1EditDistance(String s, String s2){
		for (int j = 0; j < s.length() && j < s2.length(); j++){
            if (s.charAt(j) != s2.charAt(j)){
                return s.substring(j + 1).equals(s2.substring(j + 1))    
                    || s.substring(j + 1).equals(s2.substring(j))     
                    || s.substring(j).equals(s2.substring(j + 1)) ;      
            }
        }
        return Math.abs(s.length() - s2.length()) == 1;
	}
	public static List<String> getPossibleCandidates(String s) {
		// Edit Distance 1
		ArrayList<String> candidates = new ArrayList<String>();
		for(int i =0;i<words.size();i++){
			if(Is1EditDistance(s, words.get(i))){
				candidates.add(words.get(i));
			}			
		}
		return candidates;
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
				word_list.put(line.toLowerCase(),true);
				words.add(line.toLowerCase());
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
					total_unigrams += value + 1;
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
					String key_elements[] = key.split(" ");
					
					if (key_elements.length == 2) {
						if (!continuation_count.containsKey(key_elements[1])) {
							continuation_count.put(key_elements[1], (long) 1);
						} else {
							continuation_count.put(key_elements[1], continuation_count.get(
									key_elements[1]) + 1);
						}
						
						if (!first_word_count.containsKey(key_elements[0])){
							first_word_count.put(key_elements[0], (long) 1);
						} else {
							first_word_count.put(key_elements[0], first_word_count.get(
									key_elements[0]) + 1);
						}
					}
					
					long value = Long.parseLong(words[1]);
					bigram_count.put(key, value);
					total_bigrams += value + 1;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	public static Boolean isValidWord(String word){
		word = word.toLowerCase();
		if(word_list.containsKey(word)){
			return true;
		}
		return false;
	}
	public static long getUnigramCount(String unigram) {
		if (unigram_count.containsKey(unigram)) {
			return (long)unigram_count.get(unigram) + 1;
		} else {
			
			// add-1 smoothing.
			return 1;
		}
	}
	
	public static long getBigramCount(String bigram) {
		if (bigram_count.containsKey(bigram)) {
			return (long)bigram_count.get(bigram) + 1;
		} else {
			// applied add-1 smoothing.
			return 1;
		}
	}
	
	public static double getTotalScore(String candidate, String prev) {
		// TODO: Right now the score is just based on Language model which is P(C), later we'll
		// also account for P(X|C) 
		return getLMScore(candidate, prev);
	}
	
	
	// Kneser - Nay Smoothing 
	// https://www.dropbox.com/s/ypdjrjwwry8xw32/Kneser-Nay.png?dl=0
	// gets the language model score for a given candidate
	// TODO (danish): need to review the logic once again.
	public static double getLMScore(String candidate, String prev) {
		String bigram = prev + " " + candidate;
		double answer = 0;
		
		answer = (double)Math.max(getBigramCount(bigram) - discount, 0)/getUnigramCount(prev);
		answer += getNormalizedDiscount(prev) * getContinuationProbability(candidate);
		
		return answer;
		
	}
	
	// https://www.dropbox.com/s/ypdjrjwwry8xw32/Kneser-Nay.png?dl=0
	static double getNormalizedDiscount (String p1) { 
		if (first_word_count.containsKey(p1)) {
			return (discount) * first_word_count.get(p1)/ getUnigramCount(p1);
		}
		
		return 0.0;
	}
	
	// https://www.dropbox.com/s/000ybtyshgmq8u4/Continuation_Prob.png?dl=0
	static double getContinuationProbability(String candidate) {
		
		if (total_bigrams != 0 && continuation_count.containsKey(candidate)) {
			return (continuation_count.get(candidate))/(double)total_bigrams;
		}
		
		return 0.0;
	}
}
