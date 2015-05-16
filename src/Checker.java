import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Checker {

	public static void main(String[] args) throws IOException {
		Utils.loadModule();
		System.out.println(Utils.getBigramCount("The day"));
		System.out.println(Utils.getUnigramCount("amazing"));
		System.out.println(Utils.getUnigramCount("day"));
		System.out.println(Utils.isValidWord("super"));
		
		
		// candidate, previous word
		// for the query "believe my"
		System.out.println(Utils.getTotalScore("neverssssss", "believe"));
		// for the query "believe me"
		System.out.println(Utils.getTotalScore("me", "believe"));
		// for the query "believe mine"
		System.out.println(Utils.getTotalScore("mine", "believe"));
		Utils.getPossibleCandidates("carrot");
		
		Filter.BasicFilter();
	}

}
