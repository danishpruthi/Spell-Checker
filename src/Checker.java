import java.io.IOException;


public class Checker {

	public static void main(String[] args) throws IOException {
		Utils.loadModule();
		Filter.BasicFilter();
		
		
//		System.out.println(Utils.getBigramCount("The day"));
//		System.out.println(Utils.getUnigramCount("amazing"));
//		System.out.println(Utils.getUnigramCount("day"));
//		System.out.println(Utils.isValidWord("super"));
//		
//		// candidate, previous word
//		// for the query "believe my"
//		System.out.println(Utils.getTotalScore("my", "believe"));
//		// for the query "believe me"
//		System.out.println(Utils.getTotalScore("me", "believe"));
//		// for the query "believe mine"
//		System.out.println(Utils.getTotalScore("mine", "believe"));
		


		// candidate, previous word
		// for the query "believe my"
		System.out.println(Utils.getTotalScore("my", "believe"));
		// for the query "believe me"
		System.out.println(Utils.getTotalScore("me", "believe"));
		// for the query "believe mine"
		System.out.println(Utils.getTotalScore("mine", "believe"));
		Filter.BasicFilter();

	}

}
