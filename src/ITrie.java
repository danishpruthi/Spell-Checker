
public interface ITrie {
	void addWords(String word);
	int getLongestMatch(String word);
	boolean contains(String word);
}
