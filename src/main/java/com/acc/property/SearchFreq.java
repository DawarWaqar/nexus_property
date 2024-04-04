package com.acc.property;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/*
 * Class SearchFreq  
 * 
 * caseSensitive:	boolean, for search word  * 
 * 
 * addsearchword:
 * param:	String searchcword
 *  
 * getAllSearchTimes: current search word times in first line,others history words is sorted from max to min
 * param:	String searchword
 * return:	List<String>
 * 
 * getKeywordSearchTimes(String searchword)
 * return:	int 
 */

public class SearchFreq {
	public boolean caseSensitive=false;
	Map<String, Integer> searchedhashMap = new HashMap<>();
	private SearchFreq() {
		caseSensitive=false;		
	}
	private SearchFreq(boolean case_sensitive) {
		caseSensitive=case_sensitive;		
	}

	public void addsearchword(String searchword) {
		if (!caseSensitive) {
			searchword=searchword.toLowerCase();
		}
		if (searchedhashMap.containsKey(searchword)) {
			searchedhashMap.put(searchword, searchedhashMap.get(searchword)+1);
		}
		else {
			searchedhashMap.put(searchword,1);
		}
	}
	
	public int getKeywordSearchTimes(String searchword) {
		if (!caseSensitive) {
			searchword=searchword.toLowerCase();
		}
		int his_times=0;
		MaxHeap myheap = new MaxHeap();		
		//get current search word times in first
		if (searchedhashMap.containsKey(searchword)) {
			his_times=searchedhashMap.get(searchword);
		}
		return his_times;
	}	
	
	public List<String> getAllSearchTimes(String searchword) {
		List<String> showList=new ArrayList<>();
		if (!caseSensitive) {
			searchword=searchword.toLowerCase();
		}

		MaxHeap myheap = new MaxHeap();		
		//get current search word times in first
		if (searchedhashMap.containsKey(searchword)) {
        	String s;
        	s=String.format("%-20s  %d times",searchword,searchedhashMap.get(searchword));
        	showList.add(s);
		}
   	    //Create maxHeap for sort,current search word in first,other history searched words shows in order max->min
 	    for (Map.Entry<String, Integer> entry : searchedhashMap.entrySet()) {
	        WordFreq wordFreq = new WordFreq(entry.getKey(), entry.getValue());
	        myheap.insert(wordFreq);
	    } 

 	    while (true) {
	        WordFreq wordFreq = myheap.extractMax();
	        if (wordFreq == null) {				//if heap is empty then break output!
	        		break;				
	        	}
	        else {
	        	String s;
	        	s=String.format("%-20s  %d times",wordFreq.word,wordFreq.freq);
	        	if (!wordFreq.word.equals(searchword))
	        		showList.add(s);	            
	        }
	    } 
 	    return showList;
	}
	
	public void clear() {
		searchedhashMap.clear();
	}	
	
	public void savetofile(String filename) {
        try (ObjectOutputStream outstream = new ObjectOutputStream(new FileOutputStream(filename))) {
        	outstream.writeObject(searchedhashMap);
        } catch (IOException e) {
            System.out.println("save search history file error! " + e.getMessage());
        }	
	}
	
	public void loadfromfile(String filename) {
		File file = new File(filename);
		if (file.exists()) 
		{
	        try (ObjectInputStream inputstream = new ObjectInputStream(new FileInputStream(filename))) {
	        	searchedhashMap = (Map<String, Integer>) inputstream.readObject();
	        } catch (IOException | ClassNotFoundException e) {
	            System.out.println("read search history file error! " + e.getMessage());
	        }
		}
	}	
	public static void main(String[] args, String searchKeyword) {
		List <String> resultlist=new ArrayList<>();
		SearchFreq sf=new SearchFreq();
		sf.loadfromfile("search_history.his");
		
		sf.addsearchword(searchKeyword);

		
		System.out.println("Word:" + searchKeyword +" times="+sf.getKeywordSearchTimes(searchKeyword));
		sf.savetofile("search_history.his");

	}
	
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////
/*  follow is support class
 * 
 */
class WordFreq 
{
    int freq;
    String word;
    WordFreq(String word, int freq) {
        this.freq = freq;
    	this.word = word;
    }
}

class MaxHeap {
    List<WordFreq> heap;

    MaxHeap() {
        heap = new ArrayList<>();
    }
    //my own reset function
    void reset() {
    	heap.clear();
    }

    void insert(WordFreq wordFreq) {
        heap.add(wordFreq);
        int index = heap.size() - 1;
        bubbleUp(index);
    }

    void bubbleUp(int index) {
        while (index > 0) {
            int parentIndex = (index - 1) / 2;
            if (heap.get(index).freq > heap.get(parentIndex).freq) {
                // Swap
                Collections.swap(heap, index, parentIndex);
                index = parentIndex;
            } else {
                break;
            }
        }
    }

    WordFreq extractMax() {
        if (heap.isEmpty())
            return null;
        WordFreq max = heap.get(0);
        WordFreq last = heap.remove(heap.size() - 1);

        if (!heap.isEmpty()) {
            heap.set(0, last);
            bubbleDown(0);
        }
        return max;
    }

    void bubbleDown(int index) {
        int size = heap.size();
        while (index < size) {
            int leftChildIdx = 2 * index + 1;
            int rightChildIdx = 2 * index + 2;
            int largest = index;

            if (leftChildIdx < size && heap.get(leftChildIdx).freq > heap.get(largest).freq) {
                largest = leftChildIdx;
            }

            if (rightChildIdx < size && heap.get(rightChildIdx).freq > heap.get(largest).freq) {
                largest = rightChildIdx;
            }

            if (largest != index) {
                Collections.swap(heap, index, largest);
                index = largest;
            } else {
                break;
            }
        }
    }
}
