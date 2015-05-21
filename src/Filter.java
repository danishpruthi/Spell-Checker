import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.StringTokenizer;

public class Filter {
	public static void BasicFilter() throws IOException{
		FileWriter fw = new FileWriter("out.txt");
		String finalString = "";
		try {
			for (String line : Files.readAllLines(Paths.get(Constants.TEST_FILE))) {
				String delimiter = "\t ;,:'";
				
				StringTokenizer tokenizer = new StringTokenizer(line, delimiter);
				String prev = "<S>";
				
				while (tokenizer.hasMoreTokens()) {
					String curr = tokenizer.nextToken();
					if (Utils.isValidWord(curr)) {
						prev = curr;
						finalString += curr + " ";
						continue;
					}
					List<String> candidates = Utils.getPossibleCandidates(curr);
					double maxScore = -1;
					String desiredCandidate = "";
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
					finalString += desiredCandidate + " ";
					prev = curr;
				}
				
				
				
				
//				
//				for(int i = 0; ; i++){
//					//System.out.println(words[i]);
//					words[i] = words[i].toLowerCase();
//					if(i == 0 || Utils.word_list.containsKey( words[i] ) ) { // || words[i].matches("[^a-zA-Z0-9 ]")){
//						//System.out.println("problem with " + words[i]);
//						continue;						
//					}						
//					else{
//						
//						words[i] = words[i].substring(1);
//						List<String> candidates = Utils.getPossibleCandidates(words[i]);
//						double max_score = -1;
//						String best_match = "";
//						for(int j = 0;j<candidates.size();j++){
//							double score = Utils.getTotalScore(candidates.get(j), words[i-1]);
//							if(score>max_score){
//								max_score = score;
//								best_match = candidates.get(j);
//							}
//						}
//						if(max_score != -1){
//							fw.write(words[i] + " " + best_match + " " + max_score + "\n");
//							//System.out.println(words[i] + " " + best_match + " " + max_score);
//							words[i] = best_match;							
//						}
//					}						
//				}
//				StringBuilder correctedTextBuilder = new StringBuilder();
//				for(String s : words) {
//					correctedTextBuilder.append(s+ " ");
//				}
//				String correctedTest = correctedTextBuilder.toString();
//				System.out.println(correctedTest);
			}
			System.out.println(finalString);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
