
public class Trie implements ITrie {
	private TrieNode root;
	public Trie(){
		this.root = new TrieNode();
	}
	public void addWord(String word){
		root.addWord(word.toLowerCase());
	}
}
