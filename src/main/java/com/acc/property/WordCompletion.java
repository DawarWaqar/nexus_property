package com.acc.property;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordCompletion {

	public class AG_Trie {
		Map<Character, AG_Trie> ag_ward;
		char ag_char;
		boolean ag_str_word;

		public AG_Trie(char ag_char) {
			this.ag_char = ag_char;
			ag_ward = new HashMap<>();
		}

		public AG_Trie() {
			ag_ward = new HashMap<>();
		}

		public void ag_add_insert(String ag_char_string) {
			if (ag_char_string == null || ag_char_string.isEmpty())
				return;
			char ag_starting_char = ag_char_string.charAt(0);
			AG_Trie ag_child = ag_ward.get(ag_starting_char);
			if (ag_child == null) {
				ag_child = new AG_Trie(ag_starting_char);
				ag_ward.put(ag_starting_char, ag_child);
			}

			if (ag_char_string.length() > 1)
				ag_child.ag_add_insert(ag_char_string.substring(1));
			else
				ag_child.ag_str_word = true;
		}

	}

	AG_Trie ag_root;

	public WordCompletion(List<String> ag_words) {
		ag_root = new AG_Trie();
		for (String ag_char_string : ag_words) {
			if (ag_char_string != null) {
				ag_root.ag_add_insert(ag_char_string.toLowerCase());
				ag_root.ag_add_insert(removeLeadingNumbers(ag_char_string.toLowerCase()));
				String[] wordSubArray = ag_char_string.split(",");
				for (String subWord : wordSubArray) {
					ag_root.ag_add_insert(removeLeadingNumbers(subWord.toLowerCase().trim()));
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

	public boolean af_find(String ag_prefix, boolean exact) {
		AG_Trie lastNode = ag_root;
		for (char ag_char : ag_prefix.toCharArray()) {
			lastNode = lastNode.ag_ward.get(ag_char);
			if (lastNode == null)
				return false;
		}
		return !exact || lastNode.ag_str_word;
	}

	public boolean af_find(String ag_prefix) {
		return af_find(ag_prefix, false);
	}

	public void ag_suggest_same_words(AG_Trie ag_root, List<String> ag_l_list, StringBuffer ag_current_node, String ag_prefix) {
		if (ag_root.ag_str_word && !ag_current_node.toString().equals(ag_prefix)) {
			ag_l_list.add(ag_current_node.toString());
		}

		if (ag_root.ag_ward == null || ag_root.ag_ward.isEmpty())
			return;

		for (AG_Trie ag_child : ag_root.ag_ward.values()) {
			ag_suggest_same_words(ag_child, ag_l_list, ag_current_node.append(ag_child.ag_char), ag_prefix);
			ag_current_node.setLength(ag_current_node.length() - 1);
		}
	}

	public List<String> ag_suggest_k(String ag_prefix) {
		List<String> ag_l_list = new ArrayList<>();
		AG_Trie lastNode = ag_root;
		StringBuffer ag_current_node = new StringBuffer();
		for (char ag_char : ag_prefix.toCharArray()) {
			lastNode = lastNode.ag_ward.get(ag_char);
			if (lastNode == null)
				return ag_l_list;
			ag_current_node.append(ag_char);
		}
		ag_suggest_same_words(lastNode, ag_l_list, ag_current_node, ag_prefix);
		return ag_l_list;
	}

	public static List<String> main(ArrayList<String> args, String ag_prefix) {
		WordCompletion ag_trie = new WordCompletion(args);
		List<String> ag_complete_suggestion = ag_trie.ag_suggest_k(ag_prefix.toLowerCase());
		return ag_complete_suggestion;

	}

}