import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

//good to go
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
	static Map<String, String> short_hand_crrections = new HashMap<String, String>();
	
	static Set<String> ignore_words = new HashSet<String>();
	
	// key: incorrect word; value = list of possible corrections
	static Map<String, List<String> > corrections = new HashMap<String, List<String> >();
	
	static long total_bigrams = 0;
	static long total_unigrams = 0;
	
	// experimental value... usually taken 0.5 or 0.75
	static double discount = 0.75;
	
//	private static Boolean Is1EditDistance(String s, String s2){
//		for (int j = 0; j < s.length() && j < s2.length(); j++){
//            if (s.charAt(j) != s2.charAt(j)){
//                return s.substring(j + 1).equals(s2.substring(j + 1))    
//                    || s.substring(j + 1).equals(s2.substring(j))     
//                    || s.substring(j).equals(s2.substring(j + 1)) ;      
//            }
//        }
//        return Math.abs(s.length() - s2.length()) == 1;
//	}
	//Gets all possible ED1 words including non dictionary words
	public static List<String> getPossibleED1Candidates(String s){
		if(s.length() == 0)
			return null;
		List<String> candidates = new ArrayList<>();
		String copy = s;
		//Inserting character
		for(int i = 0;i <=s.length();i++){
			for(int j = 0;j<26;j++){
				copy = copy.substring(0, i) + Character.toChars(j+97)[0] + copy.substring(i, copy.length());
				candidates.add(copy);
				copy = s;
			}
		}
		//Replacing character
		StringBuilder str = new StringBuilder(s);
		for(int i = 0;i <s.length();i++){
			for(int j = 0;j<26;j++){
				str.setCharAt(i, Character.toChars(j+97)[0]);
				candidates.add(str.toString());
				str.setCharAt(i, s.charAt(i));
			}
		}
		// No point deleting single letter words
		if(s.length() == 1)
			return candidates;
		//Deleting character
		for(int i = 0;i<s.length();i++)
		{
			StringBuilder temp = new StringBuilder(s);
			temp.deleteCharAt(i);
			candidates.add(temp.toString());
		}
		return candidates;
	}	
	
	//Gets all possible ED2 words including non dictionary words
	public static Set<String> getPossibleED2Candidates(String s){
		List<String> ED1 = getPossibleED1Candidates(s);
		//List<String> candidates = new ArrayList<String>();
		Set<String> candidates = new TreeSet<String>();
		for(String item : ED1){
			List<String> temp = getPossibleED1Candidates(item);
			candidates.addAll(temp);
		}
		return candidates;
		
	}
	
	//Gets ED1 with proper words only
	public static List<String> getProperED1Candidates(String s){
		List<String> allCandidates = getPossibleED1Candidates(s);
		List<String> properCandidates = new ArrayList<String>();
		for(String item : allCandidates){
			Boolean isWord = word_list.containsKey(item);
			if(isWord)
				properCandidates.add(item);				
		}
		return properCandidates;
	}
	
	//Gets ED2 with proper words only
	public static List<String> getProperED2Candidates(String s){
		Set<String> allCandidates = getPossibleED2Candidates(s);
		List<String> properCandidates = new ArrayList<String>();
		for(String item : allCandidates){
			Boolean isWord = word_list.containsKey(item);
			if(isWord)
				properCandidates.add(item);				
		}
		return properCandidates;
	}
//	public static List<String> getPossibleCandidates(String s) {
//		// Edit Distance 1
//		ArrayList<String> candidates = new ArrayList<String>();
//		for(int i =0;i<words.size();i++){
//			if(Is1EditDistance(s, words.get(i))){
//				candidates.add(words.get(i));
//			}			
//		}
//		return candidates;
//	}
	
	public static void loadModule() {
		populateWordList();
		populateUnigrams();
		populateBigrams();
		populateIgnoreWords();

		populateCommonMistakes();
		printMistakes();

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
	private static void populateIgnoreWords() {
		try {
			for (String line : Files.readAllLines(Paths.get(Constants.IGNORE_FILE))) {
				line = line.trim();
				if (line.length() >= 2) {
					if (line.substring(0, 2).compareTo("//") == 0) {
						continue;
					} else {
						ignore_words.add(line);
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// used for only testing, else obsolete
	private static void printIgnoreWords() {
		
		Object[] list = ignore_words.toArray();
		for (int i = 0; i < list.length; i++) {
			System.out.println(list[i]);
		}		
	}
	
	public static boolean isIgnoreWord(String s) {
		return ignore_words.contains(s);
	}
	
	// to populate common mistakes
	private static void populateCommonMistakes() {
		try {
			for (String line : Files.readAllLines(Paths.get(Constants.CORRECTION_FILE))) {
				line = line.trim();
				String[] split_line = line.split(":");
				if (split_line.length != 2) {
					continue;
				} 
				
				String correct = split_line[0].trim();
				String[] mistakes = split_line[1].split(",");
				for (String mistake: mistakes) {
					mistake = mistake.trim();
					if (corrections.containsKey(mistake)) {
						corrections.get(mistake).add(correct);
					} else {
						List<String> start_list = new ArrayList<String>();
						start_list.add(correct);
						corrections.put(mistake, start_list);
					}
				}
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// only for testing
	private static void printMistakes() {
		System.out.println(corrections.size());
		List<String> l = corrections.get("bout");
		for (String element: l) {
			System.out.println(element);
		}
	}
	
	public static boolean isCommonMistake(String s) {
		return corrections.containsKey(s);
	}
	
	public static List<String> getSuggestions(String s) {
		return corrections.get(s);
	}
	
}
