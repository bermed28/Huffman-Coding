package P3.Main;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import P3.DataStructures.List.List;
import P3.DataStructures.Map.Map;
import P3.DataStructures.Map.HashTable.HashTableSC;
import P3.DataStructures.Map.HashTable.SimpleHashFunction;
import P3.DataStructures.SortedList.SortedArrayList;
import P3.DataStructures.SortedList.SortedList;
import P3.DataStructures.Tree.BTNode;

public class HuffmanCoding {
	public static void main(String[] args) {
		run();
	}

	private static void run() {
		String data = loadData("huffman.txt");
		Map<String, Integer> fD = computeFD(data);
		BTNode<String, Integer> huffmanRoot = huffmanTree(fD);
		Map<String,Integer> encodedHuffman = huffmanCode(huffmanRoot);
		String output = encode(encodedHuffman);
		processResults(fD,data,output);
	
	}



	private static String loadData(String inputFile) {
		BufferedReader br = null;
		String line = "";
		
		try {
			
			br = new BufferedReader(new FileReader("inputFiles/" + inputFile));
			line = br.readLine();
			
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
			
		} catch (IOException e) {
			
			e.printStackTrace();
			
		} finally {
			if (br != null) 
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			
		}
		return line;
	}
	
	private static Map<String, Integer> computeFD(String data) {
		Map<String,Integer> ht = new HashTableSC<String,Integer>(data.length(), new SimpleHashFunction<String>());
		for (int i = 0; i < data.length(); i++) {
			String key = String.valueOf(data.charAt(i));
			if (ht.containsKey(key)) {
				ht.put(key, ht.get(key + 1));

			} else {
				ht.put(key, 1);
			}
		}
		return ht;
	}
	
	private static BTNode<String, Integer> huffmanTree(Map<String, Integer> fD) {
		SortedList<BTNode<String,Integer>> sL = new SortedArrayList<BTNode<String,Integer>>(fD.size());
		List<String> keys = fD.getKeys();
		List<Integer> vals = fD.getValues();
		
		for (int i = 0; i < fD.size(); i++) {
			BTNode<String, Integer> node = new BTNode<String, Integer>(keys.get(i), vals.get(i));
			sL.add(node);
		}
		
		for (int i = 1; i < fD.size() - 1; i++) {
			BTNode<String, Integer> n = new BTNode<String, Integer>();
			n.setLeftChild(sL.removeIndex(0));
			n.setRightChild(sL.removeIndex(0));
			n.setValue(n.getLeftChild().getValue() + n.getRightChild().getValue());
			n.setKey(n.getLeftChild().getKey() + n.getRightChild().getKey());
			sL.add(n);
		}
		return sL.get(0);
	}
	
	private static Map<String, Integer> huffmanCode(BTNode<String, Integer> huffmanRoot) {
		return null;
	}
	
	private static String encode(Map<String, Integer> encodedHuffman) {
		return "";
	}

	private static void processResults(Map<String, Integer> fD, String inputData, String outputData) {
		
	}
}
