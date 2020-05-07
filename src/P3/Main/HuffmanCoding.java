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
import P3.DataStructures.Tree.utils.BinaryTreePrinter;

public class HuffmanCoding {
	public static void main(String[] args) {
		run();
	}

	private static void run() {
		String data = loadData("huffman.txt");
		Map<String, Integer> fD = computeFD(data);
		BTNode<String, Integer> huffmanRoot = huffmanTree(fD);
		Map<String,String> encodedHuffman = huffmanCode(huffmanRoot);
		String output = encode(encodedHuffman);
		processResults(fD,data,output);

	}



	public static String loadData(String inputFile) {
		BufferedReader br = null;
		String line = "";

		try {

			br = new BufferedReader(new FileReader("inputData/" + inputFile));
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

	public static Map<String, Integer> computeFD(String data) {
		Map<String,Integer> ht = new HashTableSC<String,Integer>(data.length(), new SimpleHashFunction<String>());
		for (int i = 0; i < data.length(); i++) {
			char key = data.charAt(i);
			if (ht.containsKey(String.valueOf(key))) {
				ht.put(String.valueOf(key), ht.get(String.valueOf(key)) + 1);

			} else {
				ht.put(String.valueOf(key), 1);
			}
		}
		return ht;
	}

	public static BTNode<String, Integer> huffmanTree(Map<String, Integer> fD) {
		int size = fD.size();
		boolean isEmpty =true;
		SortedList<BTNode<String,Integer>> sL = new SortedArrayList<BTNode<String,Integer>>(size);
		List<String> keys = fD.getKeys();
		List<Integer> vals = fD.getValues();

		for (int i = 0; i < fD.size(); i++) {
			BTNode<String, Integer> node = new BTNode<String, Integer>(keys.get(i), vals.get(i));
			sL.add(node);
		}

		for (int i = 1; i <= size - 1; i++) {
			BTNode<String, Integer> n = new BTNode<String, Integer>(); 

			BTNode<String, Integer> x = minFreq(sL);
			BTNode<String, Integer> y = minFreq(sL);

			if(isEmpty) {
				n.setLeftChild(x);
				n.setRightChild(y);
				isEmpty = false;
			} else {
				
				if(x.getValue() == y.getValue()) {
					if (x.getKey().compareTo(y.getKey()) < 0) {
						n.setLeftChild(y);
						n.setRightChild(x);
					}
				} else {
					n.setLeftChild(x);
					n.setRightChild(y);
				}
			}
			n.setValue(x.getValue() + y.getValue());
			n.setKey(x.getKey() + y.getKey());
			sL.add(n);
		}

		BTNode<String, Integer> nodeToReturn = minFreq(sL);
		System.out.println("Root of Huffman Tree: {" + nodeToReturn.getKey() + "," + nodeToReturn.getValue()+"}");
		System.out.println("Huffman Tree: ");
		BinaryTreePrinter.print(nodeToReturn);


		return nodeToReturn;

	}

	private static BTNode<String, Integer> minFreq(SortedList<BTNode<String, Integer>> sL) {
		int smallest = Integer.MAX_VALUE;
		BTNode<String,Integer> smallNode = null;

		for (int i = 0; i < sL.size(); i++) {
			if (sL.get(i).getValue() < smallest) {
				smallNode = sL.get(i);
				smallest = smallNode.getValue();

			}
		}

		for (int i = 0; i < sL.size(); i++) {
			if (smallNode.getKey().compareTo(sL.get(i).getKey()) == 0) {
				sL.removeIndex(i);
			}
		}

		return smallNode;
	}

	public static Map<String, String> huffmanCode(BTNode<String, Integer> huffmanRoot) {
		Map<String, String> ht= new HashTableSC<String, String>(new SimpleHashFunction<String>());
		ht = recTraversal(huffmanRoot, ht);
		ht.print(System.out);
		return ht;
	}

	private static Map<String,String> recTraversal(BTNode<String, Integer> huffmanRoot, Map<String, String> ht) {
		if(huffmanRoot == null) return ht;
		else {
			
			recTraversal(huffmanRoot.getLeftChild(), ht);
			recTraversal(huffmanRoot.getRightChild(), ht);
		}
		return ht;
	}

	

	public static String encode(Map<String, String> encodedHuffman) {
		String result = "";
		List<String> vals = encodedHuffman.getValues();
		for (String string : vals) {
			result += string;
		}
		return result;
	}

	public static void processResults(Map<String, Integer> fD, String inputData, String outputData) {

	}
}
