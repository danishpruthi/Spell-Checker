import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.StringTokenizer;

public class Filter {
	public static void BasicFilter() throws IOException{
		String finalString = "";
		try {			
			for (String line : Files.readAllLines(Paths.get(Constants.TEST_FILE))) {
				String delimiter = "\t ;,:.\n";
				
				StringTokenizer tokenizer = new StringTokenizer(line, delimiter,true);
				String prev = "<S>";
				
				while (tokenizer.hasMoreTokens()) {
					String curr = tokenizer.nextToken();					
					Boolean flag = false;
					//Checking if possible proper noum
					if (curr.length() >= 2) {
						if (curr.charAt(0) >= 'A' && curr.charAt(0) <= 'Z') {
							if (curr.charAt(1) >= 'a' && curr.charAt(1)<= 'z') {
								flag = true;
							}
						}
					}
					//If valid work, ignore the word
					if (Utils.isValidWord(curr) || flag)  {
						prev = curr;
						finalString += curr;
						continue;
					}
					// This condition checks and removes special characters. 
					// Retains full stops,commas and space
					if(curr.length() == 1 && !Character.isLetter(curr.charAt(0))){
						//Full stop
						char lastChar = finalString.charAt(finalString.length()-1);
						if(curr.charAt(0) ==lastChar){
							continue;
						}
						else{
							if(curr.charAt(0)=='.' || curr.charAt(0) == ',' || curr.charAt(0) == ' '){
								finalString += curr;
							}	
							if(curr.charAt(0) == '.')prev = "<S>";
						}
						continue;
					}
					//Control reaches here if word is wrong. We do edit distance,
					//and bigram count to get best match.
					List<String> candidates = Utils.getPossibleCandidates(curr);
					double maxScore = -1;
					String desiredCandidate = "";
					if (candidates.isEmpty()) {
						desiredCandidate = curr;
					}
					for (String candidate :  candidates) {
						if (desiredCandidate == "") {
							desiredCandidate = candidate;
							
						}
						double score = Utils.getTotalScore(candidate, prev); 
						if (score > maxScore) {
							maxScore = score;
							desiredCandidate = candidate;
						}							
					}
					//Add correct word to string.(Best candidates)
					finalString += desiredCandidate;
					prev = desiredCandidate;
					
				}
			}
			// Tokenize entire string based on full stops to get sentences.
			StringTokenizer separateSentences = new StringTokenizer(finalString,".");
			while (separateSentences.hasMoreTokens()) {
				String sentence = separateSentences.nextToken() + ".";
				sentence = sentence.trim();
				System.out.println(sentence);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
