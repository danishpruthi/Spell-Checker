import java.util.ArrayList;
import java.util.List;

public class TrieNode {
	private TrieNode parent;
	private TrieNode children[];
	private char character;
	
	public TrieNode(){
		Children = new TrieNode[26];
	}
	public TrieNode(char letter){
		this.character = letter;
		children = new TrieNode[26];
	}
	public void AddWord(String word){
		if(word.isEmpty()){
			return ;
		}
		int position = word.charAt(0) - 'a';
		
		if(children[position] == null){
			children[position] = new TrieNode(word.charAt(0));
			children[position].parent = this;			
		}
		if(word.length() > 1){
			children[position].AddWord(word.substring(1));
		}	
	}
	private TrieNode getNode(char letter){
		return children[letter-'a'];
	}
	
}
