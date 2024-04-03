package com.acc.property;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordCompletion {

	public class TrieNode {
		Map<Character, TrieNode> children;
		char c;
		boolean isWord;

		public TrieNode(char c) {
			this.c = c;
			children = new HashMap<>();
		}

		public TrieNode() {
			children = new HashMap<>();
		}

		public void insert(String word) {
			if (word == null || word.isEmpty())
				return;
			char firstChar = word.charAt(0);
			TrieNode child = children.get(firstChar);
			if (child == null) {
				child = new TrieNode(firstChar);
				children.put(firstChar, child);
			}

			if (word.length() > 1)
				child.insert(word.substring(1));
			else
				child.isWord = true;
		}

	}

	TrieNode root;

	public WordCompletion(List<String> words) {
		root = new TrieNode();
		for (String word : words) {
			if (word != null) {
				root.insert(word.toLowerCase());
				root.insert(removeLeadingNumbers(word.toLowerCase()));
				String[] wordSubArray = word.split(",");
				for (String subWord : wordSubArray) {
					root.insert(removeLeadingNumbers(subWord.toLowerCase().trim()));
				}
			}
		}
	}
	
	 public String removeLeadingNumbers(String input) {
	        // Regular expression to match leading digits
	        String regex = "^\\d+"; // ^ asserts position at start of the string, \d+ matches one or more digits

	        // Replace leading digits with an empty string
	        String result = input.replaceAll(regex, "").trim();

	        return result;
	    }

	public boolean find(String prefix, boolean exact) {
		TrieNode lastNode = root;
		for (char c : prefix.toCharArray()) {
			lastNode = lastNode.children.get(c);
			if (lastNode == null)
				return false;
		}
		return !exact || lastNode.isWord;
	}

	public boolean find(String prefix) {
		return find(prefix, false);
	}

	public void suggestHelper(TrieNode root, List<String> list, StringBuffer curr, String prefix) {
		if (root.isWord && !curr.toString().equals(prefix)) {
			list.add(curr.toString());
		}

		if (root.children == null || root.children.isEmpty())
			return;

		for (TrieNode child : root.children.values()) {
			suggestHelper(child, list, curr.append(child.c), prefix);
			curr.setLength(curr.length() - 1);
		}
	}

	public List<String> suggest(String prefix) {
		List<String> list = new ArrayList<>();
		TrieNode lastNode = root;
		StringBuffer curr = new StringBuffer();
		for (char c : prefix.toCharArray()) {
			lastNode = lastNode.children.get(c);
			if (lastNode == null)
				return list;
			curr.append(c);
		}
		suggestHelper(lastNode, list, curr, prefix);
		return list;
	}

	public static List<String> main(ArrayList<String> args, String prefix) {
		WordCompletion trie = new WordCompletion(args);
		List<String> completions = trie.suggest(prefix.toLowerCase());
		return completions;

	}

}